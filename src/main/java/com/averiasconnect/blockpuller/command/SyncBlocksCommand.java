package com.averiasconnect.blockpuller.command;

import com.averiasconnect.blockpuller.external.client.ScanWebClient;
import com.averiasconnect.blockpuller.external.model.Block;
import com.averiasconnect.blockpuller.model.BlockEntity;
import com.averiasconnect.blockpuller.repository.BlockRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SyncBlocksCommand {
  private final ScanWebClient client;
  private final BlockRepository blockRepository;
  private final ProcessBlockCommand processBlockCommand;

  public SyncBlocksCommand(
      ScanWebClient client,
      BlockRepository blockRepository,
      ProcessBlockCommand processBlockCommand) {
    this.client = client;
    this.blockRepository = blockRepository;
    this.processBlockCommand = processBlockCommand;
  }

  public void sync() {
    Block mostRecentBlock = this.client.getBlockByNumber(null).block();
    if (mostRecentBlock == null) {
      log.error("Most recent block was not found");
      return;
    }

    BlockEntity lastStoredBlock = blockRepository.findLatest(mostRecentBlock.network);
    if (lastStoredBlock == null) {
      BlockEntity storedBlock = processBlockCommand.process(mostRecentBlock);
      log.info("Most recent block {} has been stored as a first block", storedBlock.getId());
      return;
    }

    long blockOffset = lastStoredBlock.getId() + 1L - mostRecentBlock.getBlockNumberAsLong();
    if (blockOffset == 0L) {
      BlockEntity storedBlock = processBlockCommand.process(mostRecentBlock);
      log.info("Most recent block {} has been stored", storedBlock.getId());
    } else if (blockOffset < 0L) {
      syncPreviousMissedBlocks(lastStoredBlock, mostRecentBlock);
    } else {
      log.info(
          "Most recent block {} has not been stored, it already exists",
          mostRecentBlock.getBlockNumberAsLong());
    }
  }

  private void syncPreviousMissedBlocks(BlockEntity lastStoredBlock, Block mostRecentBlock) {
    this.client
        .getDelayedBlocksRange(lastStoredBlock.getId() + 1L, mostRecentBlock.getBlockNumberAsLong())
        .doOnNext(processBlockCommand::process)
        .doOnNext(
            storedBlock -> log.info("Block {} has been stored", storedBlock.getBlockNumberAsLong()))
        .doOnError(e -> log.error("Error when syncing blocks range: {}", e.toString()))
        .blockLast();
  }
}

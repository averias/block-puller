package com.averiasconnect.blockpuller.command;

import com.averiasconnect.blockpuller.exception.TransactionsProducerException;
import com.averiasconnect.blockpuller.external.model.Block;
import com.averiasconnect.blockpuller.external.model.Transaction;
import com.averiasconnect.blockpuller.model.BlockEntity;
import com.averiasconnect.blockpuller.producer.TransactionsProducerToggle;
import com.averiasconnect.blockpuller.repository.BlockRepository;
import com.averiasconnect.blockpuller.service.converter.BlockConverter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ProcessBlockCommand {
  private final BlockRepository blockRepository;
  private final BlockConverter blockConverter;
  private final TransactionsProducerToggle transactionsProducerToggle;

  public BlockEntity process(Block block) {
    List<Transaction> transactions =
        block.transactions.stream().map(tx -> tx.setTimestamp(block.timestamp)).toList();
    try {
      transactionsProducerToggle
          .getProducer()
          .sendTransactions(transactions)
          .doOnNext(
              count ->
                  log.info(
                      "Processed {} transactions of {} included in the block {}",
                      count,
                      transactions.size(),
                      block.number))
          .subscribe();
    } catch (TransactionsProducerException e) {
      log.error(
          "Transactions for block number {} were not sent, block will be saved anyway. Reason: {}",
          block.number,
          e.toString());
    }

    return blockRepository.save(blockConverter.convertToEntity(block));
  }
}

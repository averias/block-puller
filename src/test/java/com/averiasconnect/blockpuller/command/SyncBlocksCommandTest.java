package com.averiasconnect.blockpuller.command;

import com.averiasconnect.blockpuller.external.client.ScanWebClient;
import com.averiasconnect.blockpuller.external.model.Block;
import com.averiasconnect.blockpuller.model.BlockEntity;
import com.averiasconnect.blockpuller.repository.BlockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SyncBlocksCommandTest {

    @Mock
    private ScanWebClient client;

    @Mock
    private BlockRepository blockRepository;

    @Mock
    private ProcessBlockCommand processBlockCommand;

    @InjectMocks
    private SyncBlocksCommand syncBlocksCommand;

    @Test
    void syncBlocks_whenMostRecentBlockNotFound() {
        // Given
        when(client.getBlockByNumber(null)).thenReturn(Mono.empty());

        // When
        syncBlocksCommand.sync();

        // Then
        verify(client).getBlockByNumber(null);
        verifyNoInteractions(blockRepository, processBlockCommand);
    }

    @Test
    void syncBlocks_whenItIsTheFirstBlock() {
        // Given
        Block mostRecentBlock = new Block();
        mostRecentBlock.network = "ethereum";
        mostRecentBlock.number = "0x1";
        when(client.getBlockByNumber(null)).thenReturn(Mono.just(mostRecentBlock));
        when(blockRepository.findLatest("ethereum")).thenReturn(null);
        BlockEntity storedBlock = new BlockEntity();
        storedBlock.setId(1L);
        when(processBlockCommand.process(mostRecentBlock)).thenReturn(storedBlock);

        // When
        syncBlocksCommand.sync();

        // Then
        verify(client).getBlockByNumber(null);
        verify(blockRepository).findLatest("ethereum");
        verify(processBlockCommand).process(mostRecentBlock);
        verifyNoMoreInteractions(client, blockRepository, processBlockCommand);
    }

    @Test
    void syncBlocks_whenBlockStoredIsTheMostRecentBlock() {
        // Given
        Block mostRecentBlock = new Block();
        mostRecentBlock.network = "ethereum";
        mostRecentBlock.number = "0x3";
        BlockEntity lastStoredBlock = new BlockEntity();
        lastStoredBlock.setId(2L);
        lastStoredBlock.setNetwork("ethereum");
        when(client.getBlockByNumber(null)).thenReturn(Mono.just(mostRecentBlock));
        when(blockRepository.findLatest("ethereum")).thenReturn(lastStoredBlock);
        BlockEntity storedBlock = new BlockEntity();
        storedBlock.setId(3L);
        when(processBlockCommand.process(mostRecentBlock)).thenReturn(storedBlock);

        // When
        syncBlocksCommand.sync();

        // Then
        verify(client).getBlockByNumber(null);
        verify(blockRepository).findLatest("ethereum");
        verify(processBlockCommand).process(mostRecentBlock);
        verifyNoMoreInteractions(client, blockRepository, processBlockCommand);
    }

    @Test
    void syncBlocks_whenMostRecentBlockAlreadyStored() {
        // Given
        Block mostRecentBlock = new Block();
        mostRecentBlock.network = "ethereum";
        mostRecentBlock.number = "0x2";
        BlockEntity lastStoredBlock = new BlockEntity();
        lastStoredBlock.setId(2L);
        lastStoredBlock.setNetwork("ethereum");
        when(client.getBlockByNumber(null)).thenReturn(Mono.just(mostRecentBlock));
        when(blockRepository.findLatest("ethereum")).thenReturn(lastStoredBlock);

        // When
        syncBlocksCommand.sync();

        // Then
        verify(client).getBlockByNumber(null);
        verify(blockRepository).findLatest("ethereum");
        verifyNoMoreInteractions(client, blockRepository, processBlockCommand);
    }

    @Test
    void syncBlocks_whenSomeBlocksAreDelayed() {
        // Given
        Block mostRecentBlock = new Block();
        mostRecentBlock.network = "ethereum";
        mostRecentBlock.number = "0x4";
        BlockEntity lastStoredBlock = new BlockEntity();
        lastStoredBlock.setId(1L);
        lastStoredBlock.setNetwork("ethereum");
        when(client.getBlockByNumber(null)).thenReturn(Mono.just(mostRecentBlock));
        when(blockRepository.findLatest("ethereum")).thenReturn(lastStoredBlock);
        Block block2 = new Block();
        block2.network = "ethereum";
        block2.number = "0x2";
        Block block3 = new Block();
        block3.network = "ethereum";
        block3.number = "0x3";
        when(client.getDelayedBlocksRange(2L, 4L)).thenReturn(Flux.just(block2, block3, mostRecentBlock));
        BlockEntity storedBlock2 = new BlockEntity();
        storedBlock2.setId(2L);
        BlockEntity storedBlock3 = new BlockEntity();
        storedBlock3.setId(3L);
        BlockEntity storedBlock4 = new BlockEntity();
        storedBlock3.setId(4L);
        when(processBlockCommand.process(block2)).thenReturn(storedBlock2);
        when(processBlockCommand.process(block3)).thenReturn(storedBlock3);
        when(processBlockCommand.process(mostRecentBlock)).thenReturn(storedBlock4);


        // When
        syncBlocksCommand.sync();

        // Then
        verify(client).getBlockByNumber(null);
        verify(blockRepository).findLatest("ethereum");
        verify(client).getDelayedBlocksRange(2L, 4L);
        verify(processBlockCommand).process(block2);
        verify(processBlockCommand).process(block3);
        verify(processBlockCommand).process(mostRecentBlock);
        verifyNoMoreInteractions(client, blockRepository, processBlockCommand);
    }
}
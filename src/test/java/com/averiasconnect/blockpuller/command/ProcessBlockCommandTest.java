package com.averiasconnect.blockpuller.command;

import com.averiasconnect.blockpuller.exception.TransactionsProducerException;
import com.averiasconnect.blockpuller.external.model.Block;
import com.averiasconnect.blockpuller.external.model.Transaction;
import com.averiasconnect.blockpuller.model.BlockEntity;
import com.averiasconnect.blockpuller.producer.TransactionsProducer;
import com.averiasconnect.blockpuller.producer.TransactionsProducerToggle;
import com.averiasconnect.blockpuller.repository.BlockRepository;
import com.averiasconnect.blockpuller.service.converter.BlockConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessBlockCommandTest {

  @Mock private BlockRepository blockRepository;

  @Mock private BlockConverter blockConverter;

  @Mock private TransactionsProducerToggle transactionsProducerToggle;

  @InjectMocks private ProcessBlockCommand processBlockCommand;

  @Test
  void processBlock() {
    // Given
    Block block = new Block();
    block.timestamp = "1234567890";
    Transaction transaction = new Transaction();
    block.transactions = new ArrayList<>(List.of(transaction));
    BlockEntity blockEntity = new BlockEntity();
    TransactionsProducer transactionsProducer = mock(TransactionsProducer.class);

    when(transactionsProducer.sendTransactions(block.transactions)).thenReturn(Mono.just(2L));
    when(blockConverter.convertToEntity(block)).thenReturn(blockEntity);
    when(blockRepository.save(blockEntity)).thenReturn(blockEntity);
    when(transactionsProducerToggle.getProducer()).thenReturn(transactionsProducer);

    // When
    BlockEntity result = processBlockCommand.process(block);

    // Then
    verify(blockConverter).convertToEntity(block);
    verify(transactionsProducerToggle).getProducer();
    verify(transactionsProducer).sendTransactions(List.of(transaction));
    verify(blockRepository).save(blockEntity);
    assert (result.equals(blockEntity));
  }

  @Test
  void processBlock_whenSendTransactions_throwException() {
    // Given
    Block block = new Block();
    block.timestamp = "1234567890";
    Transaction transaction = new Transaction();
    block.transactions = new ArrayList<>(List.of(transaction));
    BlockEntity blockEntity = new BlockEntity();
    TransactionsProducer transactionsProducer = mock(TransactionsProducer.class);

    when(transactionsProducer.sendTransactions(block.transactions))
        .thenThrow(TransactionsProducerException.class);
    when(blockConverter.convertToEntity(block)).thenReturn(blockEntity);
    when(blockRepository.save(blockEntity)).thenReturn(blockEntity);
    when(transactionsProducerToggle.getProducer()).thenReturn(transactionsProducer);

    // When
    BlockEntity result = processBlockCommand.process(block);

    // Then
    verify(blockConverter).convertToEntity(block);
    verify(transactionsProducerToggle).getProducer();
    verify(transactionsProducer).sendTransactions(List.of(transaction));
    verify(blockRepository).save(blockEntity);
    assert (result.equals(blockEntity));
  }
}

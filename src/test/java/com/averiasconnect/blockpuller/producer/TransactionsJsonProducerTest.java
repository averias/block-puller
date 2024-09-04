package com.averiasconnect.blockpuller.producer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.averiasconnect.blockpuller.exception.TransactionsProducerException;
import com.averiasconnect.blockpuller.external.model.Transaction;
import com.averiasconnect.blockpuller.model.avro.TransactionRecord;
import java.lang.reflect.Field;
import java.util.List;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderResult;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;

class TransactionsJsonProducerTest {

  @Mock private KafkaSender<String, TransactionRecord> avroKafkaSender;

  @InjectMocks private TransactionsJsonProducer transactionsJsonProducer;

  @BeforeEach
  void setUp() throws NoSuchFieldException, IllegalAccessException {
    MockitoAnnotations.openMocks(this);
    // Use reflection to set the private transactionsTopic field
    Field topicField = TransactionsJsonProducer.class.getDeclaredField("transactionsTopic");
    topicField.setAccessible(true); // Make the private field accessible
    topicField.set(transactionsJsonProducer, "json.transactions"); // Set the value
  }

  @Test
  void sendTransactions() {
    // Given
    Transaction tx1 = new Transaction();
    tx1.hash = "hash1";
    tx1.to = "address1";
    tx1.chainId = "chainId1";

    Transaction tx2 = new Transaction();
    tx2.hash = "hash2";
    tx2.to = null; // This should be filtered out as "to" is null
    tx2.chainId = "chainId2";

    Transaction tx3 = new Transaction();
    tx3.hash = "hash3";
    tx3.to = "address2";
    tx3.chainId = "chainId3";

    TestPublisher<SenderResult<String>> testPublisher = TestPublisher.createCold();
    when(avroKafkaSender.send(any(Flux.class))).thenReturn(testPublisher.flux());

    SenderResult<String> mockSenderResult = mock(SenderResult.class);
    RecordMetadata mockRecordMetadata = mock(RecordMetadata.class);
    when(mockRecordMetadata.timestamp())
        .thenReturn(System.currentTimeMillis()); // Or any valid timestamp
    when(mockSenderResult.recordMetadata()).thenReturn(mockRecordMetadata);

    // Simulate successful send results for tx1 and tx3
    testPublisher.emit(mockSenderResult, mockSenderResult);

    // When
    Mono<Long> result = transactionsJsonProducer.sendTransactions(List.of(tx1, tx2, tx3));

    // Then
    StepVerifier.create(result)
        .expectNext(2L) // Expect 2 transactions to be successfully processed
        .verifyComplete();

    verify(avroKafkaSender, times(1)).send(any(Flux.class));
  }

  @Test
  void sendTransactions_withError() {
    // Given
    Transaction tx1 = new Transaction();
    tx1.hash = "hash1";
    tx1.to = "address1";
    tx1.chainId = "chainId1";

    TestPublisher<SenderResult<String>> testPublisher = TestPublisher.create();
    testPublisher.error(new RuntimeException("Kafka error"));

    when(avroKafkaSender.send(any(Flux.class))).thenReturn(testPublisher.flux());

    // When
    Mono<Long> result = transactionsJsonProducer.sendTransactions(List.of(tx1));

    // When & Then
    StepVerifier.create(result)
        .expectErrorMatches(
            throwable ->
                throwable instanceof TransactionsProducerException
                    && throwable.getMessage().contains("Error in TransactionsJsonProducer"))
        .verify();
  }
}

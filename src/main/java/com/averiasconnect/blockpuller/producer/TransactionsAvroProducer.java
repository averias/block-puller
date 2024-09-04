package com.averiasconnect.blockpuller.producer;

import com.averiasconnect.blockpuller.exception.TransactionsProducerException;
import com.averiasconnect.blockpuller.external.model.Transaction;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import com.averiasconnect.blockpuller.model.avro.TransactionRecord;
import com.averiasconnect.blockpuller.service.converter.TransactionConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionsAvroProducer implements TransactionsProducer {
  private final KafkaSender<String, TransactionRecord> avroKafkaSender;
  private final TransactionConverter transactionConverter;

  @Value("${app.config.avro.transactions.topic.name}")
  private String transactionsTopic;

  public Mono<Long> sendTransactions(List<Transaction> transactions) {
    return avroKafkaSender
        .send(
            Flux.fromStream(transactions.stream())
                // txs with field "to" as null are smart contract creations
                .filter(tx -> (tx.to != null && tx.chainId != null))
                .map(
                    tx ->
                        SenderRecord.create(
                            new ProducerRecord<>(
                                transactionsTopic,
                                tx.hash,
                                transactionConverter.toAvroTransactionRecord(tx)),
                            tx.hash)))
        .doOnNext(
            r -> {
              RecordMetadata metadata = r.recordMetadata();
              Instant timestamp = Instant.ofEpochMilli(metadata.timestamp());
              log.info(
                  "Avro message for transaction {} sent successfully, topic-partition={}-{} offset={} timestamp={}",
                  r.correlationMetadata(),
                  metadata.topic(),
                  metadata.partition(),
                  metadata.offset(),
                  timestamp);
            })
        .doOnError(e -> log.error("Avro transaction sent error: {}", e.toString()))
        .onErrorMap(
            e ->
                new TransactionsProducerException(
                    String.format("Error in TransactionsAvroProducer: %s", e)))
        .collect(Collectors.counting());
  }
}

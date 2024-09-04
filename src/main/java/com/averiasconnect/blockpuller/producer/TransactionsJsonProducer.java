package com.averiasconnect.blockpuller.producer;

import com.averiasconnect.blockpuller.exception.TransactionsProducerException;
import com.averiasconnect.blockpuller.external.model.Transaction;
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

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionsJsonProducer implements TransactionsProducer {
  private final KafkaSender<String, Transaction> jsonKafkaSender;

  @Value("${app.config.json.transactions.topic.name}")
  private String transactionsTopic;

  public Mono<Long> sendTransactions(List<Transaction> transactions) {
    return jsonKafkaSender
        .send(
            Flux.fromStream(transactions.stream())
                // txs with file "to" as null are smart contract creations
                .filter(tx -> (tx.to != null && tx.chainId != null))
                .map(
                    tx ->
                        SenderRecord.create(
                            new ProducerRecord<>(transactionsTopic, tx.hash, tx), tx.hash)))
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
        .doOnError(e -> log.error("Json transaction sent error: {}", e.toString()))
        .onErrorMap(
            e ->
                new TransactionsProducerException(
                    String.format("Error in TransactionsJsonProducer: %s", e)))
        .collect(Collectors.counting());
  }
}

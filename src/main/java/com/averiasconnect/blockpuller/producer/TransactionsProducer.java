package com.averiasconnect.blockpuller.producer;

import com.averiasconnect.blockpuller.external.model.Transaction;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TransactionsProducer {
    Mono<Long> sendTransactions(List<Transaction> transactions);
}

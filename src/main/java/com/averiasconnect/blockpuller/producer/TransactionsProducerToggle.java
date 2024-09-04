package com.averiasconnect.blockpuller.producer;

import io.getunleash.Unleash;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionsProducerToggle {
    private final Unleash unleashDefaultService;
    private final TransactionsAvroProducer transactionsAvroProducer;
    private final TransactionsJsonProducer transactionsJsonProducer;

    public TransactionsProducer getProducer() {
        return unleashDefaultService.isEnabled("kafkaAvroEnabled")
                ? transactionsAvroProducer
                : transactionsJsonProducer;
    }
}

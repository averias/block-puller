package com.averiasconnect.blockpuller.external.service;

import com.averiasconnect.blockpuller.external.handler.WebClientStatusCodeHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

@Service
public class ExchangeFilterFunctionBuilder {
    public ExchangeFilterFunction responseStatusErrorFilter() {
        return ExchangeFilterFunction.ofResponseProcessor(
                WebClientStatusCodeHandler::exchangeFilterResponseProcessor);
    }
}

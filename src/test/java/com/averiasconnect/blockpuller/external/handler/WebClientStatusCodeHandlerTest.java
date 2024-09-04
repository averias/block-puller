package com.averiasconnect.blockpuller.external.handler;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WebClientStatusCodeHandlerTest {

  @Test
  void testExchangeFilterResponseProcessor() {
    // Given
    ClientResponse mockResponse = mock(ClientResponse.class);
    when(mockResponse.statusCode()).thenReturn(HttpStatus.OK);
    when(mockResponse.bodyToMono(String.class)).thenReturn(Mono.just("Success"));

    // When
    Mono<ClientResponse> result =
        WebClientStatusCodeHandler.exchangeFilterResponseProcessor(mockResponse);

    // Then
    StepVerifier.create(result).expectNext(mockResponse).verifyComplete();
  }

  @Test
  void testExchangeFilterResponseProcessor_whenInternalServerError() {
    // Given
    String errorMessage = "Internal Server Error";
    ClientResponse mockResponse = mock(ClientResponse.class);
    when(mockResponse.statusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(mockResponse.headers())
        .thenReturn(ClientResponse.create(HttpStatus.INTERNAL_SERVER_ERROR).build().headers());
    when(mockResponse.bodyToMono(String.class)).thenReturn(Mono.just(errorMessage));

    // When
    Mono<ClientResponse> result =
        WebClientStatusCodeHandler.exchangeFilterResponseProcessor(mockResponse);

    // Then
    StepVerifier.create(result)
        .expectErrorMatches(
            throwable -> {
              assertTrue(throwable instanceof WebClientResponseException);
              WebClientResponseException ex = (WebClientResponseException) throwable;
              assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getStatusCode().value());
              assertEquals(errorMessage, ex.getResponseBodyAsString());
              return true;
            })
        .verify();
  }
}

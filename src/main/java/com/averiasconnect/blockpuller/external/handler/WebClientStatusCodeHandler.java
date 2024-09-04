package com.averiasconnect.blockpuller.external.handler;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

public class WebClientStatusCodeHandler {
  public static Mono<ClientResponse> exchangeFilterResponseProcessor(ClientResponse response) {
    HttpStatusCode status = response.statusCode();
    if (response.statusCode().isError()) {
      return response
          .bodyToMono(String.class)
          .flatMap(
              body ->
                  Mono.error(
                      new WebClientResponseException(
                          status.value(),
                          status.toString(),
                          response.headers().asHttpHeaders(),
                          body.getBytes(),
                          null)));
    }
    return Mono.just(response);
  }
}

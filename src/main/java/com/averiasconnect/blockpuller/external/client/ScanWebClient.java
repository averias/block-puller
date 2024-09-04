package com.averiasconnect.blockpuller.external.client;

import com.averiasconnect.blockpuller.external.model.Block;
import com.averiasconnect.blockpuller.external.model.BlockNumber;
import com.averiasconnect.blockpuller.external.response.BlockNumberResponse;
import com.averiasconnect.blockpuller.external.response.BlockResponse;
import jakarta.annotation.Nullable;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.LongStream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class ScanWebClient {
  private static final Integer DELAY = 1000;

  private final WebClient client;
  private final String network;
  private final String apikey;

  public ScanWebClient(WebClient client, String network, String apikey) {
    this.client = client;
    this.network = network;
    this.apikey = apikey;
  }

  public Mono<BlockNumber> getMostRecentBlockNumber() {
    return this.client
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path("/api")
                    .queryParam("module", "proxy")
                    .queryParam("action", "eth_blockNumber")
                    .queryParam("apikey", apikey)
                    .build())
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(BlockNumberResponse.class)
        .map(BlockNumberResponse::getBlockNumber)
        .doOnError(
            ex ->
                log.error(
                    "Pulling most recent block number from {} network failed. Reason {}",
                    network,
                    ex.toString()))
        .onErrorResume(e -> Mono.empty());
  }

  public Mono<Block> getBlockByNumber(@Nullable String blockNumber) {
    String finalBlockNumber = Optional.ofNullable(blockNumber).orElse("latest");
    return this.client
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path("/api")
                    .queryParam("module", "proxy")
                    .queryParam("action", "eth_getBlockByNumber")
                    .queryParam("tag", finalBlockNumber)
                    .queryParam("boolean", "true")
                    .queryParam("apikey", apikey)
                    .build())
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(BlockResponse.class)
        .map(response -> response.getBlock(network))
        .doOnError(
            ex ->
                log.error(
                    "Pulling block number {} from {} network failed. Reason {}",
                    blockNumber,
                    network,
                    ex.toString()))
        .onErrorResume(e -> Mono.empty());
  }

  public Flux<Block> getDelayedBlocksRange(Long from, Long to) {
    return Flux.fromStream(LongStream.rangeClosed(from, to).boxed())
        .delayElements(Duration.ofMillis(DELAY))
        .flatMap(blockNumber -> getBlockByNumber(Long.toHexString(blockNumber)))
        .filter(Objects::nonNull);
  }
}

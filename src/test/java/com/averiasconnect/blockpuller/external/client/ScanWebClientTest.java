package com.averiasconnect.blockpuller.external.client;

import com.averiasconnect.blockpuller.external.model.Block;
import com.averiasconnect.blockpuller.external.model.BlockNumber;
import com.averiasconnect.blockpuller.external.service.ExchangeFilterFunctionBuilder;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScanWebClientTest {
  private static final String HEX_BLOCK_NUMBER = "0x12cb38e";

  private MockWebServer mockWebServer;
  private ScanWebClient scanWebClient;

  @BeforeEach
  void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();

    ExchangeFilterFunctionBuilder exchangeFilterFunctionBuilder =
        new ExchangeFilterFunctionBuilder();

    String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
    WebClient webClient =
        WebClient.builder()
            .baseUrl(baseUrl)
            .filter(exchangeFilterFunctionBuilder.responseStatusErrorFilter())
            .build();
    scanWebClient = new ScanWebClient(webClient, "ethereum", "yourApiKey");
  }

  @AfterEach
  void tearDown() throws IOException {
    mockWebServer.shutdown();
  }

  @Test
  void getMostRecentBlockNumber() throws IOException {
    String responseBody = getJsonFileContent("most-recent-block-number-response.json");
    mockWebServer.enqueue(getMockedResponse(HttpStatus.OK, responseBody));

    Mono<BlockNumber> blockNumberMono = scanWebClient.getMostRecentBlockNumber();

    StepVerifier.create(blockNumberMono)
        .assertNext(
            blockNumber -> assertEquals(HEX_BLOCK_NUMBER, blockNumber.number()))
        .verifyComplete();
  }

  @Test
  void getMostRecentBlockNumber_notFound() throws IOException {
    String responseBody = getJsonFileContent("most-recent-block-number-response.json");
    mockWebServer.enqueue(getMockedResponse(HttpStatus.NOT_FOUND, responseBody));

    Mono<BlockNumber> blockNumberMono = scanWebClient.getMostRecentBlockNumber();

    StepVerifier.create(blockNumberMono).expectNextCount(0).verifyComplete();
  }

  @Test
  void getBlockByNumber() throws IOException {
    String responseBody = getJsonFileContent("block-19706766-response.json");
    mockWebServer.enqueue(getMockedResponse(HttpStatus.OK, responseBody));

    Mono<Block> blockMono = scanWebClient.getBlockByNumber("0x12cb38e");

    StepVerifier.create(blockMono)
        .assertNext(
            block -> {
              assertEquals("ethereum", block.network);
              assertEquals("0x12cb38e", block.number);
              assertEquals(19706766L, block.getBlockNumberAsLong());
              assertEquals(4, block.transactions.size());
            })
        .verifyComplete();
  }

  @Test
  void getBlockByNumber_badRequestResponse() throws IOException {
    String responseBody = getJsonFileContent("block-19706766-response.json");
    mockWebServer.enqueue(getMockedResponse(HttpStatus.BAD_REQUEST, responseBody));

    Mono<Block> blockMono = scanWebClient.getBlockByNumber("0x12cb38e");

    StepVerifier.create(blockMono).expectNextCount(0).verifyComplete();
  }

  @Test
  void getDelayedBlocksRange() throws IOException {
    String responseBodyBlock19706766 = getJsonFileContent("block-19706766-response.json");
    String responseBodyBlock19706767 = getJsonFileContent("block-19706767-response.json");
    mockWebServer.enqueue(getMockedResponse(HttpStatus.OK, responseBodyBlock19706766));
    mockWebServer.enqueue(getMockedResponse(HttpStatus.OK, responseBodyBlock19706767));

    Flux<Block> blockFlux = scanWebClient.getDelayedBlocksRange(19706766L, 19706767L);

    StepVerifier.create(blockFlux)
        .assertNext(
            block -> {
              assertEquals("ethereum", block.network);
              assertEquals("0x12cb38e", block.number);
              assertEquals(19706766L, block.getBlockNumberAsLong());
              assertEquals(4, block.transactions.size());
            })
        .assertNext(
            block -> {
              assertEquals("ethereum", block.network);
              assertEquals("0x12cb38f", block.number);
              assertEquals(19706767L, block.getBlockNumberAsLong());
              assertEquals(4, block.transactions.size());
            })
        .verifyComplete();
  }

  @Test
  void getDelayedBlocksRange_whenLastBlockInTheRangeDoesNotExist() throws IOException {
    String responseBodyBlock19706766 = getJsonFileContent("block-19706766-response.json");
    String responseBodyBlock19706767 = getJsonFileContent("block-19706767-response.json");
    mockWebServer.enqueue(getMockedResponse(HttpStatus.OK, responseBodyBlock19706766));
    mockWebServer.enqueue(getMockedResponse(HttpStatus.OK, responseBodyBlock19706767));
    mockWebServer.enqueue(getMockedResponse(HttpStatus.OK, "{}"));

    Flux<Block> blockFlux = scanWebClient.getDelayedBlocksRange(19706766L, 19706768L);

    StepVerifier.create(blockFlux).expectNextCount(2).verifyComplete();
  }

  private String getJsonFileContent(String filename) throws IOException {
    return new String(
        Files.readAllBytes(
            new File(String.format("src/test/java/resources/testdata/%s", filename))
                .getCanonicalFile()
                .toPath()));
  }

  private MockResponse getMockedResponse(HttpStatus responseStatus, String responseBody) {
    return new MockResponse()
        .setResponseCode(responseStatus.value())
        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .setBody(responseBody);
  }
}

package com.averiasconnect.blockpuller.config;

import com.averiasconnect.blockpuller.command.ProcessBlockCommand;
import com.averiasconnect.blockpuller.command.SyncBlocksCommand;
import com.averiasconnect.blockpuller.external.client.ScanWebClient;
import com.averiasconnect.blockpuller.external.service.ExchangeFilterFunctionBuilder;
import com.averiasconnect.blockpuller.repository.BlockRepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class SyncBlocksCommandConfiguration {
  private final BlockRepository blockRepository;
  private final ProcessBlockCommand processBlockCommand;
  private final ExchangeFilterFunctionBuilder exchangeFilterFunctionBuilder;

  @Bean
  @Qualifier("syncEthereumBlocksCommand")
  public SyncBlocksCommand syncEthereumBlocksCommand(
      @Value("${etherscan.url}") String url,
      @Value("${etherscan.network}") String network,
      @Value("${etherscan.apikey}") String apikey) {
    ScanWebClient scanWebClient =
        new ScanWebClient(
                webClientBuilder(url, responseStatusErrorFilter()), network, apikey);
    return new SyncBlocksCommand(scanWebClient, blockRepository, processBlockCommand);
  }

  @Bean
  @Qualifier("syncPolygonBlocksCommand")
  public SyncBlocksCommand syncPolygonBlocksCommand(
      @Value("${polygon.url}") String url,
      @Value("${polygon.network}") String network,
      @Value("${polygon.apikey}") String apikey) {
    ScanWebClient scanWebClient =
        new ScanWebClient(
            webClientBuilder(url, responseStatusErrorFilter()), network, apikey);
    return new SyncBlocksCommand(scanWebClient, blockRepository, processBlockCommand);
  }

  private WebClient webClientBuilder(String url, @Nullable ExchangeFilterFunction filter) {
    WebClient.Builder builder = WebClient.builder().baseUrl(url);
    return (filter != null) ? builder.filter(filter).build() : builder.build();
  }

  private ExchangeFilterFunction responseStatusErrorFilter() {
    return exchangeFilterFunctionBuilder.responseStatusErrorFilter();
  }
}

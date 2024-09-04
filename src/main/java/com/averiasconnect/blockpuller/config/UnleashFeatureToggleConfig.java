package com.averiasconnect.blockpuller.config;

import io.getunleash.DefaultUnleash;
import io.getunleash.Unleash;
import io.getunleash.util.UnleashConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration("unleashDefaultService")
public class UnleashFeatureToggleConfig {
  private static final String UNLEASH_APP_NAME = "blockchain-alerting";

  @Bean
  @Primary
  public Unleash getDefaultUnleash(
          @Value("${unleash.api.url}") String unleashApiUrl,
          @Value("${unleash.api.key}") String unleashApiKey) {
    UnleashConfig config =
            UnleashConfig.builder()
                    .appName(UNLEASH_APP_NAME)
                    .unleashAPI(unleashApiUrl)
                    .apiKey(unleashApiKey)
                    .build();
    return new DefaultUnleash(config);
  }
}

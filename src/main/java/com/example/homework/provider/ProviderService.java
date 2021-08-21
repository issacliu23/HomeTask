package com.example.homework.provider;

import com.example.homework.configuration.ProviderConfiguration;
import com.example.homework.customexceptions.InvalidAPIProviderException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Service
@EnableConfigurationProperties(value = ProviderConfiguration.class)
public class ProviderService {
    public Boolean isAccountNumberValid(String url, Integer accountNumber) throws InvalidAPIProviderException {
        return WebClient.create(url)
                .get()
                .uri(UriBuilder -> UriBuilder.path("/{accountNumber}").build(accountNumber))
                .retrieve()
                .bodyToMono(Boolean.class)
                .block(Duration.ofSeconds(1));
    }

}

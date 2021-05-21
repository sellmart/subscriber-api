package com.netflix.subscriberapi.integration.service;

import com.netflix.subscriberapi.integration.dto.AllowedCardNetworksDto;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class AvailableCardNetworkService {
    private final RestTemplate restTemplate;

    @Value("${NETFLIX_URL}")
    private String host;

    public Try<AllowedCardNetworksDto> getAllowedCardNetworks() {
        String url = String.format("http://%s/api/v1/cardNetworks", host);

        return Try.of(() -> restTemplate.getForObject(url, AllowedCardNetworksDto.class))
                .onFailure(t -> {
                    String message = String.format("Error occurred while attempting retrieve available card networks. Error message: %s",
                            t.getMessage());
                    log.error(message);
                });
    }
}

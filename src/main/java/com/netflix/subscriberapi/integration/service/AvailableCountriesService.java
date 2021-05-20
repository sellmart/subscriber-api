package com.netflix.subscriberapi.integration.service;

import com.netflix.subscriberapi.integration.dto.AvailableCountriesDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import io.vavr.control.Try;

@Service
@Slf4j
@RequiredArgsConstructor
public class AvailableCountriesService {
    @Value("${NETFLIX_URL}")
    private String host;

    public Try<AvailableCountriesDto> getAvailableCountries() {
        String url = String.format("http://%s/api/v1/countries", host);
        RestTemplate restTemplate = new RestTemplate();
        return Try.of(() -> restTemplate.getForObject(url, AvailableCountriesDto.class))
                .onFailure(t -> {
                    String message = String.format("Error occurred while attempting retrieve available countries. Error message: %s",
                            t.getMessage());
                    log.error(message);
                });
    }
}

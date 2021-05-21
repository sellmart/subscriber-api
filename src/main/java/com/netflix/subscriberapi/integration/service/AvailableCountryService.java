package com.netflix.subscriberapi.integration.service;

import com.netflix.subscriberapi.integration.dto.AvailableCountriesDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import io.vavr.control.Try;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class AvailableCountryService {
    private RestTemplate restTemplate;

    @Value("${NETFLIX_URL}")
    private String host;

    public Try<AvailableCountriesDto> getAvailableCountries() {
        var url = String.format("http://%s/api/v1/countries", host);

        return Try.of(() -> getRestTemplate().getForObject(url, AvailableCountriesDto.class))
                .onFailure(t -> {
                    String message = String.format("Error occurred while attempting retrieve available countries. Error message: %s",
                            t.getMessage());
                    log.error(message);
                });
    }

    private RestTemplate getRestTemplate() {
        if (Objects.isNull(restTemplate)) {
            restTemplate = new RestTemplate();
        }
        return restTemplate;
    }
}

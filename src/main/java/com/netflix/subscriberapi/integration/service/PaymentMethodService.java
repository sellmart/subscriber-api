package com.netflix.subscriberapi.integration.service;

import com.netflix.subscriberapi.integration.dto.AddPaymentResponseDto;
import com.netflix.subscriberapi.integration.dto.PaymentMethodRequestDto;
import com.netflix.subscriberapi.util.CardNumberUtils;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentMethodService {
    private RestTemplate restTemplate;

    @Value("${NETFLIX_URL}")
    private String host;

    public Try<AddPaymentResponseDto> addPaymentMethod(PaymentMethodRequestDto request) {
        String maskedCardNumber = CardNumberUtils.maskCardNumber(request.getCardNumber());
        var url = String.format("http://%s/api/v1/addPayment", host);

        HttpEntity<PaymentMethodRequestDto> entity = new HttpEntity<>(request);
        return Try.of(() ->
                getRestTemplate().postForObject(url, entity, AddPaymentResponseDto.class))
                    .onFailure(t -> {
                        String message = String.format("Error occurred while attempting to add payment method {}. Error message: %s",
                        maskedCardNumber, t.getMessage());
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

package com.netflix.subscriberapi.service;

import com.netflix.subscriberapi.dataaccess.dto.StatsDto;
import com.netflix.subscriberapi.dataaccess.dto.SubscriptionRequestDto;
import com.netflix.subscriberapi.dataaccess.dto.SubscriptionResponseDto;
import com.netflix.subscriberapi.dataaccess.mapper.SubscriptionMapper;
import com.netflix.subscriberapi.dataaccess.search.StatsFilter;
import com.netflix.subscriberapi.exception.SubscriberApiException;
import com.netflix.subscriberapi.integration.dto.AddPaymentResponseDto;
import com.netflix.subscriberapi.integration.dto.AllowedCardNetworksDto;
import com.netflix.subscriberapi.integration.dto.AvailableCountriesDto;
import com.netflix.subscriberapi.integration.dto.PaymentMethodRequestDto;
import com.netflix.subscriberapi.integration.service.AvailableCardNetworksService;
import com.netflix.subscriberapi.integration.service.AvailableCountriesService;
import com.netflix.subscriberapi.integration.service.PaymentMethodService;
import com.netflix.subscriberapi.model.Subscription;
import com.netflix.subscriberapi.repository.SubscriptionRepository;
import com.netflix.subscriberapi.util.CardNumberUtils;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class SubscriptionService {
    private static final SubscriptionMapper mapper = SubscriptionMapper.INSTANCE;

    private final AvailableCardNetworksService cardNetworksService;
    private final AvailableCountriesService countriesService;
    private final PaymentMethodService paymentMethodService;
    private final SubscriptionRepository subscriptionRepository;
    private final EntityManager entityManager;

    public SubscriptionResponseDto createSubscription(SubscriptionRequestDto request) {
        if (!request.isValid()) {
            String maskedCardNumber = CardNumberUtils.maskCardNumber(request.getCardNumber());
            log.error("Invalid request object, cardNumber: {}, Network: {}, Country: {}, Exp Year: {}",
                    maskedCardNumber, request.getCardNetwork(), request.getCountry(), request.getTwoDigitYear());
            throw new SubscriberApiException(HttpStatus.BAD_REQUEST,
                    "One or more required fields are missing or invalid.");
        }

        validateAllowedCardNetwork(request.getCardNetwork(), request.getCardNumber());
        validateAllowedCountry(request.getCountry());

        if (!savedPaymentMethod(request)) {
            throw new SubscriberApiException(HttpStatus.BAD_REQUEST,
                    "Unable to save payment method, validate card payment info and try again.");
        }

        return saveAndMapSubscription(request);
    }

    public StatsDto getSubscriptionCount(StatsFilter filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countCriteria = cb.createQuery(Long.class);
        Root<Subscription> root = countCriteria.from(Subscription.class);

        countCriteria.select(cb.count(root));
        List<Predicate> andPredicates = getSearchPredicates(cb, root, filter);
        if (!andPredicates.isEmpty()) {
            countCriteria.where(cb.and(andPredicates.stream().toArray(Predicate[]::new)));
        }

        TypedQuery<Long> queryCount = entityManager.createQuery(countCriteria);
        Long count = queryCount.getSingleResult();

        return StatsDto.builder().count(count).build();
    }

    private void validateAllowedCountry(String country) {
        Try<AvailableCountriesDto> availableCountryResponse = countriesService.getAvailableCountries();
        if (availableCountryResponse.isFailure()) {
            throw new SubscriberApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error occurred attempting to retrieve available countries.");
        }

        List<String> countries = availableCountryResponse.get().getCountries();
        if (Objects.isNull(countries) || !countries.stream()
                .anyMatch(availableCountry -> StringUtils.equalsIgnoreCase(availableCountry, country))) {
            log.error("Country: {} does not match available country list {}",
                    country, countries.toString()
            );
            throw new SubscriberApiException(HttpStatus.BAD_REQUEST,
                    "Unable to subscribe in requested country.");
        }
    }

    private void validateAllowedCardNetwork(String cardNetwork, String cardNumber) {
        Try<AllowedCardNetworksDto> allowedCardResponse = cardNetworksService.getAllowedCardNetworks();
        if (allowedCardResponse.isFailure()) {
            throw new SubscriberApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error occurred attempting to retrieve available card networks.");
        }
        List<String> cardNetworks = allowedCardResponse.get().getCardNetworks();
        if (Objects.isNull(cardNetworks) || !cardNetworks.stream()
                .anyMatch(availableNetwork -> StringUtils.equalsIgnoreCase(availableNetwork, cardNetwork))) {
            log.error("Card network: {} does not match available network list {}",
                    cardNetwork, cardNetworks.toString()
            );
            throw new SubscriberApiException(HttpStatus.BAD_REQUEST,
                    "Unable to subscribe with requested card network.");
        }

        // TODO: validate card network matches card number
    }

    private boolean savedPaymentMethod(SubscriptionRequestDto request) {
        PaymentMethodRequestDto paymentMethod = PaymentMethodRequestDto.builder().cardNumber(request.getCardNumber())
                .twoDigitYear(request.getTwoDigitYear()).build();
        Try<AddPaymentResponseDto> response = paymentMethodService.addPaymentMethod(paymentMethod);
        if (response.isFailure()) {
            throw new SubscriberApiException(HttpStatus.BAD_REQUEST, response.getCause().getMessage());
        }
        return Boolean.TRUE.equals(response.get().getValid());
    }

    private List<Predicate> getSearchPredicates(CriteriaBuilder cb, Root<Subscription> root, StatsFilter filter) {
        List<Predicate> predicates = new ArrayList<>();
        if (Objects.nonNull(filter.getCountry())) {
            predicates.add(cb.equal(cb.lower(root.get("country")), StringUtils.lowerCase(filter.getCountry())));
        }
        if (Objects.nonNull(filter.getCardNetwork())) {
            predicates.add(cb.equal(cb.lower(root.get("cardNetwork")), StringUtils.lowerCase(filter.getCardNetwork())));
        }
        return predicates;
    }

    private SubscriptionResponseDto saveAndMapSubscription(SubscriptionRequestDto request) {
        Subscription entity = mapper.toEntity(request);
        entity.setCardNumber(CardNumberUtils.maskCardNumber(request.getCardNumber()));
        entity.setIsActive(Boolean.TRUE);
        return mapper.toDto(subscriptionRepository.save(entity));
    }
}

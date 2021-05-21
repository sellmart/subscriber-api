package com.netflix.subscriberapi.service;

import com.netflix.subscriberapi.dataaccess.dto.StatsDto;
import com.netflix.subscriberapi.dataaccess.dto.SubscriptionRequestDto;
import com.netflix.subscriberapi.dataaccess.dto.SubscriptionResponseDto;
import com.netflix.subscriberapi.dataaccess.search.StatsFilter;
import com.netflix.subscriberapi.exception.SubscriberApiException;
import com.netflix.subscriberapi.integration.dto.AddPaymentResponseDto;
import com.netflix.subscriberapi.integration.dto.AllowedCardNetworksDto;
import com.netflix.subscriberapi.integration.dto.AvailableCountriesDto;
import com.netflix.subscriberapi.integration.service.AvailableCardNetworkService;
import com.netflix.subscriberapi.integration.service.AvailableCountryService;
import com.netflix.subscriberapi.integration.service.PaymentMethodService;
import com.netflix.subscriberapi.model.Subscription;
import com.netflix.subscriberapi.repository.SubscriptionRepository;
import com.netflix.subscriberapi.util.CardNumberUtils;
import io.vavr.control.Try;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
public class SubscriptionServiceTest {
    @Mock
    private SubscriptionRepository repository;
    @Mock
    private AvailableCardNetworkService availableCardNetworkService;
    @Mock
    private AvailableCountryService availableCountryService;
    @Mock
    private PaymentMethodService paymentMethodService;
    @Mock
    private CreditCardNumberFormatRuleService creditCardNumberFormatRuleService;
    @Mock
    private EntityManager entityManager;
    @InjectMocks
    private SubscriptionService service;

    @Test(expected = SubscriberApiException.class)
    public void createSubscriptionWithEmptyRequestShouldThrowException() {

        service.createSubscription(SubscriptionRequestDto.builder().build());
    }

    @Test(expected = SubscriberApiException.class)
    public void createSubscriptionShouldThrowExceptionWithRequestThatHasInvalidExpYear() {

        service.createSubscription(SubscriptionRequestDto.builder().twoDigitYear("4").build());
    }

    @Test(expected = SubscriberApiException.class)
    public void createSubscriptionShouldThrowExceptionWhenNetworkAndCardMatchThrowsException() {
        Mockito.when(creditCardNumberFormatRuleService.cardNumberMatchCardNetwork(anyString(),
                anyString())).thenThrow(new SubscriberApiException("Error Occurred"));

        service.createSubscription(getMockValidSubscriptionRequestDto());
    }

    @Test(expected = SubscriberApiException.class)
    public void createSubscriptionShouldThrowExceptionWhenNetworkAndCardAreNotAMatch() {
        Mockito.when(creditCardNumberFormatRuleService.cardNumberMatchCardNetwork(anyString(),
                anyString())).thenReturn(false);

        service.createSubscription(getMockValidSubscriptionRequestDto());
    }

    @Test(expected = SubscriberApiException.class)
    public void createSubscriptionShouldThrowExceptionWhenAllowedCardNetworkServiceThrowsException() {
        Mockito.when(availableCardNetworkService.getAllowedCardNetworks())
                .thenThrow(new SubscriberApiException("Error occurred"));

        service.createSubscription(getMockValidSubscriptionRequestDto());
    }

    @Test(expected = SubscriberApiException.class)
    public void createSubscriptionShouldThrowExceptionWhenProvidedCardNetworkDoesNotMatchAllowedList() {
        Mockito.when(availableCardNetworkService.getAllowedCardNetworks())
                .thenReturn(getTryCardNetworkResponse(null));

        service.createSubscription(getMockValidSubscriptionRequestDto());
    }

    @Test(expected = SubscriberApiException.class)
    public void createSubscriptionShouldThrowExceptionWhenProvidedCountryDoesNotMatchAllowedList() {
        Mockito.when(availableCountryService.getAvailableCountries())
                .thenReturn(getTryCountriesResponse(null));

        service.createSubscription(getMockValidSubscriptionRequestDto());
    }

    @Test(expected = SubscriberApiException.class)
    public void createSubscriptionShouldThrowExceptionWhenAllowedCountryServiceSendsEmptyResponse() {
        Mockito.when(availableCountryService.getAvailableCountries())
                .thenReturn(Try.of(() -> AvailableCountriesDto.builder().build()));

        service.createSubscription(getMockValidSubscriptionRequestDto());
    }

    @Test(expected = SubscriberApiException.class)
    public void createSubscriptionShouldThrowExceptionWhenAllowedCountryServiceThrowsAnException() {
        Mockito.when(availableCountryService.getAvailableCountries())
                .thenThrow(new SubscriberApiException("Error Occurred"));

        service.createSubscription(getMockValidSubscriptionRequestDto());
    }

    @Test(expected = SubscriberApiException.class)
    public void createSubscriptionShouldThrowExceptionWhenAddPaymentMethodServiceThrowsException() {
        Mockito.when(paymentMethodService.addPaymentMethod(Mockito.any()))
                .thenThrow(new SubscriberApiException("Error Occurred"));

        service.createSubscription(getMockValidSubscriptionRequestDto());
    }

    @Test(expected = SubscriberApiException.class)
    public void createSubscriptionShouldThrowExceptionWhenAddPaymentMethodServiceReturnsFalse() {
        Mockito.when(paymentMethodService.addPaymentMethod(Mockito.any()))
                .thenReturn(Try.of(() -> AddPaymentResponseDto.builder().valid(false).build()));

        service.createSubscription(getMockValidSubscriptionRequestDto());
    }

    @Test
    public void createSubscriptionShouldSavePaymentMethodAndSubscriberRecordSuccessfullyWhenRequestIsValidAndIntermediaryServicesSucceed() {
        SubscriptionRequestDto request = getMockValidSubscriptionRequestDto();
        Mockito.when(creditCardNumberFormatRuleService.cardNumberMatchCardNetwork(anyString(),
                anyString())).thenReturn(true);
        Mockito.when(paymentMethodService.addPaymentMethod(Mockito.any()))
                .thenReturn(Try.of(() -> AddPaymentResponseDto.builder().valid(true).build()));
        Mockito.when(availableCountryService.getAvailableCountries())
                .thenReturn(getTryCountriesResponse(request.getCountry()));
        Mockito.when(availableCardNetworkService.getAllowedCardNetworks())
                .thenReturn(getTryCardNetworkResponse(request.getCardNetwork()));
        Subscription stub = Subscription.builder()
                .cardNumber(CardNumberUtils.maskCardNumber(request.getCardNumber()))
                .cardNetwork(request.getCardNetwork())
                .country(request.getCountry()).id(34L).isActive(Boolean.TRUE)
                .createdDate(LocalDateTime.now()).lastModifiedDate(LocalDateTime.now())
                .expirationYear(request.getTwoDigitYear()).build();
        Mockito.when(repository.save(Mockito.any(Subscription.class))).thenReturn(stub);

        SubscriptionResponseDto response = service.createSubscription(request);

        assertThat(response.getCardNetwork()).isEqualTo(request.getCardNetwork());
        assertThat(response.getCountry()).isEqualTo(request.getCountry());
        assertThat(response.getExpYear()).isEqualTo(request.getTwoDigitYear());
        assertThat(response.getCardNumber()).isNotEqualTo(request.getCardNumber());
        assertThat(response.getCardNumber()).contains("*");
    }

    @Test
    public void getSubscriptionCountWithNoSearchFilters() {
        Long countResponse = 10L;
        performSubscriptionCountMocks(countResponse);

        StatsDto response = service.getSubscriptionCount(StatsFilter.builder().build());

        assertThat(response.getCount()).isEqualTo(countResponse);
    }

    @Test
    public void getSubscriptionCountWithCountryFilter() {
        Long countResponse = 8L;
        performSubscriptionCountMocks(countResponse);

        StatsDto response = service.getSubscriptionCount(StatsFilter.builder().country("US").build());

        assertThat(response.getCount()).isEqualTo(countResponse);
    }

    @Test
    public void getSubscriptionCountWithNetworkFilter() {
        Long countResponse = 3L;
        performSubscriptionCountMocks(countResponse);

        StatsDto response = service.getSubscriptionCount(StatsFilter.builder().cardNetwork("VISA").build());

        assertThat(response.getCount()).isEqualTo(countResponse);
    }

    @Test
    public void getSubscriptionCountWithNetworkAndCountryFilter() {
        Long countResponse = 1L;
        performSubscriptionCountMocks(countResponse);

        StatsDto response = service.getSubscriptionCount(StatsFilter.builder()
                .cardNetwork("VISA").country("GA").build());

        assertThat(response.getCount()).isEqualTo(countResponse);
    }

    private SubscriptionRequestDto getMockValidSubscriptionRequestDto() {
        return SubscriptionRequestDto.builder()
                .twoDigitYear("25").cardNetwork("VISA").country("GU").cardNumber("44304400056670241").build();
    }

    private Try<AllowedCardNetworksDto> getTryCardNetworkResponse(String additionalCardNetwork) {
        List<String> availableNetworks = new ArrayList<>() {{
            add("MASTERCARD");
            add("DISCOVER");
            add("AMEX");
        }};
        if (StringUtils.isNotEmpty(additionalCardNetwork)) {
            availableNetworks.add(additionalCardNetwork);
        }
        return Try.of(() -> AllowedCardNetworksDto.builder()
                .cardNetworks(availableNetworks).build());
    }

    private Try<AvailableCountriesDto> getTryCountriesResponse(String additionalCountry) {
        List<String> availableCountries = new ArrayList<>() {{
            add("US");
            add("PR");
            add("PA");
        }};
        if (StringUtils.isNotEmpty(additionalCountry)) {
            availableCountries.add(additionalCountry);
        }
        return Try.of(() -> AvailableCountriesDto.builder()
                .countries(availableCountries).build());
    }

    private void performSubscriptionCountMocks(Long val) {
        CriteriaBuilder cb = Mockito.mock(CriteriaBuilder.class);
        TypedQuery mockedQuery = Mockito.mock(TypedQuery.class);
        CriteriaQuery qMock = Mockito.mock(CriteriaQuery.class, Answers.RETURNS_DEEP_STUBS);
        Mockito.when(cb.createQuery(Mockito.any())).thenReturn(qMock);
        Mockito.when(entityManager.getCriteriaBuilder()).thenReturn(cb);
        Mockito.when(mockedQuery.getSingleResult()).thenReturn(val);
        Mockito.when(entityManager.createQuery(Mockito.any(CriteriaQuery.class)))
                .thenReturn(mockedQuery);
    }
}

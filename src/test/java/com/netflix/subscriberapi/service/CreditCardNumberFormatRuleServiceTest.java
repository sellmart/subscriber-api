package com.netflix.subscriberapi.service;

import com.netflix.subscriberapi.model.CreditCardNumberFormatRule;
import com.netflix.subscriberapi.repository.CreditCardNumberFormatRuleRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
public class CreditCardNumberFormatRuleServiceTest {
    private static final String FAKE_VISA_CARD_NUMBER = "4430112234246979";
    @Mock
    private CreditCardNumberFormatRuleRepository repository;
    @InjectMocks
    private CreditCardNumberFormatRuleService service;

    @Test
    public void cardNetworkAndNumberValidationPassesWhenRuleMissing() {
        Mockito.when(repository.findFirstByCardNetwork(Mockito.anyString())).thenReturn(null);

        assertThat(service.cardNumberMatchCardNetwork("3334111255652345", "JCB")).isTrue();
    }

    @Test
    public void cardNetworkAndNumberValidationFailsWhenImproperlyFormatted() {
        Mockito.when(repository.findFirstByCardNetwork(Mockito.anyString())).thenReturn(
                CreditCardNumberFormatRule.builder()
                .cardNetwork("JCB").id(2).iinRanges("33,44,66").lengthRanges("15").build()
        );

        assertThat(service.cardNumberMatchCardNetwork("3334111255652345", "JCB")).isFalse();
    }

    @Test
    public void cardNetworkAndNumberValidationPassesWhenOneOrMoreRangeRuleAreEmpty() {
        Mockito.when(repository.findFirstByCardNetwork(Mockito.anyString())).thenReturn(
                CreditCardNumberFormatRule.builder()
                        .cardNetwork("JCB").id(2).iinRanges("").lengthRanges(null).build()
        );
        assertThat(service.cardNumberMatchCardNetwork("3334111255652345", "JCB")).isTrue();
    }

    @Test
    public void cardNetworkAndNumberValidationFailsWhenNetworkAndNumberAreNoMatch() {
        Mockito.when(repository.findFirstByCardNetwork(Mockito.anyString())).thenReturn(
                CreditCardNumberFormatRule.builder()
                        .cardNetwork("JCB").id(2).iinRanges("[2211]").lengthRanges("[12-15]").build()
        );
        assertThat(service.cardNumberMatchCardNetwork(FAKE_VISA_CARD_NUMBER, "JCB")).isFalse();
    }

    @Test
    public void cardNetworkAndNumberValidationPassesWhenNetworkAndNumberMatch() {
        Mockito.when(repository.findFirstByCardNetwork(Mockito.anyString())).thenReturn(
                CreditCardNumberFormatRule.builder()
                        .cardNetwork("VISA").id(2).iinRanges("[4]").lengthRanges("[16]").build()
        );
        assertThat(service.cardNumberMatchCardNetwork(FAKE_VISA_CARD_NUMBER, "VISA")).isTrue();
    }
}

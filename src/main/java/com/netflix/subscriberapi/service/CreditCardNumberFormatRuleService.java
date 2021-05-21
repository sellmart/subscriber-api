package com.netflix.subscriberapi.service;

import com.netflix.subscriberapi.model.CreditCardNumberFormatRule;
import com.netflix.subscriberapi.repository.CreditCardNumberFormatRulesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Objects;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CreditCardNumberFormatRuleService {
    private static final String EQUALS_COMPARISON = "=";
    private static final String BEGINS_WITH_COMPARISON = "^";

    private final CreditCardNumberFormatRulesRepository repository;

    public boolean cardNumberMatchCardNetwork(String cardNumber, String cardNetwork) {
        CreditCardNumberFormatRule rule = repository.findFirstByCardNetwork(cardNetwork);
        if (Objects.isNull(rule)) {
            log.info("cardNetwork: {} rule not available, proceeding with flow as a new network may be available", cardNetwork);
            return true;
        }

        String[] iinRangeRules = extractRangeRuleParameterValues(rule.getIinRanges());
        String[] lengthRangeRules = extractRangeRuleParameterValues(rule.getLengthRanges());

        return isIINRangeValid(iinRangeRules, cardNumber) && isValidLength(lengthRangeRules, cardNumber);
    }

    private String[] extractRangeRuleParameterValues(String rangeRuleValue) {
        String commaDelimitedString = rangeRuleValue.replaceAll("\\[(.*?)\\]", "$1");
        return commaDelimitedString.split(",");
    }

    private boolean isIINRangeValid(String[] rules, String cardNumber) {
        return Arrays.stream(rules).anyMatch(rule -> {
            if (StringUtils.contains(rule, "-")) {
                return checkSequenceRange(rule, cardNumber, BEGINS_WITH_COMPARISON);
            }
            return StringUtils.startsWith(cardNumber, rule);
        });
    }

    private boolean isValidLength(String[] rules, String cardNumber) {
        return Arrays.stream(rules).anyMatch(rule -> {
            if (StringUtils.contains(rule, "-")) {
                return checkSequenceRange(rule, cardNumber, EQUALS_COMPARISON);
            }
            return StringUtils.length(cardNumber) == Integer.parseInt(rule);
        });
    }

    private boolean checkSequenceRange(String rule, String cardNumber, String comparison) {
        String[] splitRule = rule.split("-");
        Integer lowerBound = Integer.parseInt(splitRule[0]);
        Integer upperBound = Integer.parseInt(splitRule[1]);
        boolean isInRange = false;
        for (int i = lowerBound; i < upperBound; i++) {
            if (BEGINS_WITH_COMPARISON.equals(comparison)) {
                isInRange = StringUtils.startsWith(cardNumber, String.valueOf(i));
            }
            if (EQUALS_COMPARISON.equals(comparison)) {
                isInRange = StringUtils.length(cardNumber) == i;
            }
            if (isInRange) {
                break;
            }
        }
        return isInRange;
    }
}

package com.netflix.subscriberapi.util;

import org.apache.commons.lang3.StringUtils;

public class CardNumberUtils {

    public static String maskCardNumber(String cardNumber) {
        int maskLen = StringUtils.length(cardNumber) - 4;
        if (maskLen < 1) {
            return cardNumber;
        }
        return String.format("%s%s", "*".repeat(maskLen), cardNumber.substring(maskLen));
    }
}

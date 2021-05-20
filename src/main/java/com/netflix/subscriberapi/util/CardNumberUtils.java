package com.netflix.subscriberapi.util;

public class CardNumberUtils {

    public static String maskCardNumber(String cardNumber) {
        int maskLen = cardNumber.length() - 4;
        if (maskLen < 1) {
            return cardNumber;
        }
        return String.format("%s%s", "*".repeat(maskLen), cardNumber.substring(maskLen));
    }
}

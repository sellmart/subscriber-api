package com.netflix.subscriberapi.util;

public class CardNumberUtils {

    public static String maskCardNumber(String cardNumber) {
        int maskLen = cardNumber.length() - 4;
        if (maskLen < 1) {
            return cardNumber;
        }
        return "*".repeat(maskLen).format("%s", cardNumber.substring(maskLen));
    }
}

package com.netflix.subscriberapi.dataaccess.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionRequestDto {
    private String twoDigitYear;
    private String cardNumber;
    private String cardNetwork;
    private String country;

    public boolean isValid() {
        if ((Objects.isNull(twoDigitYear)
                || Objects.isNull(cardNumber)
                || Objects.isNull(cardNetwork)
                || Objects.isNull(country))) {
            return false;
        }

        if (twoDigitYear.length() != 2 || country.length() != 2
                || !NumberUtils.isParsable(twoDigitYear)) {
            return false;
        }

        return true;
    }
}

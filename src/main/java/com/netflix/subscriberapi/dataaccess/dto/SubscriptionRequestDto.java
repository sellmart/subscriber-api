package com.netflix.subscriberapi.dataaccess.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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
        if ((StringUtils.isEmpty(twoDigitYear)
                || StringUtils.isEmpty(cardNumber)
                || StringUtils.isEmpty(cardNetwork)
                || StringUtils.isEmpty(country))) {
            return false;
        }

        if (StringUtils.length(twoDigitYear) != 2 || StringUtils.length(country) != 2
                || !NumberUtils.isParsable(twoDigitYear)) {
            return false;
        }

        return true;
    }
}

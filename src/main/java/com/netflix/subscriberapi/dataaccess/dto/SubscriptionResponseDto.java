package com.netflix.subscriberapi.dataaccess.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionResponseDto {
    private Long id;
    private String cardNumber;
    private String cardNetwork;
    private String country;
    private String expYear;
    private Boolean isActive;
    private LocalDateTime created;
    private LocalDateTime lastModified;
}

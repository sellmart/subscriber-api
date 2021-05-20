package com.netflix.subscriberapi.dataaccess.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatsFilter {
    private String cardNetwork;
    private String country;
}

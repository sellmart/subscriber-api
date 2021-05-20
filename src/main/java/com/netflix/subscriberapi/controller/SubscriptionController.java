package com.netflix.subscriberapi.controller;

import com.netflix.subscriberapi.dataaccess.dto.StatsDto;
import com.netflix.subscriberapi.dataaccess.dto.SubscriptionRequestDto;
import com.netflix.subscriberapi.dataaccess.dto.SubscriptionResponseDto;
import com.netflix.subscriberapi.dataaccess.search.StatsFilter;
import com.netflix.subscriberapi.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @GetMapping("/stats/subscribers")
    public StatsDto getSubscriptionCount(@RequestParam(required = false) String cardNetwork,
                                         @RequestParam(required = false) String country) {
        StatsFilter filter = StatsFilter.builder().cardNetwork(cardNetwork).country(country).build();
        return subscriptionService.getSubscriptionCount(filter);
    }

    @PostMapping("/subscribe")
    public SubscriptionResponseDto subscribe(@RequestBody SubscriptionRequestDto request) {
        return subscriptionService.createSubscription(request);
    }
}

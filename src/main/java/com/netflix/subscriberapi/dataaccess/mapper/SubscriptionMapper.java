package com.netflix.subscriberapi.dataaccess.mapper;

import com.netflix.subscriberapi.dataaccess.dto.SubscriptionRequestDto;
import com.netflix.subscriberapi.dataaccess.dto.SubscriptionResponseDto;
import com.netflix.subscriberapi.model.Subscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SubscriptionMapper {
    SubscriptionMapper INSTANCE = Mappers.getMapper(SubscriptionMapper.class);

    @Mapping(target = "id", source = "model.id")
    @Mapping(target = "cardNumber", source = "model.cardNumber")
    @Mapping(target = "cardNetwork", source = "model.cardNetwork")
    @Mapping(target = "country", source = "model.country")
    @Mapping(target = "expYear", source = "model.expirationYear")
    @Mapping(target = "isActive", source = "model.isActive")
    @Mapping(target = "created", source = "model.createdDate")
    @Mapping(target = "lastModified", source = "model.lastModifiedDate")
    SubscriptionResponseDto toDto(Subscription model);

    @Mapping(target = "cardNumber", source = "cardNumber")
    @Mapping(target = "cardNetwork", source = "cardNetwork")
    @Mapping(target = "country", source = "country")
    @Mapping(target = "expirationYear", source = "twoDigitYear")
    Subscription toEntity(SubscriptionRequestDto model);
}

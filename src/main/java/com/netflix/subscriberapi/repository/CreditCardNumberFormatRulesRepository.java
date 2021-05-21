package com.netflix.subscriberapi.repository;

import com.netflix.subscriberapi.model.CreditCardNumberFormatRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditCardNumberFormatRulesRepository extends JpaRepository<CreditCardNumberFormatRule, Integer> {

    CreditCardNumberFormatRule findFirstByCardNetwork(String cardNetwork);
}

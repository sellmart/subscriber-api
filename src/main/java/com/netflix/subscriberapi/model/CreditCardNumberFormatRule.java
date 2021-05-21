package com.netflix.subscriberapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "credit_card_number_format_rules")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardNumberFormatRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;

    @Column(name = "card_network")
    private String cardNetwork;

    @Column(name = "iin_range")
    private String iinRanges;

    @Column(name = "length_range")
    private String lengthRanges;
}

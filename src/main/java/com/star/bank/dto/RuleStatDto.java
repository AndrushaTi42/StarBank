package com.star.bank.dto;

import com.fasterxml.jackson.annotation.JsonProperty;


public record RuleStatDto(
        @JsonProperty("rule_id") Long ruleId,
        @JsonProperty("count") Long count
) {
}

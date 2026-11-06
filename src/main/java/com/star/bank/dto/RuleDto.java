package com.star.bank.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.UUID;

public record RuleDto(
        @JsonProperty("id") Long id, // При создании будет null, при ответе заполнится
        @JsonProperty("product_name") String productName,
        @JsonProperty("product_id") UUID productId,
        @JsonProperty("product_text") String productText,
        @JsonProperty("rule") List<QueryDto> rule
) {}

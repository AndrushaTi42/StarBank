package com.star.bank.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record QueryDto(
        @JsonProperty("query") String query,
        @JsonProperty("arguments") List<String> arguments,
        @JsonProperty("negate") boolean negate
) {}

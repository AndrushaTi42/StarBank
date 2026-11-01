package com.star.bank.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.UUID;

public record UserRecommendationsDto(
        @JsonProperty("user_id") UUID userId,
        List<RecommendationDto> recommendations
) {}

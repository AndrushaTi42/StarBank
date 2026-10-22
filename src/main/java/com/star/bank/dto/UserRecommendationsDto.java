package com.star.bank.dto;

import java.util.List;
import java.util.UUID;

public class UserRecommendationsDto {
    private UUID userId;
    private List<RecommendationDto> recommendations;

    public UserRecommendationsDto(UUID userId, List<RecommendationDto> recommendations) {
        this.userId = userId;
        this.recommendations = recommendations;
    }

    // Геттеры
    public UUID getUserId() { return userId; }
    public List<RecommendationDto> getRecommendations() { return recommendations; }
}

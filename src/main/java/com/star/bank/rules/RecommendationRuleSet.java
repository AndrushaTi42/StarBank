package com.star.bank.rules;

import com.star.bank.dto.RecommendationDto;
import com.star.bank.dto.UserStats;

import java.util.Optional;
import java.util.UUID;

public interface RecommendationRuleSet {
    Optional<RecommendationDto> check(UUID userId, UserStats userStats);
}

package com.star.bank.service;

import com.star.bank.dto.RecommendationDto;
import com.star.bank.dto.UserRecommendationsDto;
import com.star.bank.dto.UserStats;
import com.star.bank.repository.RecommendationRepository;
import com.star.bank.rules.RecommendationRuleSet;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RecommendationService {
    private final RecommendationRepository repository;
    private final List<RecommendationRuleSet> ruleSets;

    public RecommendationService(RecommendationRepository repository, List<RecommendationRuleSet> ruleSets) {
        this.repository = repository;
        this.ruleSets = ruleSets;
    }

    public UserRecommendationsDto getRecommendations(UUID userId) {
        // Проверяем, существует ли пользователь (кинет исключение, если нет)
        if (!repository.userExists(userId)) {
            throw new IllegalArgumentException("Пользователь с ID " + userId + " не найден");
        }
        UserStats stats = repository.getUserStats(userId);

        List<RecommendationDto> recommendations = ruleSets.stream()
                .map(rule -> rule.check(userId, stats))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        return new UserRecommendationsDto(userId, recommendations);
    }
}

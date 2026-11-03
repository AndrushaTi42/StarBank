package com.star.bank.service;

import com.star.bank.dto.RecommendationDto;
import com.star.bank.dto.UserRecommendationsDto;
import com.star.bank.dto.UserStats;
import com.star.bank.entity.QueryEntity; // Проверь путь!
import com.star.bank.entity.RuleEntity;  // Проверь путь!
import com.star.bank.repository.DynamicRuleCheckRepository;
import com.star.bank.repository.RecommendationRepository;
import com.star.bank.repository.RuleRepository;
import com.star.bank.rules.RecommendationRuleSet;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RecommendationService {

    private final RecommendationRepository oldRepository;
    private final DynamicRuleCheckRepository dynamicCheckRepository;
    private final RuleRepository ruleRepository; // Должен быть RuleRepository!
    private final List<RecommendationRuleSet> fixedRuleSets;

    public RecommendationService(RecommendationRepository oldRepository,
                                 DynamicRuleCheckRepository dynamicCheckRepository,
                                 RuleRepository ruleRepository, // Исправлено
                                 List<RecommendationRuleSet> fixedRuleSets) {
        this.oldRepository = oldRepository;
        this.dynamicCheckRepository = dynamicCheckRepository;
        this.ruleRepository = ruleRepository; // Исправлено
        this.fixedRuleSets = fixedRuleSets;
    }

    public UserRecommendationsDto getRecommendations(UUID userId) {
        if (!oldRepository.userExists(userId)) {
            throw new IllegalArgumentException("Пользователь с ID " + userId + " не найден");
        }

        List<RecommendationDto> recommendations = new ArrayList<>();

        // 1. Проверяем СТАРЫЕ фиксированные правила (как было в 1-м этапе)
        UserStats stats = oldRepository.getUserStats(userId);
        fixedRuleSets.stream()
                .map(rule -> rule.check(userId, stats))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(recommendations::add);

        // 2. Проверяем НОВЫЕ динамические правила из БД
        List<RuleEntity> dynamicRules = ruleRepository.findAll();
        for (RuleEntity rule : dynamicRules) {
            if (evaluateRule(rule, userId)) {
                recommendations.add(new RecommendationDto(
                        rule.getProductId(),
                        rule.getProductName(),
                        rule.getProductText()
                ));
            }
        }

        return new UserRecommendationsDto(userId, recommendations);
    }

    private boolean evaluateRule(RuleEntity rule, UUID userId) {
        for (QueryEntity query : rule.getRule()) {
            boolean result = executeQuery(query, userId);
            if (query.isNegate()) { // Или query.getNegate() если Lombok не используется
                result = !result;
            }
            if (!result) {
                return false;
            }
        }
        return true;
    }

    private boolean executeQuery(QueryEntity q, UUID userId) {
        List<String> args = q.getArguments();
        // Обрати внимание: используем dynamicCheckRepository, а не oldRepository
        return switch (q.getQuery()) {
            case "USER_OF" -> dynamicCheckRepository.isUserOf(userId, args.get(0), false);
            case "ACTIVE_USER_OF" -> dynamicCheckRepository.isUserOf(userId, args.get(0), true);
            case "TRANSACTION_SUM_COMPARE" -> {
                long sum = dynamicCheckRepository.getSum(userId, args.get(0), args.get(1));
                yield compare(sum, args.get(2), Long.parseLong(args.get(3)));
            }
            case "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW" -> {
                long[] dw = dynamicCheckRepository.getDepositWithdraw(userId, args.get(0));
                yield compare(dw[0], args.get(1), dw[1]);
            }
            default -> false;
        };
    }

    private boolean compare(long a, String operator, long b) {
        return switch (operator) {
            case ">" -> a > b;
            case "<" -> a < b;
            case "=" -> a == b;
            case ">=" -> a >= b;
            case "<=" -> a <= b;
            default -> throw new IllegalArgumentException("Unknown operator: " + operator);
        };
    }
}

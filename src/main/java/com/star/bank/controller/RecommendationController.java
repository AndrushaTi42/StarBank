package com.star.bank.controller;

import com.star.bank.dto.UserRecommendationsDto;
import com.star.bank.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/recommendation")
public class RecommendationController {
    private final RecommendationService service;

    public RecommendationController(RecommendationService service) {
        this.service = service;
    }

    /**
     * Получает список персональных рекомендаций для пользователя.
     * Включает в себя как статические, так и динамические правила из БД.
     *
     * @param userId уникальный идентификатор пользователя (UUID)
     * @return HTTP-ответ со списком рекомендованных продуктов
     * @throws IllegalArgumentException если пользователь с таким ID не найден (обрабатывается GlobalExceptionHandler)
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserRecommendationsDto> getRecommendations(@PathVariable UUID userId) {
        return ResponseEntity.ok(service.getRecommendations(userId));
    }
}

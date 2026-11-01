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

    @GetMapping("/{userId}")
    public ResponseEntity<UserRecommendationsDto> getRecommendations(@PathVariable UUID userId) {
        return ResponseEntity.ok(service.getRecommendations(userId));
    }
}

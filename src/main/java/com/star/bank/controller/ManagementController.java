package com.star.bank.controller;

import com.star.bank.repository.jdbc.DynamicRuleCheckRepository;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/management")
public class ManagementController {
    private final DynamicRuleCheckRepository repository;
    private final BuildProperties buildProperties;

    public ManagementController(DynamicRuleCheckRepository repository,
                                BuildProperties buildProperties) {
        this.repository = repository;
        this.buildProperties = buildProperties;
    }

    /**
     * Очищает все кеши рекомендаций (Caffeine).
     * Используется внешними системами при необходимости сбросить устаревшие данные.
     *
     * @return HTTP-ответ со статусом 200 OK
     */
    @PostMapping("/clear-caches")
    public ResponseEntity<Void> clearCaches() {
        repository.clearAllCaches();
        return ResponseEntity.ok().build();
    }

    /**
     * Возвращает метаданные о сервисе (название и версию), извлеченные из pom.xml при сборке.
     *
     * @return JSON с полями "name" и "version"
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> getInfo() {
        return ResponseEntity.ok(Map.of(
                "name", Objects.requireNonNull(buildProperties.getArtifact()),
                "version", Objects.requireNonNull(buildProperties.getVersion())
        ));
    }
}

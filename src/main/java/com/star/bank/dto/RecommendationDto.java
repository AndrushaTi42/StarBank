package com.star.bank.dto;

import java.util.UUID;

public class RecommendationDto {
    private UUID id;
    private String name;
    private String text;

    public RecommendationDto(UUID id, String name, String text) {
        this.id = id;
        this.name = name;
        this.text = text;
    }

    // Геттеры
    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getText() { return text; }
}

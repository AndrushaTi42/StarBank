package com.star.bank.controller;

import com.star.bank.dto.QueryDto;
import com.star.bank.dto.RuleDto;
import com.star.bank.entity.QueryEntity;
import com.star.bank.entity.RuleEntity;
import com.star.bank.repository.RuleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rule")
public class RuleController {

    private final RuleRepository ruleRepository;

    public RuleController(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    // 1. POST: Создание правила
    @PostMapping
    public ResponseEntity<RuleDto> createRule(@RequestBody RuleDto ruleDto) {
        RuleEntity entity = toEntity(ruleDto);
        RuleEntity savedEntity = ruleRepository.save(entity);
        return ResponseEntity.ok(toDto(savedEntity));
    }

    // 2. GET: Получение всех правил
    @GetMapping
    public ResponseEntity<Map<String, List<RuleDto>>> getAllRules() {
        // 1. Достаем все сущности из БД
        List<RuleEntity> entities = ruleRepository.findAll();

        // 2. Превращаем их в DTO
        List<RuleDto> dtos = entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("data", dtos));
    }

    // 3. DELETE: Удаление правила
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        ruleRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    // ==========================================
    // ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ (Маппинг Entity <-> DTO)
    // ==========================================

    // Из DTO в Entity
    private RuleEntity toEntity(RuleDto dto) {
        RuleEntity entity = new RuleEntity();
        entity.setProductName(dto.productName());
        entity.setProductId(dto.productId());
        entity.setProductText(dto.productText());

        List<QueryEntity> queryEntities = dto.rule().stream().map(qDto -> {
            QueryEntity q = new QueryEntity();
            q.setQuery(qDto.query());
            q.setArguments(qDto.arguments());
            q.setNegate(qDto.negate());
            return q;
        }).collect(Collectors.toList());

        entity.setRule(queryEntities);
        return entity;
    }

    // Из Entity в DTO
    private RuleDto toDto(RuleEntity entity) {
        List<QueryDto> queryDtos = entity.getRule().stream().map(q ->
                new QueryDto(q.getQuery(), q.getArguments(), q.isNegate())
        ).collect(Collectors.toList());

        return new RuleDto(
                entity.getId(),
                entity.getProductName(),
                entity.getProductId(),
                entity.getProductText(),
                queryDtos
        );
    }
}
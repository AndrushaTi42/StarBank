package com.star.bank.controller;

import com.star.bank.dto.QueryDto;
import com.star.bank.dto.RuleDto;
import com.star.bank.dto.RuleStatDto;
import com.star.bank.entity.QueryEntity;
import com.star.bank.entity.RuleEntity;
import com.star.bank.entity.RuleStatEntity;
import com.star.bank.repository.jpa.RuleRepository;
import com.star.bank.repository.jpa.RuleStatRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rule")
public class RuleController {

    private final RuleRepository ruleRepository;
    private final RuleStatRepository statRepository;

    public RuleController(RuleRepository ruleRepository,
                          RuleStatRepository statRepository) {
        this.ruleRepository = ruleRepository;
        this.statRepository = statRepository;
    }

    /**
     * Создает новое динамическое правило рекомендаций и сохраняет его в PostgreSQL.
     *
     * @param ruleDto DTO с данными о продукте и набором логических запросов (правил)
     * @return HTTP-ответ с сохраненным правилом, включая присвоенный базой ID
     */
    @PostMapping
    public ResponseEntity<RuleDto> createRule(@RequestBody RuleDto ruleDto) {
        RuleEntity entity = toEntity(ruleDto);
        RuleEntity savedEntity = ruleRepository.save(entity);
        return ResponseEntity.ok(toDto(savedEntity));
    }

    /**
     * Возвращает список всех динамических правил, сохраненных в базе данных.
     *
     * @return HTTP-ответ в формате {"data": [...]} со списком всех правил
     */
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

    /**
     * Удаляет динамическое правило из базы данных по его ID.
     * Вместе с правилом каскадно удаляется статистика его срабатываний.
     *
     * @param id уникальный идентификатор правила
     * @return HTTP-ответ со статусом 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        ruleRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * Получает статистику срабатывания всех динамических правил.
     * Правила, которые еще не срабатывали, возвращаются со значением count = 0.
     *
     * @return HTTP-ответ в формате {"stats": [{"rule_id": ..., "count": ...}]}
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, List<RuleStatDto>>> getStats() {
        List<RuleEntity> allRules = ruleRepository.findAll();

        List<RuleStatDto> stats = allRules.stream().map(rule -> {
            long count = statRepository.findByRuleId(rule.getId())
                    .map(RuleStatEntity::getHitCount)
                    .orElse(0L);
            return new RuleStatDto(rule.getId(), count);
        }).collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("stats", stats));
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
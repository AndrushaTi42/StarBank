package com.star.bank.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TestController {

    private final JdbcTemplate jdbcTemplate;

    public TestController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/test-db")
    public List<Map<String, Object>> testDb() {
        // Получаем структуру колонок для наших таблиц
        String sql = "SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE " +
                "FROM INFORMATION_SCHEMA.COLUMNS " +
                "WHERE TABLE_SCHEMA = 'PUBLIC' " +
                "AND TABLE_NAME IN ('USERS', 'PRODUCTS', 'TRANSACTIONS') " +
                "ORDER BY TABLE_NAME, ORDINAL_POSITION";
        return jdbcTemplate.queryForList(sql);
    }

    @GetMapping("/products")
    public List<Map<String, Object>> getProducts() {
        return jdbcTemplate.queryForList("SELECT * FROM PRODUCTS");
    }

    @GetMapping("/transactions")
    public List<Map<String, Object>> getTransactions() {
        return jdbcTemplate.queryForList("SELECT * FROM TRANSACTIONS LIMIT 10");
    }
}

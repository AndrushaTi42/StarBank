package com.star.bank.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class RecommendationRepository {
    private final JdbcTemplate jdbcTemplate;

    public RecommendationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Object> getUserStats(UUID userId) {
        // Мы считаем сумму пополнений и трат по DEBIT, сумму по SAVING,
        // а также количество уникальных продуктов определенных типов
        String sql = """
            SELECT 
                COALESCE(SUM(CASE WHEN p.TYPE = 'DEBIT' AND t.TYPE = 'DEPOSIT' THEN t.AMOUNT ELSE 0 END), 0) as debit_deposit,
                COALESCE(SUM(CASE WHEN p.TYPE = 'DEBIT' AND t.TYPE = 'WITHDRAW' THEN t.AMOUNT ELSE 0 END), 0) as debit_withdraw,
                COALESCE(SUM(CASE WHEN p.TYPE = 'SAVING' AND t.TYPE = 'DEPOSIT' THEN t.AMOUNT ELSE 0 END), 0) as saving_deposit,
                COUNT(DISTINCT CASE WHEN p.TYPE = 'DEBIT' THEN p.ID END) as debit_count,
                COUNT(DISTINCT CASE WHEN p.TYPE = 'CREDIT' THEN p.ID END) as credit_count,
                COUNT(DISTINCT CASE WHEN p.TYPE = 'INVEST' THEN p.ID END) as invest_count
            FROM TRANSACTIONS t
            JOIN PRODUCTS p ON t.PRODUCT_ID = p.ID
            WHERE t.USER_ID = ?
        """;

        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, userId);

        // Если у пользователя вообще нет транзакций, queryForList может вернуть пустой список (или строку с нулями в зависимости от СУБД)
        // Поэтому если пусто, отдаем нули.
        if (result.isEmpty()) {
            Map<String, Object> emptyStats = new HashMap<>();
            emptyStats.put("debit_deposit", 0);
            emptyStats.put("debit_withdraw", 0);
            emptyStats.put("saving_deposit", 0);
            emptyStats.put("debit_count", 0);
            emptyStats.put("credit_count", 0);
            emptyStats.put("invest_count", 0);
            return emptyStats;
        }
        return result.get(0);
    }

    public boolean userExists(UUID userId) {
        String sql = "SELECT COUNT(*) FROM USERS WHERE ID = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count != null && count > 0;
    }
}

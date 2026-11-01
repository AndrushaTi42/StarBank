package com.star.bank.repository;

import com.star.bank.dto.UserStats;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class RecommendationRepository {
    private final JdbcTemplate jdbcTemplate;

    public RecommendationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public UserStats getUserStats(UUID userId) {

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

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new UserStats(
                rs.getLong("debit_deposit"),
                rs.getLong("debit_withdraw"),
                rs.getLong("saving_deposit"),
                rs.getInt("debit_count"),
                rs.getInt("credit_count"),
                rs.getInt("invest_count")
        ), userId);
    }

    public boolean userExists(UUID userId) {
        String sql = "SELECT COUNT(*) FROM USERS WHERE ID = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count != null && count > 0;
    }
}

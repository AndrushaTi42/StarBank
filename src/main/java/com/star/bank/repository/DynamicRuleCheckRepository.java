package com.star.bank.repository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Repository
public class DynamicRuleCheckRepository {

    private final JdbcTemplate jdbcTemplate;


    private final Cache<String, Boolean> userOfCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    private final Cache<String, Long> sumCompareCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    private final Cache<String, long[]> depositWithdrawCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    public DynamicRuleCheckRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 1. Для USER_OF и ACTIVE_USER_OF
    public boolean isUserOf(UUID userId, String productType, boolean active) {
        String cacheKey = userId + "_" + productType + "_" + active;
        return Boolean.TRUE.equals(userOfCache.get(cacheKey, k -> {
            String sql = """
            SELECT COUNT(t.id) 
            FROM TRANSACTIONS t 
            JOIN PRODUCTS p ON t.PRODUCT_ID = p.ID 
            WHERE t.USER_ID = ? AND p.TYPE = ?
        """;
            Long count = jdbcTemplate.queryForObject(sql, Long.class, userId, productType);
            if (count == null) return false;
            return active ? (count >= 5) : (count > 0);
        }));
    }

    // 2. Для TRANSACTION_SUM_COMPARE
    public long getSum(UUID userId, String productType, String txType) {
        String cacheKey = userId + "_" + productType + "_" + txType;
        return sumCompareCache.get(cacheKey, k -> {
            String sql = """
                        SELECT COALESCE(SUM(t.AMOUNT), 0) 
                        FROM TRANSACTIONS t 
                        JOIN PRODUCTS p ON t.PRODUCT_ID = p.ID 
                        WHERE t.USER_ID = ? AND p.TYPE = ? AND t.TYPE = ?
                    """;
            return jdbcTemplate.queryForObject(sql, Long.class, userId, productType, txType);
        });
    }

    // 3. Для TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW
    public long[] getDepositWithdraw(UUID userId, String productType) {
        String cacheKey = userId + "_" + productType;
        return depositWithdrawCache.get(cacheKey, k -> {
            String sql = """
                        SELECT 
                            COALESCE(SUM(CASE WHEN t.TYPE = 'DEPOSIT' THEN t.AMOUNT ELSE 0 END), 0) as deposit,
                            COALESCE(SUM(CASE WHEN t.TYPE = 'WITHDRAW' THEN t.AMOUNT ELSE 0 END), 0) as withdraw
                        FROM TRANSACTIONS t 
                        JOIN PRODUCTS p ON t.PRODUCT_ID = p.ID 
                        WHERE t.USER_ID = ? AND p.TYPE = ?
                    """;
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new long[]{
                    rs.getLong("deposit"),
                    rs.getLong("withdraw")
            }, userId, productType);
        });
    }
}
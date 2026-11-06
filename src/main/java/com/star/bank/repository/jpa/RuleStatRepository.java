package com.star.bank.repository.jpa;

import com.star.bank.entity.RuleStatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

public interface RuleStatRepository extends JpaRepository<RuleStatEntity, Long> {

    Optional<RuleStatEntity> findByRuleId(Long ruleId);


    @Modifying
    @Transactional
    @Query("UPDATE RuleStatEntity r SET r.hitCount = r.hitCount + 1 WHERE r.rule.id = :ruleId")
    void incrementCount(@Param("ruleId") Long ruleId);
}

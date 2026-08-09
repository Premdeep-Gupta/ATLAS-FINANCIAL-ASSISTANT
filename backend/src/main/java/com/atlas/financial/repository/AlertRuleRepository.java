package com.atlas.financial.repository;

import com.atlas.financial.model.AlertRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRuleRepository extends JpaRepository<AlertRule, Long> {
    List<AlertRule> findByUserId(Long userId);
    List<AlertRule> findByUserIdAndActiveTrue(Long userId);
}

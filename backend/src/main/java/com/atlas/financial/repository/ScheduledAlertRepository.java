package com.atlas.financial.repository;

import com.atlas.financial.model.ScheduledAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ScheduledAlertRepository extends JpaRepository<ScheduledAlert, Long> {
    List<ScheduledAlert> findByUserId(Long userId);
}

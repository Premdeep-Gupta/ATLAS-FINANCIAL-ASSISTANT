package com.atlas.financial.repository;

import com.atlas.financial.model.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {
    Optional<UserPreference> findByUserId(Long userId);
    List<UserPreference> findByBriefingTime(String briefingTime);
}

package com.atlas.financial.repository;

import com.atlas.financial.model.ConversationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConversationHistoryRepository extends JpaRepository<ConversationHistory, Long> {
    List<ConversationHistory> findTop10ByUserIdOrderByTimestampDesc(Long userId);
    List<ConversationHistory> findByUserIdOrderByTimestampAsc(Long userId);
    void deleteByUserId(Long userId);
}

package com.atlas.financial.repository;

import com.atlas.financial.model.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {
    List<Watchlist> findByUserId(Long userId);
    Optional<Watchlist> findByUserIdAndTicker(Long userId, String ticker);
    void deleteByUserIdAndTicker(Long userId, String ticker);
    void deleteByUserId(Long userId);
}

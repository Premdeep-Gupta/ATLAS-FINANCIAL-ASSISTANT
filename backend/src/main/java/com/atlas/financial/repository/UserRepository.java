package com.atlas.financial.repository;

import com.atlas.financial.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByTelegramId(Long telegramId);
}

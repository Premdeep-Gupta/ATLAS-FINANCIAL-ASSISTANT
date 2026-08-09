package com.atlas.financial.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "telegram_id")
    private Long telegramId;

    private String username;
    private String firstName;
    private String role;
    
    private Boolean onboardingCompleted = false;
    private String onboardingStep = "role"; // role -> watchlist -> briefing_time -> completed

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    public User() {}

    public User(Long telegramId, String username, String firstName, String role, Boolean onboardingCompleted, String onboardingStep, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.telegramId = telegramId;
        this.username = username;
        this.firstName = firstName;
        this.role = role;
        this.onboardingCompleted = onboardingCompleted != null ? onboardingCompleted : false;
        this.onboardingStep = onboardingStep != null ? onboardingStep : "role";
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public static class UserBuilder {
        private Long telegramId;
        private String username;
        private String firstName;
        private String role;
        private Boolean onboardingCompleted = false;
        private String onboardingStep = "role";
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime updatedAt = LocalDateTime.now();

        public UserBuilder telegramId(Long telegramId) { this.telegramId = telegramId; return this; }
        public UserBuilder username(String username) { this.username = username; return this; }
        public UserBuilder firstName(String firstName) { this.firstName = firstName; return this; }
        public UserBuilder role(String role) { this.role = role; return this; }
        public UserBuilder onboardingCompleted(Boolean onboardingCompleted) { this.onboardingCompleted = onboardingCompleted; return this; }
        public UserBuilder onboardingStep(String onboardingStep) { this.onboardingStep = onboardingStep; return this; }
        public UserBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public UserBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public User build() {
            return new User(telegramId, username, firstName, role, onboardingCompleted, onboardingStep, createdAt, updatedAt);
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getTelegramId() { return telegramId; }
    public void setTelegramId(Long telegramId) { this.telegramId = telegramId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Boolean getOnboardingCompleted() { return onboardingCompleted; }
    public void setOnboardingCompleted(Boolean onboardingCompleted) { this.onboardingCompleted = onboardingCompleted; }

    public String getOnboardingStep() { return onboardingStep; }
    public void setOnboardingStep(String onboardingStep) { this.onboardingStep = onboardingStep; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

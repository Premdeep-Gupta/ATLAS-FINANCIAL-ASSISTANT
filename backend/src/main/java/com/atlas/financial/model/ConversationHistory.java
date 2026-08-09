package com.atlas.financial.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "conversation_histories")
public class ConversationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String role; // "user" or "model"

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private LocalDateTime timestamp = LocalDateTime.now();

    public ConversationHistory() {}

    public ConversationHistory(Long id, Long userId, String role, String content, LocalDateTime timestamp) {
        this.id = id;
        this.userId = userId;
        this.role = role;
        this.content = content;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    }

    public static ConversationHistoryBuilder builder() {
        return new ConversationHistoryBuilder();
    }

    public static class ConversationHistoryBuilder {
        private Long id;
        private Long userId;
        private String role;
        private String content;
        private LocalDateTime timestamp = LocalDateTime.now();

        public ConversationHistoryBuilder id(Long id) { this.id = id; return this; }
        public ConversationHistoryBuilder userId(Long userId) { this.userId = userId; return this; }
        public ConversationHistoryBuilder role(String role) { this.role = role; return this; }
        public ConversationHistoryBuilder content(String content) { this.content = content; return this; }
        public ConversationHistoryBuilder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }

        public ConversationHistory build() {
            return new ConversationHistory(id, userId, role, content, timestamp);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}

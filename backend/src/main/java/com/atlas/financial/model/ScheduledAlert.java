package com.atlas.financial.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "scheduled_alerts")
public class ScheduledAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String ticker;

    private Double thresholdPercent = 5.0;
    private LocalDateTime lastTriggeredAt;

    public ScheduledAlert() {}

    public ScheduledAlert(Long id, Long userId, String ticker, Double thresholdPercent, LocalDateTime lastTriggeredAt) {
        this.id = id;
        this.userId = userId;
        this.ticker = ticker;
        this.thresholdPercent = thresholdPercent != null ? thresholdPercent : 5.0;
        this.lastTriggeredAt = lastTriggeredAt;
    }

    public static ScheduledAlertBuilder builder() {
        return new ScheduledAlertBuilder();
    }

    public static class ScheduledAlertBuilder {
        private Long id;
        private Long userId;
        private String ticker;
        private Double thresholdPercent = 5.0;
        private LocalDateTime lastTriggeredAt;

        public ScheduledAlertBuilder id(Long id) { this.id = id; return this; }
        public ScheduledAlertBuilder userId(Long userId) { this.userId = userId; return this; }
        public ScheduledAlertBuilder ticker(String ticker) { this.ticker = ticker; return this; }
        public ScheduledAlertBuilder thresholdPercent(Double thresholdPercent) { this.thresholdPercent = thresholdPercent; return this; }
        public ScheduledAlertBuilder lastTriggeredAt(LocalDateTime lastTriggeredAt) { this.lastTriggeredAt = lastTriggeredAt; return this; }

        public ScheduledAlert build() {
            return new ScheduledAlert(id, userId, ticker, thresholdPercent, lastTriggeredAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getTicker() { return ticker; }
    public void setTicker(String ticker) { this.ticker = ticker; }

    public Double getThresholdPercent() { return thresholdPercent; }
    public void setThresholdPercent(Double thresholdPercent) { this.thresholdPercent = thresholdPercent; }

    public LocalDateTime getLastTriggeredAt() { return lastTriggeredAt; }
    public void setLastTriggeredAt(LocalDateTime lastTriggeredAt) { this.lastTriggeredAt = lastTriggeredAt; }
}

package com.atlas.financial.model;

import jakarta.persistence.*;

@Entity
@Table(name = "alert_rules")
public class AlertRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String ticker;

    @Column(name = "threshold_percent")
    private Double thresholdPercent = 5.0;

    private String conditionType = "CHANGE_GREATER_THAN"; // CHANGE_GREATER_THAN, DROP_GREATER_THAN, RISE_GREATER_THAN

    private Boolean active = true;

    public AlertRule() {}

    public AlertRule(Long userId, String ticker, Double thresholdPercent, String conditionType, Boolean active) {
        this.userId = userId;
        this.ticker = ticker;
        this.thresholdPercent = thresholdPercent != null ? thresholdPercent : 5.0;
        this.conditionType = conditionType != null ? conditionType : "CHANGE_GREATER_THAN";
        this.active = active != null ? active : true;
    }

    public static AlertRuleBuilder builder() {
        return new AlertRuleBuilder();
    }

    public static class AlertRuleBuilder {
        private Long userId;
        private String ticker;
        private Double thresholdPercent = 5.0;
        private String conditionType = "CHANGE_GREATER_THAN";
        private Boolean active = true;

        public AlertRuleBuilder userId(Long userId) { this.userId = userId; return this; }
        public AlertRuleBuilder ticker(String ticker) { this.ticker = ticker; return this; }
        public AlertRuleBuilder thresholdPercent(Double thresholdPercent) { this.thresholdPercent = thresholdPercent; return this; }
        public AlertRuleBuilder conditionType(String conditionType) { this.conditionType = conditionType; return this; }
        public AlertRuleBuilder active(Boolean active) { this.active = active; return this; }

        public AlertRule build() {
            return new AlertRule(userId, ticker, thresholdPercent, conditionType, active);
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

    public String getConditionType() { return conditionType; }
    public void setConditionType(String conditionType) { this.conditionType = conditionType; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}

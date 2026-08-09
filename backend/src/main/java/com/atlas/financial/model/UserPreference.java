package com.atlas.financial.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_preferences")
public class UserPreference {
    @Id
    @Column(name = "user_id")
    private Long userId;

    private String briefingTime = "08:30 AM";
    private String timezone = "UTC";
    private String topics = "market_news,earnings,sec_filings";
    private String insightTypes = "Market News, Earnings Summaries, SEC Filings, Analyst Ratings, Macro Events";
    private String connectedAccounts = "Gmail, Google Calendar, Google Sheets";
    private String secondaryVerticals = "Technology, Startup Ecosystem";
    private Double alertThresholdPercent = 5.0;

    @Column(columnDefinition = "TEXT")
    private String financialInterests = "NVIDIA, AMD, AI, US stocks";

    @Column(columnDefinition = "TEXT")
    private String customAlerts = "Alert me if NVIDIA moves more than 5%";

    public UserPreference() {}

    public UserPreference(Long userId, String briefingTime, String timezone, String topics, String insightTypes, String connectedAccounts, String secondaryVerticals, Double alertThresholdPercent, String financialInterests, String customAlerts) {
        this.userId = userId;
        this.briefingTime = briefingTime != null ? briefingTime : "08:30 AM";
        this.timezone = timezone != null ? timezone : "UTC";
        this.topics = topics != null ? topics : "market_news,earnings,sec_filings";
        this.insightTypes = insightTypes != null ? insightTypes : "Market News, Earnings Summaries, SEC Filings, Analyst Ratings, Macro Events";
        this.connectedAccounts = connectedAccounts != null ? connectedAccounts : "Gmail, Google Calendar, Google Sheets";
        this.secondaryVerticals = secondaryVerticals != null ? secondaryVerticals : "Technology, Startup Ecosystem";
        this.alertThresholdPercent = alertThresholdPercent != null ? alertThresholdPercent : 5.0;
        this.financialInterests = financialInterests != null ? financialInterests : "NVIDIA, AMD, AI, US stocks";
        this.customAlerts = customAlerts != null ? customAlerts : "Alert me if NVIDIA moves more than 5%";
    }

    public static UserPreferenceBuilder builder() {
        return new UserPreferenceBuilder();
    }

    public static class UserPreferenceBuilder {
        private Long userId;
        private String briefingTime = "08:30 AM";
        private String timezone = "UTC";
        private String topics = "market_news,earnings,sec_filings";
        private String insightTypes = "Market News, Earnings Summaries, SEC Filings, Analyst Ratings, Macro Events";
        private String connectedAccounts = "Gmail, Google Calendar, Google Sheets";
        private String secondaryVerticals = "Technology, Startup Ecosystem";
        private Double alertThresholdPercent = 5.0;
        private String financialInterests = "NVIDIA, AMD, AI, US stocks";
        private String customAlerts = "Alert me if NVIDIA moves more than 5%";

        public UserPreferenceBuilder userId(Long userId) { this.userId = userId; return this; }
        public UserPreferenceBuilder briefingTime(String briefingTime) { this.briefingTime = briefingTime; return this; }
        public UserPreferenceBuilder timezone(String timezone) { this.timezone = timezone; return this; }
        public UserPreferenceBuilder topics(String topics) { this.topics = topics; return this; }
        public UserPreferenceBuilder insightTypes(String insightTypes) { this.insightTypes = insightTypes; return this; }
        public UserPreferenceBuilder connectedAccounts(String connectedAccounts) { this.connectedAccounts = connectedAccounts; return this; }
        public UserPreferenceBuilder secondaryVerticals(String secondaryVerticals) { this.secondaryVerticals = secondaryVerticals; return this; }
        public UserPreferenceBuilder alertThresholdPercent(Double alertThresholdPercent) { this.alertThresholdPercent = alertThresholdPercent; return this; }
        public UserPreferenceBuilder financialInterests(String financialInterests) { this.financialInterests = financialInterests; return this; }
        public UserPreferenceBuilder customAlerts(String customAlerts) { this.customAlerts = customAlerts; return this; }

        public UserPreference build() {
            return new UserPreference(userId, briefingTime, timezone, topics, insightTypes, connectedAccounts, secondaryVerticals, alertThresholdPercent, financialInterests, customAlerts);
        }
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getBriefingTime() { return briefingTime; }
    public void setBriefingTime(String briefingTime) { this.briefingTime = briefingTime; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    public String getTopics() { return topics; }
    public void setTopics(String topics) { this.topics = topics; }

    public String getInsightTypes() { return insightTypes; }
    public void setInsightTypes(String insightTypes) { this.insightTypes = insightTypes; }

    public String getConnectedAccounts() { return connectedAccounts; }
    public void setConnectedAccounts(String connectedAccounts) { this.connectedAccounts = connectedAccounts; }

    public String getSecondaryVerticals() { return secondaryVerticals; }
    public void setSecondaryVerticals(String secondaryVerticals) { this.secondaryVerticals = secondaryVerticals; }

    public Double getAlertThresholdPercent() { return alertThresholdPercent; }
    public void setAlertThresholdPercent(Double alertThresholdPercent) { this.alertThresholdPercent = alertThresholdPercent; }

    public String getFinancialInterests() { return financialInterests; }
    public void setFinancialInterests(String financialInterests) { this.financialInterests = financialInterests; }

    public String getCustomAlerts() { return customAlerts; }
    public void setCustomAlerts(String customAlerts) { this.customAlerts = customAlerts; }
}

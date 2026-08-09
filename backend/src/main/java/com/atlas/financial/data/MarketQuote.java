package com.atlas.financial.data;

import java.time.Instant;

public class MarketQuote {
    private String symbol;
    private String exchange;
    private double price;
    private double changePercent;
    private double open;
    private double high;
    private double low;
    private double previousClose;
    private long volume;
    private double relativeVolume;
    private String currency;
    private Instant timestamp;
    private String source;
    private String freshnessStatus;
    private double confidenceScore;
    private String verificationStatus;

    public MarketQuote() {}

    public MarketQuote(String symbol, String exchange, double price, double changePercent, double open, double high, double low, double previousClose, long volume, double relativeVolume, String currency, Instant timestamp, String source, String freshnessStatus, double confidenceScore, String verificationStatus) {
        this.symbol = symbol;
        this.exchange = exchange;
        this.price = price;
        this.changePercent = changePercent;
        this.open = open;
        this.high = high;
        this.low = low;
        this.previousClose = previousClose;
        this.volume = volume;
        this.relativeVolume = relativeVolume;
        this.currency = currency;
        this.timestamp = timestamp;
        this.source = source;
        this.freshnessStatus = freshnessStatus;
        this.confidenceScore = confidenceScore;
        this.verificationStatus = verificationStatus;
    }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getExchange() { return exchange; }
    public void setExchange(String exchange) { this.exchange = exchange; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getChangePercent() { return changePercent; }
    public void setChangePercent(double changePercent) { this.changePercent = changePercent; }

    public double getOpen() { return open; }
    public void setOpen(double open) { this.open = open; }

    public double getHigh() { return high; }
    public void setHigh(double high) { this.high = high; }

    public double getLow() { return low; }
    public void setLow(double low) { this.low = low; }

    public double getPreviousClose() { return previousClose; }
    public void setPreviousClose(double previousClose) { this.previousClose = previousClose; }

    public long getVolume() { return volume; }
    public void setVolume(long volume) { this.volume = volume; }

    public double getRelativeVolume() { return relativeVolume; }
    public void setRelativeVolume(double relativeVolume) { this.relativeVolume = relativeVolume; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getFreshnessStatus() { return freshnessStatus; }
    public void setFreshnessStatus(String freshnessStatus) { this.freshnessStatus = freshnessStatus; }

    public double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(double confidenceScore) { this.confidenceScore = confidenceScore; }

    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }
}

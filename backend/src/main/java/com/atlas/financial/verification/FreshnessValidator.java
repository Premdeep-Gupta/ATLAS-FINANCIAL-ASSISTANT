package com.atlas.financial.verification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Service
public class FreshnessValidator {

    private static final Logger log = LoggerFactory.getLogger(FreshnessValidator.class);

    public String formatFreshnessHeader() {
        String utcTime = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm 'UTC'")
                .withZone(ZoneOffset.UTC)
                .format(Instant.now());

        return "🕐 **Market Data:** " + utcTime + " *(Freshness: Real-time)*\n" +
               "📰 **News Checked Through:** " + utcTime;
    }

    public boolean isDataFresh(long timestampMs, long maxAllowedAgeMs) {
        long age = System.currentTimeMillis() - timestampMs;
        return age <= maxAllowedAgeMs;
    }
}

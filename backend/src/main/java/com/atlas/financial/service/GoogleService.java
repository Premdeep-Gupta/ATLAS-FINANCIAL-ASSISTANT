package com.atlas.financial.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GoogleService {

    private static final Logger log = LoggerFactory.getLogger(GoogleService.class);

    public List<Map<String, String>> searchEmails(String query) {
        log.info("Searching Gmail inbox for query: {}", query);
        List<Map<String, String>> emails = new ArrayList<>();

        emails.add(Map.of(
                "id", "msg_101",
                "sender", "ir@tesla.com",
                "subject", "Tesla Q2 Earnings Highlights & Investor Presentation",
                "date", "2026-08-08 18:30",
                "snippet", "Enclosed is the investor letter for Q2 2026. Revenues grew 12% YoY to $28.5B, operating margin at 9.2%. Full-year guidance remains unchanged..."
        ));

        emails.add(Map.of(
                "id", "msg_102",
                "sender", "chief.analyst@hedgefund.com",
                "subject", "URGENT: Nvidia Valuation and Supply Chain Update",
                "date", "2026-08-09 08:00",
                "snippet", "Please check Nvidia's current trading multiples. Blackwell chip demand is accelerating faster than expected. Let's discuss in the afternoon meeting."
        ));

        emails.add(Map.of(
                "id", "msg_103",
                "sender", "alerts@crunchbase.com",
                "subject", "Acquisition Alert: Stripe Acquires Enterprise FinTech Startup",
                "date", "2026-08-09 09:15",
                "snippet", "Stripe announces purchase of LedgerFlow for $250M to bolster enterprise automated reconciliation tools..."
        ));

        if (query == null || query.isBlank()) {
            return emails;
        }

        String qLower = query.toLowerCase();
        List<Map<String, String>> filtered = new ArrayList<>();
        for (Map<String, String> email : emails) {
            if (email.get("subject").toLowerCase().contains(qLower) ||
                email.get("snippet").toLowerCase().contains(qLower) ||
                email.get("sender").toLowerCase().contains(qLower)) {
                filtered.add(email);
            }
        }
        return filtered.isEmpty() ? emails.subList(0, 2) : filtered;
    }

    public Map<String, Object> scheduleMeeting(String summary, String startTime, int durationMinutes) {
        log.info("Scheduling Google Calendar meeting: '{}' at {} for {} mins", summary, startTime, durationMinutes);
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("summary", summary);
        result.put("startTime", startTime);
        result.put("duration", durationMinutes + " minutes");
        result.put("eventLink", "https://calendar.google.com/calendar/event?eid=mock_atlas_event_" + System.currentTimeMillis());
        result.put("message", "Successfully scheduled meeting '" + summary + "' on Google Calendar.");
        return result;
    }

    public List<List<String>> readGoogleSheet(String sheetId, String rangeName) {
        log.info("Reading Google Sheet ID: {} Range: {}", sheetId, rangeName);
        return List.of(
                List.of("Metric", "Q1 Actual", "Q2 Actual", "Q3 Forecast", "Q4 Forecast"),
                List.of("Revenue", "$1,200,000", "$1,350,000", "$1,500,000", "$1,650,000"),
                List.of("COGS", "$450,000", "$500,000", "$550,000", "$600,000"),
                List.of("Marketing", "$200,000", "$250,000", "$220,000", "$240,000"),
                List.of("Net Income", "$550,000", "$600,000", "$730,000", "$810,000")
        );
    }
}

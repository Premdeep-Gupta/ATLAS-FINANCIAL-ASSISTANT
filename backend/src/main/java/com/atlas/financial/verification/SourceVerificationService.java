package com.atlas.financial.verification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SourceVerificationService {

    private static final Logger log = LoggerFactory.getLogger(SourceVerificationService.class);

    public enum SourceTier {
        TIER_1_PRIMARY("Tier 1 — Primary Source (SEC EDGAR / Official IR)", 1.0),
        TIER_2_HIGH_QUALITY("Tier 2 — High-Quality Institutional Source (Reuters / Bloomberg)", 0.85),
        TIER_3_SECONDARY("Tier 3 — Secondary Financial Feed", 0.65);

        private final String label;
        private final double weight;

        SourceTier(String label, double weight) {
            this.label = label;
            this.weight = weight;
        }

        public String getLabel() { return label; }
        public double getWeight() { return weight; }
    }

    public SourceTier classifySourceTier(String sourceName) {
        if (sourceName == null) return SourceTier.TIER_3_SECONDARY;
        String lower = sourceName.toLowerCase();

        if (lower.contains("sec") || lower.contains("edgar") || lower.contains("10-k") || lower.contains("10-q") || lower.contains("8-k") || lower.contains("investor relations") || lower.contains("ir")) {
            return SourceTier.TIER_1_PRIMARY;
        }

        if (lower.contains("reuters") || lower.contains("bloomberg") || lower.contains("wsj") || lower.contains("financial times") || lower.contains("yahoo finance")) {
            return SourceTier.TIER_2_HIGH_QUALITY;
        }

        return SourceTier.TIER_3_SECONDARY;
    }

    public double calculateConfidenceScore(List<String> sources) {
        if (sources == null || sources.isEmpty()) return 7.5;
        double totalWeight = 0;
        for (String src : sources) {
            totalWeight += classifySourceTier(src).getWeight();
        }
        double avg = totalWeight / sources.size();
        return Math.min(9.8, Math.max(6.0, avg * 9.8));
    }
}

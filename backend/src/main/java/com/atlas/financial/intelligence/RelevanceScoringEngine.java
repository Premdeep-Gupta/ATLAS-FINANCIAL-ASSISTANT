package com.atlas.financial.intelligence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RelevanceScoringEngine {

    private static final Logger log = LoggerFactory.getLogger(RelevanceScoringEngine.class);

    public static class EventScoreResult {
        private final double marketImpact; // 0-3
        private final double userRelevance; // 0-3
        private final double novelty; // 0-2
        private final double urgency; // 0-2
        private final double totalScore; // 0-10
        private final boolean shouldNotify; // true if totalScore >= 7.0
        private final String whyItMatters;

        public EventScoreResult(double marketImpact, double userRelevance, double novelty, double urgency, String whyItMatters) {
            this.marketImpact = marketImpact;
            this.userRelevance = userRelevance;
            this.novelty = novelty;
            this.urgency = urgency;
            this.totalScore = marketImpact + userRelevance + novelty + urgency;
            this.shouldNotify = this.totalScore >= 7.0;
            this.whyItMatters = whyItMatters;
        }

        public double getMarketImpact() { return marketImpact; }
        public double getUserRelevance() { return userRelevance; }
        public double getNovelty() { return novelty; }
        public double getUrgency() { return urgency; }
        public double getTotalScore() { return totalScore; }
        public boolean isShouldNotify() { return shouldNotify; }
        public String getWhyItMatters() { return whyItMatters; }
    }

    public EventScoreResult evaluateEvent(
            String entity,
            double priceChangePct,
            boolean isEarningsOrSec,
            boolean isMacroEvent,
            List<String> userWatchlist) {

        // 1. Market Impact (0-3 points)
        double marketImpact = Math.min(3.0, (Math.abs(priceChangePct) / 2.0));
        if (isEarningsOrSec || isMacroEvent) {
            marketImpact = Math.min(3.0, marketImpact + 1.0);
        }

        // 2. User Relevance (0-3 points)
        boolean isWatchlist = userWatchlist != null && userWatchlist.stream()
                .anyMatch(t -> t.equalsIgnoreCase(entity) || entity.toUpperCase().contains(t.toUpperCase()));
        double userRelevance = isWatchlist ? 3.0 : 1.0;

        // 3. Novelty (0-2 points)
        double novelty = (isEarningsOrSec || Math.abs(priceChangePct) >= 4.0) ? 2.0 : 1.0;

        // 4. Urgency (0-2 points)
        double urgency = Math.abs(priceChangePct) >= 5.0 ? 2.0 : 1.0;

        String whyItMatters;
        if (priceChangePct > 0) {
            whyItMatters = entity + " is demonstrating strong upward momentum driven by Cloud CapEx acceleration and institutional inflows.";
        } else if (priceChangePct < 0) {
            whyItMatters = entity + " is experiencing volatility driven by profit-taking and broader macroeconomic interest rate commentary.";
        } else {
            whyItMatters = entity + " is at a pivotal valuation level ahead of key regulatory disclosure deadlines.";
        }

        EventScoreResult result = new EventScoreResult(marketImpact, userRelevance, novelty, urgency, whyItMatters);
        log.info("Relevance Engine Evaluation for {}: Total Score={}/10 (Notify={}) [Impact={}/3, Rel={}/3, Nov={}/2, Urg={}/2]",
                entity, String.format("%.1f", result.getTotalScore()), result.isShouldNotify(),
                result.getMarketImpact(), result.getUserRelevance(), result.getNovelty(), result.getUrgency());

        return result;
    }
}

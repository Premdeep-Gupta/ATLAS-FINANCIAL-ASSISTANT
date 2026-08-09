package com.atlas.financial.service;

import com.atlas.financial.intelligence.RelevanceScoringEngine;
import com.atlas.financial.intelligence.RelevanceScoringEngine.EventScoreResult;
import com.atlas.financial.model.User;
import com.atlas.financial.model.UserPreference;
import com.atlas.financial.model.Watchlist;
import com.atlas.financial.repository.UserPreferenceRepository;
import com.atlas.financial.repository.UserRepository;
import com.atlas.financial.repository.WatchlistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@EnableScheduling
public class SchedulerService {

    private static final Logger log = LoggerFactory.getLogger(SchedulerService.class);

    private final UserRepository userRepository;
    private final UserPreferenceRepository userPreferenceRepository;
    private final WatchlistRepository watchlistRepository;
    private final AIService aiService;
    private final FinancialService financialService;
    private final RelevanceScoringEngine scoringEngine;
    private final TelegramBotService telegramBotService;

    public SchedulerService(
            UserRepository userRepository,
            UserPreferenceRepository userPreferenceRepository,
            WatchlistRepository watchlistRepository,
            AIService aiService,
            FinancialService financialService,
            RelevanceScoringEngine scoringEngine,
            @Lazy TelegramBotService telegramBotService) {
        this.userRepository = userRepository;
        this.userPreferenceRepository = userPreferenceRepository;
        this.watchlistRepository = watchlistRepository;
        this.aiService = aiService;
        this.financialService = financialService;
        this.scoringEngine = scoringEngine;
        this.telegramBotService = telegramBotService;
    }

    @Scheduled(cron = "0 * * * * *") // Check every minute
    public void checkAndSendBriefings() {
        String currentTimeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        log.debug("Running briefing schedule check for time: {}", currentTimeStr);

        List<UserPreference> matchingPrefs = userPreferenceRepository.findByBriefingTime(currentTimeStr);
        for (UserPreference pref : matchingPrefs) {
            Optional<User> userOpt = userRepository.findByTelegramId(pref.getUserId());
            if (userOpt.isPresent() && Boolean.TRUE.equals(userOpt.get().getOnboardingCompleted())) {
                User user = userOpt.get();
                log.info("Triggering scheduled daily morning briefing for user {}", user.getTelegramId());
                sendDailyBriefingForUser(user);
            }
        }
    }

    @Scheduled(cron = "0 0 17 * * MON-FRI") // Evening Summary at 5:00 PM EST
    public void sendEveningSummaries() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (Boolean.TRUE.equals(user.getOnboardingCompleted())) {
                List<Watchlist> watchlists = watchlistRepository.findByUserId(user.getTelegramId());
                List<String> tickers = watchlists.stream().map(Watchlist::getTicker).collect(Collectors.toList());
                String summary = aiService.generateEveningSummary(user.getRole(), tickers);
                telegramBotService.sendMessage(user.getTelegramId(), summary);
            }
        }
    }

    @Scheduled(fixedRate = 300000) // Check every 5 minutes for price volatility & breaking news
    public void checkPriceAlerts() {
        log.debug("Running periodic stock price alerts check with Relevance Scoring Engine...");
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (Boolean.TRUE.equals(user.getOnboardingCompleted())) {
                List<Watchlist> watchlists = watchlistRepository.findByUserId(user.getTelegramId());
                List<String> userTickers = watchlists.stream().map(Watchlist::getTicker).collect(Collectors.toList());

                for (Watchlist item : watchlists) {
                    Map<String, Object> priceInfo = financialService.getStockPrice(item.getTicker());
                    if (priceInfo.containsKey("percent_change")) {
                        double priceChangePct = (Double) priceInfo.get("percent_change");

                        EventScoreResult scoreResult = scoringEngine.evaluateEvent(
                                item.getTicker(), priceChangePct, false, false, userTickers);

                        // Strict Silence Rule: Only notify if score >= 7.0
                        if (scoreResult.isShouldNotify()) {
                            String alertMsg = "📊 **WATCHLIST ALERT**\n\n" +
                                    "**" + item.getTicker() + "** " + (priceChangePct >= 0 ? "↑" : "↓") + " **" +
                                    String.format("%.1f", Math.abs(priceChangePct)) + "%** (Current: $" + priceInfo.get("price") + ")\n\n" +
                                    "Reason: Intraday price movement expanded beyond normal 20-day volatility range.\n\n" +
                                    "💡 **Why It Matters:**\n" + scoreResult.getWhyItMatters() + " *(Relevance Score: " +
                                    String.format("%.1f", scoreResult.getTotalScore()) + "/10)*";

                            telegramBotService.sendMessage(user.getTelegramId(), alertMsg);
                        } else {
                            log.debug("Event score for {} is {}/10 (< 7.0 threshold). REMAINING SILENT 🤫",
                                    item.getTicker(), String.format("%.1f", scoreResult.getTotalScore()));
                        }
                    }
                }
            }
        }
    }

    public void sendDailyBriefingForUser(User user) {
        List<Watchlist> watchlists = watchlistRepository.findByUserId(user.getTelegramId());
        List<String> tickers = watchlists.stream().map(Watchlist::getTicker).collect(Collectors.toList());
        UserPreference pref = userPreferenceRepository.findByUserId(user.getTelegramId()).orElse(null);

        String briefingText = aiService.generateMorningBriefing(user.getRole(), tickers, pref);
        telegramBotService.sendMessage(user.getTelegramId(), briefingText);
    }
}

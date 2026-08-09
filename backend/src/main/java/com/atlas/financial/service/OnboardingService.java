package com.atlas.financial.service;

import com.atlas.financial.model.User;
import com.atlas.financial.model.UserPreference;
import com.atlas.financial.model.Watchlist;
import com.atlas.financial.repository.UserPreferenceRepository;
import com.atlas.financial.repository.UserRepository;
import com.atlas.financial.repository.WatchlistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OnboardingService {

    private static final Logger log = LoggerFactory.getLogger(OnboardingService.class);

    private final UserRepository userRepository;
    private final UserPreferenceRepository userPreferenceRepository;
    private final WatchlistRepository watchlistRepository;

    public OnboardingService(
            UserRepository userRepository,
            UserPreferenceRepository userPreferenceRepository,
            WatchlistRepository watchlistRepository) {
        this.userRepository = userRepository;
        this.userPreferenceRepository = userPreferenceRepository;
        this.watchlistRepository = watchlistRepository;
    }

    @Transactional
    public String handleOnboardingStep(User user, String messageText) {
        String input = messageText.trim();
        String inputLower = input.toLowerCase();
        boolean isSkip = inputLower.contains("skip") || inputLower.contains("pass") || inputLower.contains("later") || inputLower.contains("next");

        String step = user.getOnboardingStep() != null ? user.getOnboardingStep() : "role";
        log.info("User {} onboarding step: {} input: {}", user.getTelegramId(), step, input);

        UserPreference pref = userPreferenceRepository.findByUserId(user.getTelegramId())
                .orElseGet(() -> {
                    UserPreference p = UserPreference.builder().userId(user.getTelegramId()).build();
                    return userPreferenceRepository.save(p);
                });

        // 1. ROLE STEP
        if ("role".equalsIgnoreCase(step)) {
            if (isSkip) {
                user.setRole("Finance Professional");
            } else {
                user.setRole(input);
            }
            user.setOnboardingStep("financial_interests");
            userRepository.save(user);

            return "Got it! Profile configured as **" + user.getRole() + "**.\n\n" +
                    "📈 **Step 2:** Which companies, sectors, or markets do you follow?\n" +
                    "*(Example: 'NVIDIA, AMD, AI, US stocks' — or reply 'skip')*";
        }

        // 2. FINANCIAL INTERESTS STEP
        else if ("financial_interests".equalsIgnoreCase(step)) {
            if (!isSkip) {
                pref.setFinancialInterests(input);
                userPreferenceRepository.save(pref);
            }
            user.setOnboardingStep("watchlist");
            userRepository.save(user);

            return "Recorded your financial interests! 🎯\n\n" +
                    "📊 **Step 3:** Anything specific you'd like me to monitor on your watchlist?\n" +
                    "*(Example: 'NVIDIA earnings, Tesla, semiconductor news' — or reply 'skip')*";
        }

        // 3. WATCHLIST STEP
        else if ("watchlist".equalsIgnoreCase(step)) {
            List<String> tickers = extractTickers(input);
            if (tickers.isEmpty() || isSkip) {
                tickers = List.of("NVDA", "AAPL", "TSLA");
            }

            for (String ticker : tickers) {
                if (watchlistRepository.findByUserIdAndTicker(user.getTelegramId(), ticker).isEmpty()) {
                    watchlistRepository.save(Watchlist.builder().userId(user.getTelegramId()).ticker(ticker).build());
                }
            }

            user.setOnboardingStep("insight_types");
            userRepository.save(user);

            return "Added **" + String.join(", ", tickers) + "** to your active watchlist! 🎯\n\n" +
                    "📰 **Step 4:** What type of financial insights are most valuable to you?\n" +
                    "• 📰 Market News\n" +
                    "• 📊 Earnings\n" +
                    "• 📄 SEC Filings\n" +
                    "• 📈 Analyst Ratings\n" +
                    "• 🌎 Macro Events\n\n" +
                    "*(Select multiple or reply 'skip')*";
        }

        // 4. INSIGHT TYPES STEP
        else if ("insight_types".equalsIgnoreCase(step)) {
            if (!isSkip) {
                pref.setInsightTypes(input);
                userPreferenceRepository.save(pref);
            }

            user.setOnboardingStep("briefing_time");
            userRepository.save(user);

            return "Insight preferences recorded! ⏱️\n\n" +
                    "⏰ **Step 5:** When should I send your daily briefing?\n" +
                    "• 8:00 AM\n" +
                    "• 8:30 AM\n" +
                    "• 9:00 AM\n" +
                    "• Custom time\n\n" +
                    "*(Reply with your preferred time or 'skip')*";
        }

        // 5. BRIEFING TIME STEP
        else if ("briefing_time".equalsIgnoreCase(step)) {
            if (!isSkip) {
                String timeStr = extractTime(input);
                pref.setBriefingTime(timeStr);
                userPreferenceRepository.save(pref);
            }

            user.setOnboardingStep("custom_alerts");
            userRepository.save(user);

            return "Daily briefing scheduled for **" + pref.getBriefingTime() + "**! ⏰\n\n" +
                    "🔔 **Step 6:** Would you like me to alert you about anything specific?\n" +
                    "*(Example: 'Alert me if NVIDIA moves more than 5%' — or reply 'skip')*";
        }

        // 6. CUSTOM ALERTS STEP
        else if ("custom_alerts".equalsIgnoreCase(step)) {
            if (!isSkip) {
                pref.setCustomAlerts(input);
                userPreferenceRepository.save(pref);
            }

            user.setOnboardingStep("google_integrations");
            userRepository.save(user);

            return "Alert preferences set! 🔔\n\n" +
                    "🔗 **Step 7:** I can also connect with your Google workspace to make your assistant more useful:\n" +
                    "• Connect Gmail\n" +
                    "• Connect Calendar\n" +
                    "• Connect Drive\n" +
                    "• Connect Sheets\n\n" +
                    "*(Reply with accounts to connect, or 'skip for now' to connect anytime from Settings)*";
        }

        // 7. GOOGLE INTEGRATIONS STEP
        else if ("google_integrations".equalsIgnoreCase(step)) {
            if (isSkip) {
                pref.setConnectedAccounts("None (Can connect from Settings)");
            } else {
                pref.setConnectedAccounts(input);
            }
            userPreferenceRepository.save(pref);

            user.setOnboardingStep("secondary_verticals");
            userRepository.save(user);

            return "Workspace integration recorded! 💼\n\n" +
                    "🌐 **Step 8:** Finance is your primary domain. Would you like to enable any secondary areas of interest?\n" +
                    "• ☑ Investing\n" +
                    "• ☑ Startup Ecosystem\n" +
                    "• Technology, Healthcare, Research, Legal, Productivity\n\n" +
                    "*(Reply with secondary interests or 'finance only')*";
        }

        // 8. SECONDARY VERTICALS & FINALIZATION STEP
        else if ("secondary_verticals".equalsIgnoreCase(step)) {
            if (!isSkip && !inputLower.contains("finance only")) {
                pref.setSecondaryVerticals(input);
                userPreferenceRepository.save(pref);
            }

            user.setOnboardingCompleted(true);
            user.setOnboardingStep("completed");
            userRepository.save(user);

            List<Watchlist> watchlists = watchlistRepository.findByUserId(user.getTelegramId());
            List<String> userTickers = new ArrayList<>();
            for (Watchlist w : watchlists) userTickers.add(w.getTicker());

            return "You're all set 🚀\n\n" +
                    "Welcome to **Atlas AI Financial Assistant**. I am now actively watching your market interests and learning your workflow preferences!\n\n" +
                    "• **Role:** " + user.getRole() + "\n" +
                    "• **Interests:** " + pref.getFinancialInterests() + "\n" +
                    "• **Watchlist:** " + String.join(", ", userTickers) + "\n" +
                    "• **Briefing Time:** " + pref.getBriefingTime() + "\n\n" +
                    "💡 You can now ask me any financial research, market comparison, or workspace document question!";
        }

        return "Welcome to **Atlas AI**. Ask me any financial question to get started!";
    }

    private List<String> extractTickers(String text) {
        List<String> tickers = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\b[A-Za-z]{2,5}\\b");
        Matcher matcher = pattern.matcher(text.toUpperCase());
        Set<String> ignore = Set.of("SKIP", "PASS", "NEXT", "LATER", "WITH", "THAT", "THIS", "SOME", "MORE", "THAN", "MOVE", "THEM", "FROM", "ONLY", "ALSO", "HAVE", "SOME", "NEED", "MAKE", "JUST", "SHOW", "WHAT", "WHEN", "WANT");

        while (matcher.find()) {
            String candidate = matcher.group();
            if (!ignore.contains(candidate)) {
                tickers.add(candidate);
            }
        }
        return tickers;
    }

    private String extractTime(String text) {
        Pattern pattern = Pattern.compile("(\\d{1,2}(:\\d{2})?\\s*(AM|PM|am|pm)?)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        return "08:30 AM";
    }
}

package com.atlas.financial.service;

import com.atlas.financial.creativity.AutonomousResearchAgent;
import com.atlas.financial.creativity.FinancialEventGraphEngine;
import com.atlas.financial.data.FinancialDataOrchestrator;
import com.atlas.financial.data.InsiderTransactionService;
import com.atlas.financial.integrations.calendar.CalendarService;
import com.atlas.financial.integrations.calendar.MeetingPreparationService;
import com.atlas.financial.integrations.gmail.GmailService;
import com.atlas.financial.memory.MemoryService;
import com.atlas.financial.model.ConversationHistory;
import com.atlas.financial.model.Watchlist;
import com.atlas.financial.portfolio.PortfolioRiskService;
import com.atlas.financial.repository.AlertRuleRepository;
import com.atlas.financial.repository.ConversationHistoryRepository;
import com.atlas.financial.repository.UserPreferenceRepository;
import com.atlas.financial.repository.WatchlistRepository;
import com.atlas.financial.research.CompanyResearchService;
import com.atlas.financial.research.CompetitorComparisonService;
import com.atlas.financial.research.EarningsAnalysisService;
import com.atlas.financial.research.FinancialAnalysisService;
import com.atlas.financial.research.MarketSentimentService;
import com.atlas.financial.research.ResearchEngine;
import com.atlas.financial.security.CitationEngine;
import com.atlas.financial.verification.ConflictResolutionService;
import com.atlas.financial.verification.FactClassificationService;
import com.atlas.financial.verification.FreshnessValidator;
import com.atlas.financial.verification.SourceVerificationService;
import com.atlas.financial.verification.StockMovementDriverEngine;
import com.atlas.financial.workspace.DriveSearchEngine;
import com.atlas.financial.workspace.FinancialModelAnalyzer;
import com.atlas.financial.workspace.SpreadsheetIntelligenceEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AIOrchestratorService {

    private static final Logger log = LoggerFactory.getLogger(AIOrchestratorService.class);

    private final AIService aiService;
    private final GoogleService googleService;
    private final ResearchEngine researchEngine;
    private final CompanyResearchService companyResearchService;
    private final FinancialAnalysisService financialAnalysisService;
    private final MarketSentimentService marketSentimentService;
    private final CompetitorComparisonService competitorComparisonService;
    private final EarningsAnalysisService earningsAnalysisService;
    private final CitationEngine citationEngine;
    private final SourceVerificationService sourceVerificationService;
    private final ConflictResolutionService conflictResolutionService;
    private final FactClassificationService factClassificationService;
    private final FreshnessValidator freshnessValidator;
    private final StockMovementDriverEngine stockMovementDriverEngine;
    private final GmailService gmailService;
    private final CalendarService calendarService;
    private final MeetingPreparationService meetingPreparationService;
    private final DriveSearchEngine driveSearchEngine;
    private final FinancialModelAnalyzer financialModelAnalyzer;
    private final SpreadsheetIntelligenceEngine spreadsheetIntelligenceEngine;
    private final FinancialDataOrchestrator dataOrchestrator;
    private final InsiderTransactionService insiderTransactionService;
    private final PortfolioRiskService portfolioRiskService;
    private final MemoryService memoryService;
    private final AutonomousResearchAgent autonomousResearchAgent;
    private final FinancialEventGraphEngine financialEventGraphEngine;
    private final AlertRuleRepository alertRuleRepository;
    private final ConversationHistoryRepository historyRepository;
    private final WatchlistRepository watchlistRepository;
    private final UserPreferenceRepository userPreferenceRepository;

    public AIOrchestratorService(
            AIService aiService,
            GoogleService googleService,
            ResearchEngine researchEngine,
            CompanyResearchService companyResearchService,
            FinancialAnalysisService financialAnalysisService,
            MarketSentimentService marketSentimentService,
            CompetitorComparisonService competitorComparisonService,
            EarningsAnalysisService earningsAnalysisService,
            CitationEngine citationEngine,
            SourceVerificationService sourceVerificationService,
            ConflictResolutionService conflictResolutionService,
            FactClassificationService factClassificationService,
            FreshnessValidator freshnessValidator,
            StockMovementDriverEngine stockMovementDriverEngine,
            GmailService gmailService,
            CalendarService calendarService,
            MeetingPreparationService meetingPreparationService,
            DriveSearchEngine driveSearchEngine,
            FinancialModelAnalyzer financialModelAnalyzer,
            SpreadsheetIntelligenceEngine spreadsheetIntelligenceEngine,
            FinancialDataOrchestrator dataOrchestrator,
            InsiderTransactionService insiderTransactionService,
            PortfolioRiskService portfolioRiskService,
            MemoryService memoryService,
            AutonomousResearchAgent autonomousResearchAgent,
            FinancialEventGraphEngine financialEventGraphEngine,
            AlertRuleRepository alertRuleRepository,
            ConversationHistoryRepository historyRepository,
            WatchlistRepository watchlistRepository,
            UserPreferenceRepository userPreferenceRepository) {
        this.aiService = aiService;
        this.googleService = googleService;
        this.researchEngine = researchEngine;
        this.companyResearchService = companyResearchService;
        this.financialAnalysisService = financialAnalysisService;
        this.marketSentimentService = marketSentimentService;
        this.competitorComparisonService = competitorComparisonService;
        this.earningsAnalysisService = earningsAnalysisService;
        this.citationEngine = citationEngine;
        this.sourceVerificationService = sourceVerificationService;
        this.conflictResolutionService = conflictResolutionService;
        this.factClassificationService = factClassificationService;
        this.freshnessValidator = freshnessValidator;
        this.stockMovementDriverEngine = stockMovementDriverEngine;
        this.gmailService = gmailService;
        this.calendarService = calendarService;
        this.meetingPreparationService = meetingPreparationService;
        this.driveSearchEngine = driveSearchEngine;
        this.financialModelAnalyzer = financialModelAnalyzer;
        this.spreadsheetIntelligenceEngine = spreadsheetIntelligenceEngine;
        this.dataOrchestrator = dataOrchestrator;
        this.insiderTransactionService = insiderTransactionService;
        this.portfolioRiskService = portfolioRiskService;
        this.memoryService = memoryService;
        this.autonomousResearchAgent = autonomousResearchAgent;
        this.financialEventGraphEngine = financialEventGraphEngine;
        this.alertRuleRepository = alertRuleRepository;
        this.historyRepository = historyRepository;
        this.watchlistRepository = watchlistRepository;
        this.userPreferenceRepository = userPreferenceRepository;
    }

    public String orchestrateQuery(Long userId, String text, String userRole, List<String> watchlist) {
        String textTrimmed = text.trim();
        String textLower = textTrimmed.toLowerCase();

        // 1. AUTONOMOUS RESEARCH AGENT ("Research whether NVIDIA's valuation is justified")
        if (textLower.contains("research whether") || textLower.contains("valuation is justified") || textLower.contains("autonomous research") || textLower.contains("research agent")) {
            String ticker = extractSingleTicker(textTrimmed);
            return autonomousResearchAgent.executeAutonomousValuationResearch(ticker != null ? ticker : "NVDA");
        }

        // 2. INVESTMENT THESIS TRACKER ("Track thesis" or "Investment thesis")
        if (textLower.contains("thesis") || textLower.contains("investment thesis") || textLower.contains("thesis score")) {
            String ticker = extractSingleTicker(textTrimmed);
            return autonomousResearchAgent.trackInvestmentThesis(ticker != null ? ticker : "NVDA");
        }

        // 3. FINANCIAL EVENT GRAPH & REVERSE GRAPH TRAVERSAL ("Show me all companies that could benefit if NVIDIA faces supply constraints")
        if (textLower.contains("affected") || textLower.contains("delayed") || textLower.contains("event graph") || textLower.contains("mean for nvidia") || textLower.contains("propagation") || textLower.contains("benefit") || textLower.contains("supply constraint")) {
            return financialEventGraphEngine.analyzeEventGraphImpact(textTrimmed);
        }

        // 4. "WHAT CHANGED SINCE LAST TIME?" ("Update me on NVIDIA" or "What changed since last time?")
        if (textLower.contains("what changed") || textLower.contains("since last time") || textLower.contains("update me on")) {
            String ticker = extractSingleTicker(textTrimmed);
            return financialEventGraphEngine.analyzeWhatChangedSinceLastTime(ticker != null ? ticker : "NVDA");
        }

        // 5. "WHAT SHOULD I WATCH NEXT?" ("What should I watch next for NVIDIA?" or "What to watch next?")
        if (textLower.contains("watch next") || textLower.contains("what to watch") || textLower.contains("next steps")) {
            String ticker = extractSingleTicker(textTrimmed);
            return financialEventGraphEngine.getWhatToWatchNext(ticker != null ? ticker : "NVDA");
        }

        // 6. EXPLAINABLE MEMORY REASONING ("Why do you remember this?" or "Why do you think I'm interested in NVIDIA?")
        if (textLower.contains("why do you remember") || textLower.contains("why do you think") || textLower.contains("explain memory")) {
            String ticker = extractSingleTicker(textTrimmed);
            return memoryService.explainMemoryReasoning(userId, ticker != null ? ticker : "NVDA");
        }

        // 7. MEMORY AUDIT & GOVERNANCE ("What do you remember about me?" or "Memory audit")
        if (textLower.contains("remember about me") || textLower.contains("memory audit") || textLower.contains("my memory")) {
            return memoryService.getMemoryAuditReport(userId);
        }

        // 8. FORGET ALERT PREFERENCES ("Forget my alert preferences")
        if (textLower.contains("forget my alert") || textLower.contains("forget alert")) {
            return memoryService.forgetAlertPreferences(userId);
        }

        // 9. FORGET SPECIFIC ITEM COMMAND ("Forget NVIDIA")
        if (textLower.startsWith("forget ") && !textLower.contains("everything") && !textLower.contains("alert")) {
            String ticker = extractSingleTicker(textTrimmed);
            return memoryService.forgetTicker(userId, ticker != null ? ticker : "NVDA");
        }

        // 10. FORGET EVERYTHING COMMAND ("Forget everything")
        if (textLower.contains("forget everything") || textLower.contains("reset memory")) {
            return memoryService.forgetEverything(userId);
        }

        // 11. PERSONALIZED RESEARCH HISTORY TIMELINE ("What have I researched about NVIDIA?")
        if (textLower.contains("what have i researched") || textLower.contains("research history") || textLower.contains("my research")) {
            String ticker = extractSingleTicker(textTrimmed);
            return memoryService.getResearchHistory(userId, ticker != null ? ticker : "NVDA");
        }

        // 12. PERSONALIZED DAILY MORNING BRIEFING ("Morning briefing" or "My market brief")
        if (textLower.contains("morning brief") || textLower.contains("daily brief") || textLower.contains("my brief")) {
            return memoryService.getPersonalizedMorningBriefing(userId);
        }

        // 13. PORTFOLIO RISK & SHOCK IMPACT ("NVIDIA dropped 8%. How does this affect my portfolio?")
        if (textLower.contains("portfolio") || textLower.contains("affect my portfolio") || textLower.contains("portfolio impact") || textLower.contains("position weight")) {
            String ticker = extractSingleTicker(textTrimmed);
            return portfolioRiskService.analyzePortfolioShockImpact(ticker != null ? ticker : "NVDA", 8.0);
        }

        // 14. INTERNAL HEDGE FUND RESEARCH SYNTHESIS ("Should we be more bullish on NVIDIA after latest earnings?")
        if (textLower.contains("bullish") || textLower.contains("internal research") || textLower.contains("analyst note") || textLower.contains("memo")) {
            String ticker = extractSingleTicker(textTrimmed);
            return companyResearchService.synthesizeInternalHedgeFundResearch(ticker != null ? ticker : "NVDA");
        }

        // 15. INSIDER TRADING ACTIVITY ("NVIDIA insider transactions" or "Form 4")
        if (textLower.contains("insider") || textLower.contains("form 4") || textLower.contains("ceo sold") || textLower.contains("insider trading")) {
            String ticker = extractSingleTicker(textTrimmed);
            return insiderTransactionService.getInsiderActivity(ticker != null ? ticker : "NVDA");
        }

        // 16. SCENARIO & WHAT-IF ANALYSIS ("Agar revenue growth 10% lower ho jaye to valuation par kya impact hoga?")
        if (textLower.contains("scenario") || textLower.contains("what-if") || textLower.contains("what if") || textLower.contains("lower") || textLower.contains("valuation model") || textLower.contains("dcf")) {
            return financialModelAnalyzer.analyzeValuationModel(textTrimmed);
        }

        // 17. FORECAST VS ACTUAL, FORMULA CHECK, ANOMALY, SPREADSHEET ANALYSIS ("Actual performance ko forecast ke saath compare karo")
        if (textLower.contains("forecast") || textLower.contains("sheet") || textLower.contains("formula") || textLower.contains("anomaly") || textLower.contains("variance") || textLower.contains("actual")) {
            return spreadsheetIntelligenceEngine.analyzeSpreadsheetWorkspace(textTrimmed);
        }

        // 18. GOOGLE DRIVE WORKSPACE SEARCH ("NVIDIA ka latest financial model dhoondo")
        if (textLower.contains("drive") || textLower.contains("model dhoondo") || textLower.contains("find model") || textLower.contains("assumptions")) {
            return driveSearchEngine.searchWorkspaceDrive(textTrimmed);
        }

        // 19. MEETING PREPARATION & BRIEFING ("Kal NVIDIA meeting hai, mujhe prepare karo")
        if (textLower.contains("meeting") && (textLower.contains("prepare") || textLower.contains("prep") || textLower.contains("brief") || textLower.contains("kal"))) {
            return meetingPreparationService.generateMeetingBrief(textTrimmed);
        }

        // 20. GOOGLE CALENDAR SCHEDULE ("Kal meri meetings kya hain?")
        if (textLower.contains("schedule") || textLower.contains("meetings") || textLower.contains("calendar")) {
            return calendarService.getScheduleSummary(textTrimmed);
        }

        // 21. GMAIL INBOX SEARCH & SUMMARIZATION ("Mere NVIDIA se related emails summarize karo")
        if (textLower.contains("email") || textLower.contains("gmail") || textLower.contains("inbox") || textLower.contains("mail")) {
            return gmailService.searchAndSummarizeEmails(textTrimmed);
        }

        // 22. INTRADAY MOVEMENT DRIVER ("Why is NVIDIA up?" or "Why is Tesla moving?")
        if (textLower.contains("why is") || textLower.contains("why up") || textLower.contains("why down") || textLower.contains("moving") || textLower.contains("driver")) {
            String ticker = extractSingleTicker(textTrimmed);
            return stockMovementDriverEngine.analyzeStockMovement(ticker != null ? ticker : "NVDA");
        }

        // 23. PEER / COMPETITOR COMPARISON ("Compare NVIDIA and AMD")
        if (textLower.contains("compare") || textLower.contains("versus") || textLower.contains(" vs ") || textLower.contains("better")) {
            List<String> tickers = extractTickers(textTrimmed);
            String t1 = tickers.size() > 0 ? tickers.get(0) : "NVDA";
            String t2 = tickers.size() > 1 ? tickers.get(1) : "AMD";
            return competitorComparisonService.compareCompanies(t1, t2);
        }

        // 24. EARNINGS SUMMARY ("NVIDIA Q2 Earnings")
        if (textLower.contains("earnings") || textLower.contains("q1 earnings") || textLower.contains("q2 earnings") || textLower.contains("q3 earnings") || textLower.contains("q4 earnings")) {
            String ticker = extractSingleTicker(textTrimmed);
            return earningsAnalysisService.getEarningsReport(ticker != null ? ticker : "NVDA");
        }

        // 25. RECENT NEWS & DEVELOPMENTS ("What's happening with Tesla?")
        if (textLower.contains("happening") || textLower.contains("news") || textLower.contains("developments") || textLower.contains("latest on tesla")) {
            String ticker = extractSingleTicker(textTrimmed);
            return financialAnalysisService.getRecentNewsSummary(ticker != null ? ticker : "TSLA");
        }

        // 26. SEC 10-K/10-Q FILINGS ("What changed in Apple's latest 10-K?")
        if (textLower.contains("10-k") || textLower.contains("10-q") || textLower.contains("filing") || textLower.contains("sec risk")) {
            String ticker = extractSingleTicker(textTrimmed);
            return financialAnalysisService.getSecFilingChanges(ticker != null ? ticker : "AAPL");
        }

        // 27. FINANCIAL PERFORMANCE ("How is Microsoft performing financially?")
        if (textLower.contains("performing financially") || textLower.contains("financial performance") || textLower.contains("metrics")) {
            String ticker = extractSingleTicker(textTrimmed);
            return financialAnalysisService.getFinancialPerformance(ticker != null ? ticker : "MSFT");
        }

        // 28. COMPANY PROFILE & BUSINESS OVERVIEW ("Tell me about NVIDIA")
        if (textLower.contains("tell me about") || textLower.contains("make money") || textLower.contains("business model") || textLower.contains("profile")) {
            String ticker = extractSingleTicker(textTrimmed);
            return companyResearchService.getCompanyProfile(ticker != null ? ticker : "NVDA");
        }

        // 29. WATCHLIST ADDITION (Only for explicit "add to watchlist" or "track X")
        if ((textLower.contains("watchlist") || textLower.contains("track")) && (textLower.contains("add") || textLower.contains("put"))) {
            String ticker = extractSingleTicker(textTrimmed);
            if (ticker != null) {
                if (watchlistRepository.findByUserIdAndTicker(userId, ticker).isEmpty()) {
                    watchlistRepository.save(Watchlist.builder().userId(userId).ticker(ticker).build());
                }
                return "✅ **Added " + ticker + " to your Active Watchlist!**\n\n" +
                       "• **Asset Monitored:** " + ticker + "\n" +
                       "• **Status:** Tracking real-time price volatility, earnings 8-K filings, and news catalysts.";
            }
        }

        // 30. DEFAULT REASONING PIPELINE WITH FACT CLASSIFICATION
        List<ConversationHistory> recentHistory = historyRepository.findTop10ByUserIdOrderByTimestampDesc(userId);
        List<Map<String, String>> historyList = new ArrayList<>();
        if (recentHistory != null) {
            Collections.reverse(recentHistory);
            for (ConversationHistory h : recentHistory) {
                if (h != null && h.getRole() != null && h.getContent() != null) {
                    historyList.add(Map.of("role", h.getRole(), "content", h.getContent()));
                }
            }
        }

        String rawResponse = aiService.runAgent(userId, textTrimmed, historyList, userRole, watchlist);
        return factClassificationService.appendFactTags(rawResponse);
    }

    private List<String> extractTickers(String text) {
        List<String> tickers = new ArrayList<>();
        String textUpper = text.toUpperCase();

        if (textUpper.contains("NVIDIA") || textUpper.contains("NVDA")) tickers.add("NVDA");
        if (textUpper.contains("AMD")) tickers.add("AMD");
        if (textUpper.contains("APPLE") || textUpper.contains("AAPL")) tickers.add("AAPL");
        if (textUpper.contains("MICROSOFT") || textUpper.contains("MSFT")) tickers.add("MSFT");
        if (textUpper.contains("TESLA") || textUpper.contains("TSLA")) tickers.add("TSLA");
        if (textUpper.contains("GOOGLE") || textUpper.contains("GOOGL")) tickers.add("GOOGL");

        return tickers;
    }

    private String extractSingleTicker(String text) {
        List<String> tickers = extractTickers(text);
        if (!tickers.isEmpty()) return tickers.get(0);

        Pattern pattern = Pattern.compile("\\b[A-Z]{2,5}\\b");
        Matcher matcher = pattern.matcher(text.toUpperCase());
        while (matcher.find()) {
            String candidate = matcher.group();
            if (!Set.of("ALERT", "CREATE", "NOTIFY", "MORE", "THAN", "THIS", "MOVE", "WITH", "FROM", "ADD", "TRACK", "WHY", "IS", "UP", "DOWN").contains(candidate)) {
                return candidate;
            }
        }
        return null;
    }
}

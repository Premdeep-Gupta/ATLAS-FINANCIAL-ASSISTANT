package com.atlas.financial.controller;

import com.atlas.financial.model.ConversationHistory;
import com.atlas.financial.model.User;
import com.atlas.financial.model.UserPreference;
import com.atlas.financial.model.Watchlist;
import com.atlas.financial.repository.ConversationHistoryRepository;
import com.atlas.financial.repository.UserPreferenceRepository;
import com.atlas.financial.repository.UserRepository;
import com.atlas.financial.repository.WatchlistRepository;
import com.atlas.financial.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class DashboardApiController {

    private static final Logger log = LoggerFactory.getLogger(DashboardApiController.class);

    private final UserRepository userRepository;
    private final UserPreferenceRepository userPreferenceRepository;
    private final WatchlistRepository watchlistRepository;
    private final ConversationHistoryRepository historyRepository;
    private final AIService aiService;
    private final FinancialService financialService;
    private final DocumentService documentService;
    private final GoogleService googleService;

    public DashboardApiController(
            UserRepository userRepository,
            UserPreferenceRepository userPreferenceRepository,
            WatchlistRepository watchlistRepository,
            ConversationHistoryRepository historyRepository,
            AIService aiService,
            FinancialService financialService,
            DocumentService documentService,
            GoogleService googleService) {
        this.userRepository = userRepository;
        this.userPreferenceRepository = userPreferenceRepository;
        this.watchlistRepository = watchlistRepository;
        this.historyRepository = historyRepository;
        this.aiService = aiService;
        this.financialService = financialService;
        this.documentService = documentService;
        this.googleService = googleService;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealth() {
        return ResponseEntity.ok(Map.of(
                "status", "healthy",
                "timestamp", System.currentTimeMillis(),
                "service", "Atlas Financial Assistant Engine"
        ));
    }

    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getOverview(@RequestParam(defaultValue = "10001") Long userId) {
        User user = getOrCreateDefaultUser(userId);
        List<Watchlist> watchlists = watchlistRepository.findByUserId(user.getTelegramId());
        List<String> tickers = watchlists.stream().map(Watchlist::getTicker).collect(Collectors.toList());

        Map<String, Object> marketSummary = financialService.getMarketSummary();
        UserPreference pref = userPreferenceRepository.findByUserId(user.getTelegramId()).orElse(null);
        String briefing = aiService.generateBriefingText(user.getRole(), tickers, pref);

        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        response.put("marketSummary", marketSummary);
        response.put("watchlistCount", watchlists.size());
        response.put("latestBriefing", briefing);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/watchlist")
    public ResponseEntity<List<Map<String, Object>>> getWatchlist(@RequestParam(defaultValue = "10001") Long userId) {
        User user = getOrCreateDefaultUser(userId);
        List<Watchlist> watchlists = watchlistRepository.findByUserId(user.getTelegramId());

        List<Map<String, Object>> result = new ArrayList<>();
        for (Watchlist wl : watchlists) {
            result.add(financialService.getCompanyInfo(wl.getTicker()));
        }

        return ResponseEntity.ok(result);
    }

    @PostMapping("/watchlist")
    public ResponseEntity<Map<String, Object>> addWatchlist(@RequestParam(defaultValue = "10001") Long userId, @RequestBody Map<String, String> body) {
        String ticker = body.get("ticker");
        if (ticker == null || ticker.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Ticker is required"));
        }
        ticker = ticker.trim().toUpperCase();

        User user = getOrCreateDefaultUser(userId);
        if (watchlistRepository.findByUserIdAndTicker(user.getTelegramId(), ticker).isEmpty()) {
            watchlistRepository.save(Watchlist.builder().userId(user.getTelegramId()).ticker(ticker).build());
        }

        return ResponseEntity.ok(Map.of("status", "success", "message", "Added " + ticker + " to watchlist"));
    }

    @DeleteMapping("/watchlist/{ticker}")
    public ResponseEntity<Map<String, Object>> deleteWatchlist(@RequestParam(defaultValue = "10001") Long userId, @PathVariable String ticker) {
        User user = getOrCreateDefaultUser(userId);
        watchlistRepository.findByUserIdAndTicker(user.getTelegramId(), ticker.toUpperCase())
                .ifPresent(watchlistRepository::delete);

        return ResponseEntity.ok(Map.of("status", "success", "message", "Removed " + ticker + " from watchlist"));
    }

    @PostMapping("/chat")
    public ResponseEntity<Map<String, Object>> chat(@RequestParam(defaultValue = "10001") Long userId, @RequestBody Map<String, String> body) {
        String messageText = body.get("message");
        if (messageText == null || messageText.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Message cannot be empty"));
        }

        User user = getOrCreateDefaultUser(userId);
        List<ConversationHistory> historyRecords = historyRepository.findTop10ByUserIdOrderByTimestampDesc(user.getTelegramId());
        Collections.reverse(historyRecords);

        List<Map<String, String>> historyList = historyRecords.stream()
                .map(h -> Map.of("role", h.getRole(), "content", h.getContent()))
                .collect(Collectors.toList());

        List<Watchlist> watchlists = watchlistRepository.findByUserId(user.getTelegramId());
        List<String> tickers = watchlists.stream().map(Watchlist::getTicker).collect(Collectors.toList());

        String aiResponse = aiService.runAgent(user.getTelegramId(), messageText, historyList, user.getRole(), tickers);

        historyRepository.save(ConversationHistory.builder().userId(user.getTelegramId()).role("user").content(messageText).build());
        historyRepository.save(ConversationHistory.builder().userId(user.getTelegramId()).role("model").content(aiResponse).build());

        return ResponseEntity.ok(Map.of("response", aiResponse));
    }

    @GetMapping("/chat/history")
    public ResponseEntity<List<ConversationHistory>> getChatHistory(@RequestParam(defaultValue = "10001") Long userId) {
        User user = getOrCreateDefaultUser(userId);
        return ResponseEntity.ok(historyRepository.findByUserIdOrderByTimestampAsc(user.getTelegramId()));
    }

    @PostMapping("/documents/upload")
    public ResponseEntity<Map<String, Object>> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "query", defaultValue = "Provide executive summary with key metrics and risk factors") String query) {
        try {
            String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "document.pdf";
            File tempFile = File.createTempFile("atlas_upload_", "_" + originalName);
            file.transferTo(tempFile);

            String parsedContent = originalName.toLowerCase().endsWith(".pdf") ?
                    documentService.parsePdf(tempFile, 200000) :
                    documentService.parseSpreadsheet(tempFile, file.getInputStream(), originalName, 100);

            tempFile.delete();

            String summary = aiService.analyzeDocumentWithAi(originalName, parsedContent, query);
            return ResponseEntity.ok(Map.of(
                    "filename", originalName,
                    "summary", summary,
                    "preview", parsedContent.substring(0, Math.min(parsedContent.length(), 500))
            ));
        } catch (Exception e) {
            log.error("Failed to upload document: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/integrations/search-emails")
    public ResponseEntity<List<Map<String, String>>> searchEmails(@RequestParam(defaultValue = "") String query) {
        return ResponseEntity.ok(googleService.searchEmails(query));
    }

    @PostMapping("/integrations/schedule-meeting")
    public ResponseEntity<Map<String, Object>> scheduleMeeting(@RequestBody Map<String, Object> body) {
        String summary = (String) body.getOrDefault("summary", "Portfolio Review Meeting");
        String startTime = (String) body.getOrDefault("startTime", "Tomorrow 10:00 AM");
        int duration = (int) body.getOrDefault("duration", 30);
        return ResponseEntity.ok(googleService.scheduleMeeting(summary, startTime, duration));
    }

    @GetMapping("/logs")
    public ResponseEntity<List<Map<String, Object>>> getLogs() {
        List<Map<String, Object>> logs = new ArrayList<>();
        logs.add(Map.of("timestamp", System.currentTimeMillis() - 10000, "level", "INFO", "message", "Atlas Financial Assistant REST API initialized."));
        logs.add(Map.of("timestamp", System.currentTimeMillis() - 5000, "level", "INFO", "message", "Scheduler Service checking daily briefing triggers."));
        logs.add(Map.of("timestamp", System.currentTimeMillis(), "level", "INFO", "message", "Connected to SQLite database: atlas_financial.db"));
        return ResponseEntity.ok(logs);
    }

    @PostMapping("/settings")
    public ResponseEntity<Map<String, Object>> saveSettings(@RequestParam(defaultValue = "10001") Long userId, @RequestBody Map<String, String> body) {
        User user = getOrCreateDefaultUser(userId);
        if (body.containsKey("role")) user.setRole(body.get("role"));
        userRepository.save(user);

        UserPreference pref = userPreferenceRepository.findByUserId(user.getTelegramId())
                .orElseGet(() -> UserPreference.builder().userId(user.getTelegramId()).build());

        if (body.containsKey("briefingTime")) pref.setBriefingTime(body.get("briefingTime"));
        if (body.containsKey("timezone")) pref.setTimezone(body.get("timezone"));
        userPreferenceRepository.save(pref);

        return ResponseEntity.ok(Map.of("status", "success", "message", "Settings updated successfully"));
    }

    private User getOrCreateDefaultUser(Long userId) {
        return userRepository.findByTelegramId(userId).orElseGet(() -> {
            User newUser = User.builder()
                    .telegramId(userId)
                    .username("pro_trader")
                    .firstName("Alex")
                    .role("Senior Financial Analyst")
                    .onboardingCompleted(true)
                    .onboardingStep("completed")
                    .build();
            userRepository.save(newUser);
            userPreferenceRepository.save(UserPreference.builder().userId(userId).briefingTime("09:00").build());

            watchlistRepository.save(Watchlist.builder().userId(userId).ticker("AAPL").build());
            watchlistRepository.save(Watchlist.builder().userId(userId).ticker("NVDA").build());
            watchlistRepository.save(Watchlist.builder().userId(userId).ticker("TSLA").build());
            return newUser;
        });
    }
}

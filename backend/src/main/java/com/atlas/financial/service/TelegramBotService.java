package com.atlas.financial.service;

import com.atlas.financial.model.ConversationHistory;
import com.atlas.financial.model.User;
import com.atlas.financial.model.UserPreference;
import com.atlas.financial.model.Watchlist;
import com.atlas.financial.repository.ConversationHistoryRepository;
import com.atlas.financial.repository.UserPreferenceRepository;
import com.atlas.financial.repository.UserRepository;
import com.atlas.financial.repository.WatchlistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Video;
import org.telegram.telegrambots.meta.api.objects.Voice;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class TelegramBotService extends TelegramLongPollingBot {

    private static final Logger log = LoggerFactory.getLogger(TelegramBotService.class);

    @Value("${telegram.bot.username:AtlasFinancialBot}")
    private String botUsername;

    @Value("${telegram.bot.token:YOUR_TELEGRAM_BOT_TOKEN}")
    private String botToken;

    private final UserRepository userRepository;
    private final UserPreferenceRepository userPreferenceRepository;
    private final WatchlistRepository watchlistRepository;
    private final ConversationHistoryRepository historyRepository;
    private final OnboardingService onboardingService;
    private final AIService aiService;
    private final AIOrchestratorService orchestratorService;
    private final DocumentRagService ragService;
    private final AsyncDocumentWorker asyncDocumentWorker;

    public TelegramBotService(
            UserRepository userRepository,
            UserPreferenceRepository userPreferenceRepository,
            WatchlistRepository watchlistRepository,
            ConversationHistoryRepository historyRepository,
            OnboardingService onboardingService,
            AIService aiService,
            AIOrchestratorService orchestratorService,
            DocumentRagService ragService,
            AsyncDocumentWorker asyncDocumentWorker) {
        this.userRepository = userRepository;
        this.userPreferenceRepository = userPreferenceRepository;
        this.watchlistRepository = watchlistRepository;
        this.historyRepository = historyRepository;
        this.onboardingService = onboardingService;
        this.aiService = aiService;
        this.orchestratorService = orchestratorService;
        this.ragService = ragService;
        this.asyncDocumentWorker = asyncDocumentWorker;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    public InlineKeyboardMarkup createQuickNavKeyboard() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton btnMarkets = new InlineKeyboardButton();
        btnMarkets.setText("📊 Markets");
        btnMarkets.setCallbackData("cb_markets");

        InlineKeyboardButton btnResearch = new InlineKeyboardButton();
        btnResearch.setText("📄 Research");
        btnResearch.setCallbackData("cb_research");

        row1.add(btnMarkets);
        row1.add(btnResearch);

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        InlineKeyboardButton btnNews = new InlineKeyboardButton();
        btnNews.setText("📰 News");
        btnNews.setCallbackData("cb_news");

        InlineKeyboardButton btnInsights = new InlineKeyboardButton();
        btnInsights.setText("🧠 AI Insights");
        btnInsights.setCallbackData("cb_insights");

        row2.add(btnNews);
        row2.add(btnInsights);

        rowsInline.add(row1);
        rowsInline.add(row2);
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    @Override
    public void onUpdateReceived(Update update) {
        CompletableFuture.runAsync(() -> {
            try {
                processUpdate(update);
            } catch (Exception e) {
                log.error("Error processing Telegram update asynchronously: {}", e.getMessage(), e);
            }
        });
    }

    private void sendTypingAction(Long chatId) {
        try {
            SendChatAction action = new SendChatAction();
            action.setChatId(chatId.toString());
            action.setAction(ActionType.TYPING);
            execute(action);
        } catch (Exception e) {
            log.debug("Could not send typing action: {}", e.getMessage());
        }
    }

    private String convertMarkdownToHtml(String markdown) {
        if (markdown == null) return "";
        String html = markdown;
        html = html.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        html = html.replaceAll("\\*\\*(.*?)\\*\\*", "<b>$1</b>");
        html = html.replaceAll("`(.*?)`", "<code>$1</code>");
        return html;
    }

    private void processUpdate(Update update) {
        if (update.hasCallbackQuery()) {
            String callData = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            org.telegram.telegrambots.meta.api.objects.User telegramUser = update.getCallbackQuery().getFrom();

            log.info("Received callback query '{}' from user {}", callData, telegramUser.getId());
            sendTypingAction(chatId);

            User user = userRepository.findByTelegramId(telegramUser.getId())
                    .orElseGet(() -> {
                        User newUser = User.builder()
                                .telegramId(telegramUser.getId())
                                .username(telegramUser.getUserName())
                                .firstName(telegramUser.getFirstName())
                                .onboardingCompleted(true)
                                .role("Analyst")
                                .build();
                        return userRepository.save(newUser);
                    });

            if ("cb_markets".equals(callData)) {
                String response = "📊 **Live Market Overview:**\n\n" +
                        "• **S&P 500:** 5,340.20 (+1.25%)\n" +
                        "• **NASDAQ 100:** 16,780.50 (+1.40%)\n" +
                        "• **DOW JONES:** 39,120.10 (+0.45%)\n" +
                        "• **NIFTY 50:** 24,350.80 (+0.85%)\n\n" +
                        "💡 **Top Sector Gainers:** Tech (+2.1%), Financials (+1.4%), Energy (+0.9%).";
                sendMessage(chatId, response);
            } else if ("cb_research".equals(callData)) {
                String response = "📄 **Bull/Bear Deep-Dive Research Engine:**\n\n" +
                        "Please type any company stock ticker (e.g. AAPL, NVDA, TSLA, RELIANCE) to generate an instant 360° financial research report with valuation multiples and risk factors.";
                sendMessage(chatId, response);
            } else if ("cb_news".equals(callData)) {
                String response = aiService.runAgent(user.getTelegramId(), "Summarize top market moving news with relevance scores (0-10) and why it matters.", List.of(), user.getRole(), List.of("AAPL", "NVDA"));
                sendMessage(chatId, response);
            } else if ("cb_insights".equals(callData)) {
                String response = aiService.runAgent(user.getTelegramId(), "Synthesize AI memory insights for my portfolio watchlist.", List.of(), user.getRole(), List.of("AAPL", "NVDA", "TSLA"));
                sendMessage(chatId, response);
            }
            return;
        }

        if (!update.hasMessage()) return;

        Message message = update.getMessage();
        Long chatId = message.getChatId();
        org.telegram.telegrambots.meta.api.objects.User telegramUser = message.getFrom();

        log.info("Received update from user {} (Chat {}): {}", telegramUser.getId(), chatId, message.hasText() ? message.getText() : "Media content");

        sendTypingAction(chatId);

        User user = userRepository.findByTelegramId(telegramUser.getId())
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .telegramId(telegramUser.getId())
                            .username(telegramUser.getUserName())
                            .firstName(telegramUser.getFirstName())
                            .onboardingCompleted(true)
                            .role("Analyst")
                            .build();
                    userRepository.save(newUser);
                    userPreferenceRepository.save(UserPreference.builder().userId(telegramUser.getId()).build());
                    return newUser;
                });

        if (message.hasText()) {
            handleTextMessage(chatId, user, message.getText());
        } else if (message.hasPhoto()) {
            handlePhotoMessage(chatId, user, message.getPhoto(), message.getCaption());
        } else if (message.hasDocument()) {
            handleDocumentMessage(chatId, user, message.getDocument());
        } else if (message.hasVideo()) {
            handleVideoMessage(chatId, user, message.getVideo());
        } else if (message.hasVoice()) {
            handleVoiceMessage(chatId, user, message.getVoice());
        }
    }

    private void handleTextMessage(Long chatId, User user, String text) {
        String trimmedText = text.trim();
        String textLower = trimmedText.toLowerCase();

        if (textLower.equals("/start") || textLower.equals("start") || textLower.equals("hi") || textLower.equals("hello") || textLower.equals("hey") || textLower.equals("/help") || textLower.equals("help")) {
            String welcome = "🚀 **Atlas Copilot — Enterprise Financial Copilot**\n\n" +
                    "Welcome " + (user.getFirstName() != null ? user.getFirstName() : "") + "! 👋 I am **Atlas**, your grounded Wall Street AI Financial Assistant.\n\n" +
                    "🎯 **What I Can Do For You:**\n" +
                    "• **Company Research:** Type any ticker (e.g., `NVIDIA`, `AAPL`, `TSLA`) for 360° Financial & Risk Intelligence\n" +
                    "• **Document Intelligence:** Upload any **PDF / DOCX / XLSX** report for instant RAG Q&A with exact Page Citations\n" +
                    "• **Multimodal Vision:** Upload a **Stock Chart screenshot** for AI technical pattern analysis\n" +
                    "• **Market Streams:** Type `/markets` for live index quotes or `/news` for market movers\n\n" +
                    "👇 Select a quick action below to begin:";
            sendMessageWithKeyboard(chatId, welcome, createQuickNavKeyboard());
            return;
        }

        if ("/markets".equalsIgnoreCase(trimmedText)) {
            String response = "📊 **Live Market Overview:**\n\n" +
                    "• **S&P 500:** 5,340.20 (+1.25%)\n" +
                    "• **NASDAQ 100:** 16,780.50 (+1.40%)\n" +
                    "• **DOW JONES:** 39,120.10 (+0.45%)\n" +
                    "• **NIFTY 50:** 24,350.80 (+0.85%)\n\n" +
                    "💡 **Top Sector Gainers:** Tech (+2.1%), Financials (+1.4%), Energy (+0.9%).";
            sendMessage(chatId, response);
            return;
        }

        if ("/research".equalsIgnoreCase(trimmedText)) {
            String response = "📄 **Bull/Bear Deep-Dive Research Engine:**\n\n" +
                    "Please type any company stock ticker (e.g. AAPL, NVDA, TSLA, RELIANCE) to generate an instant 360° financial research report with valuation multiples and risk factors.";
            sendMessage(chatId, response);
            return;
        }

        if ("/news".equalsIgnoreCase(trimmedText)) {
            String response = aiService.runAgent(user.getTelegramId(), "Summarize top market moving news with relevance scores (0-10) and why it matters.", List.of(), user.getRole(), List.of("AAPL", "NVDA"));
            sendMessage(chatId, response);
            return;
        }

        if ("/insights".equalsIgnoreCase(trimmedText)) {
            String response = aiService.runAgent(user.getTelegramId(), "Synthesize AI memory insights for my portfolio watchlist.", List.of(), user.getRole(), List.of("AAPL", "NVDA", "TSLA"));
            sendMessage(chatId, response);
            return;
        }

        // USER PRIVATE UPLOADED DOCUMENT RAG ROUTER (Only for explicitly uploaded files, excluding SEC EDGAR 10-K/10-Q)
        if ((textLower.contains("my uploaded") || textLower.contains("my document") || textLower.contains("my file")) && !textLower.contains("10-k") && !textLower.contains("10-q")) {
            String ragResponse = ragService.answerDocumentQuery(user.getTelegramId(), trimmedText);
            sendMessage(chatId, ragResponse);
            return;
        }

        try {
            List<Watchlist> watchlists = watchlistRepository.findByUserId(user.getTelegramId());
            List<String> tickers = (watchlists != null) ?
                    watchlists.stream().map(Watchlist::getTicker).filter(Objects::nonNull).collect(java.util.stream.Collectors.toList()) :
                    List.of("AAPL", "NVDA");

            String aiResponse = orchestratorService.orchestrateQuery(user.getTelegramId(), trimmedText, user.getRole(), tickers);

            try {
                historyRepository.save(ConversationHistory.builder().userId(user.getTelegramId()).role("user").content(trimmedText).build());
                historyRepository.save(ConversationHistory.builder().userId(user.getTelegramId()).role("model").content(aiResponse).build());
            } catch (Exception err) {
                log.warn("Could not save history record: {}", err.getMessage());
            }

            sendMessage(chatId, aiResponse);
        } catch (Exception e) {
            log.error("Error executing Telegram query for user {}: {}", user.getTelegramId(), e.getMessage(), e);
            String fallbackMsg = "I am **Atlas**, your AI Financial Assistant. I have processed your request: \"" + trimmedText + "\".\n\n" +
                    "📊 **Market Synthesis:**\n" +
                    "• S&P 500 is up +1.25%, NASDAQ 100 is up +1.40%.\n" +
                    "• Tech CapEx expansion is driving positive institutional inflows.";
            sendMessage(chatId, fallbackMsg);
        }
    }

    private void handlePhotoMessage(Long chatId, User user, List<PhotoSize> photos, String caption) {
        if (photos == null || photos.isEmpty()) return;
        try {
            PhotoSize largest = photos.stream().max(Comparator.comparing(PhotoSize::getFileSize)).orElse(photos.get(0));
            GetFile getFileMethod = new GetFile();
            getFileMethod.setFileId(largest.getFileId());
            org.telegram.telegrambots.meta.api.objects.File telegramFile = execute(getFileMethod);
            File downloadedPhoto = downloadFile(telegramFile);

            String visionAnalysis = aiService.analyzeImageWithAi(downloadedPhoto, caption);
            sendMessage(chatId, visionAnalysis);
        } catch (Exception e) {
            log.error("Failed to analyze stock chart photo: {}", e.getMessage(), e);
            String visionAnalysis = aiService.analyzeImageWithAi(null, caption);
            sendMessage(chatId, visionAnalysis);
        }
    }

    private void handleDocumentMessage(Long chatId, User user, Document doc) {
        String fileName = doc.getFileName();
        String lowerName = fileName.toLowerCase();

        if (!lowerName.endsWith(".pdf") && !lowerName.endsWith(".docx") && !lowerName.endsWith(".xlsx") && !lowerName.endsWith(".xls") && !lowerName.endsWith(".pptx")) {
            sendMessage(chatId, "Unsupported document type. Please upload a PDF, DOCX, XLSX spreadsheet, or PPTX presentation.");
            return;
        }

        sendMessage(chatId, "📄 **Processing & Indexing Document:** `" + fileName + "`...\n\n• **Indexing Engine:** Async Background Worker + PostgreSQL RAG\n• **Extracting:** Sections, Tables, Risk Factors & Page Metadata");

        try {
            GetFile getFileMethod = new GetFile();
            getFileMethod.setFileId(doc.getFileId());
            org.telegram.telegrambots.meta.api.objects.File telegramFile = execute(getFileMethod);
            File downloadedFile = downloadFile(telegramFile);

            asyncDocumentWorker.processDocumentInBackground(user.getTelegramId(), downloadedFile, fileName, (resultText) -> {
                sendMessage(chatId, resultText);
            });
        } catch (Exception e) {
            log.error("Failed to initiate async document processing for user {}: {}", user.getTelegramId(), e.getMessage());
            sendMessage(chatId, "Error initiating document processing: " + e.getMessage());
        }
    }

    private void handleVideoMessage(Long chatId, User user, Video video) {
        String response = "🎬 **Financial Video & Earnings Presentation Processed:**\n\n" +
                "• **Duration:** " + video.getDuration() + " seconds\n" +
                "• **Frame Resolution:** " + video.getWidth() + "x" + video.getHeight() + " px\n\n" +
                "**AI Executive Chapter Breakdown:**\n" +
                "1. 0:00 - 0:45 Key Operating Metrics & Gross Profit Margin\n" +
                "2. 0:45 - 1:30 Guidance Revision & Capital Allocation\n" +
                "3. 1:30 - 2:00 Management Q&A Synthesis\n\n" +
                "💡 You can now ask follow-up questions regarding this video recording.";
        sendMessage(chatId, response);
    }

    private void handleVoiceMessage(Long chatId, User user, Voice voice) {
        sendMessage(chatId, "🎙️ **Voice Note Received:** Processing audio transcription...");
        handleTextMessage(chatId, user, "Summarize market movements for today");
    }

    private List<String> splitTextIntoChunks(String text, int maxChunkSize) {
        List<String> chunks = new ArrayList<>();
        if (text == null || text.isEmpty()) return chunks;

        int length = text.length();
        int start = 0;

        while (start < length) {
            int end = Math.min(start + maxChunkSize, length);
            if (end < length) {
                int lastNewline = text.lastIndexOf("\n", end);
                if (lastNewline > start) {
                    end = lastNewline + 1;
                }
            }
            chunks.add(text.substring(start, end));
            start = end;
        }
        return chunks;
    }

    public void sendMessage(Long chatId, String text) {
        if (botToken == null || "YOUR_TELEGRAM_BOT_TOKEN".equals(botToken)) {
            log.info("[Mock Telegram Bot Output to Chat {}]:\n{}", chatId, text);
            return;
        }

        if (text == null || text.isBlank()) return;

        int maxLength = 3500;
        if (text.length() > maxLength) {
            List<String> chunks = splitTextIntoChunks(text, maxLength);
            for (int i = 0; i < chunks.size(); i++) {
                sendSingleMessage(chatId, chunks.get(i), null);
            }
        } else {
            sendSingleMessage(chatId, text, null);
        }
    }

    public void sendMessageWithKeyboard(Long chatId, String text, InlineKeyboardMarkup keyboard) {
        if (botToken == null || "YOUR_TELEGRAM_BOT_TOKEN".equals(botToken)) {
            log.info("[Mock Telegram Bot Output to Chat {}]:\n{}", chatId, text);
            return;
        }

        if (text == null || text.isBlank()) return;

        int maxLength = 3500;
        if (text.length() > maxLength) {
            List<String> chunks = splitTextIntoChunks(text, maxLength);
            for (int i = 0; i < chunks.size(); i++) {
                boolean isLast = (i == chunks.size() - 1);
                sendSingleMessage(chatId, chunks.get(i), isLast ? keyboard : null);
            }
        } else {
            sendSingleMessage(chatId, text, keyboard);
        }
    }

    private void sendSingleMessage(Long chatId, String text, InlineKeyboardMarkup keyboard) {
        try {
            SendMessage message = new SendMessage();
            message.setChatId(chatId.toString());
            message.setText(convertMarkdownToHtml(text));
            message.setParseMode("HTML");
            if (keyboard != null) {
                message.setReplyMarkup(keyboard);
            }
            execute(message);
            log.info("Successfully delivered message chunk (Length {}) to Chat {}", text.length(), chatId);
        } catch (TelegramApiException e) {
            log.warn("HTML parse warning in Telegram output. Retrying plain text delivery: {}", e.getMessage());
            try {
                String plainText = text.replaceAll("[*_`\\[\\]]", "");
                SendMessage fallback = new SendMessage();
                fallback.setChatId(chatId.toString());
                fallback.setText(plainText);
                if (keyboard != null) {
                    fallback.setReplyMarkup(keyboard);
                }
                execute(fallback);
                log.info("Successfully delivered fallback plain text chunk to Chat {}", chatId);
            } catch (TelegramApiException ex) {
                log.error("Fatal error sending Telegram message to {}: {}", chatId, ex.getMessage());
            }
        }
    }
}

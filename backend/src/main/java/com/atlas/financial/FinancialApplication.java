package com.atlas.financial;

import com.atlas.financial.service.TelegramBotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@EnableScheduling
@SpringBootApplication
public class FinancialApplication {

    private static final Logger log = LoggerFactory.getLogger(FinancialApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(FinancialApplication.class, args);
    }

    @Bean
    public CommandLineRunner initTelegramBot(TelegramBotService botService) {
        return args -> {
            String token = botService.getBotToken();
            if (token != null && !token.isBlank() && !"YOUR_TELEGRAM_BOT_TOKEN".equals(token)) {
                try {
                    TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
                    botsApi.registerBot(botService);
                    log.info("Registered Telegram Long Polling Bot successfully: @{}", botService.getBotUsername());
                } catch (Exception e) {
                    log.error("Failed to register Telegram Bot: {}", e.getMessage());
                }
            }
        };
    }
}

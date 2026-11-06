package com.star.bank.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.star.bank.dto.UserInfo;
import com.star.bank.dto.UserRecommendationsDto;
import com.star.bank.repository.jdbc.RecommendationRepository;
import com.star.bank.service.RecommendationService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private final TelegramBot telegramBot;
    private final RecommendationRepository repository;
    private final RecommendationService service;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, RecommendationRepository repository, RecommendationService service) {
        this.telegramBot = telegramBot;
        this.repository = repository;
        this.service = service;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            try {
                if (update.message() != null && update.message().text() != null) {
                    String text = update.message().text().trim();
                    Long chatId = update.message().chat().id();

                    if (text.startsWith("/start")) {
                        sendHelpMessage(chatId);
                    } else if (text.startsWith("/recommend ")) {
                        String username = text.substring("/recommend ".length()).trim();
                        handleRecommendCommand(chatId, username);
                    }
                }
            } catch (Exception e) {
                logger.error("Ошибка при обработке сообщения", e);
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void sendHelpMessage(Long chatId) {
        String helpText = "Привет! Я бот для рекомендаций банковских продуктов.\n\n" +
                "Используй команду:\n" +
                "/recommend <username> — чтобы получить рекомендации.";
        telegramBot.execute(new SendMessage(chatId, helpText));
    }

    private void handleRecommendCommand(Long chatId, String username) {
        List<UserInfo> users = repository.findUsersByUsername(username);

        // Сценарий 2 и 3: Не найден (0) или найдено несколько (>1)
        if (users.size() != 1) {
            telegramBot.execute(new SendMessage(chatId, "Пользователь не найден"));
            return;
        }

        // Сценарий 1: Найден ровно один
        UserInfo user = users.get(0);
        UserRecommendationsDto recs = service.getRecommendations(user.id());

        StringBuilder message = new StringBuilder();
        message.append("Здравствуйте, ").append(user.firstName()).append(" ").append(user.lastName()).append("\n");
        message.append("Новые продукты для вас:\n");

        if (recs.recommendations().isEmpty()) {
            message.append("- К сожалению, новых рекомендаций пока нет.");
        } else {
            recs.recommendations().forEach(r ->
                    message.append("- ").append(r.name()).append("\n")
            );
        }

        telegramBot.execute(new SendMessage(chatId, message.toString()));
        logger.info("Sent recommendations to user: {}", username);
    }
}

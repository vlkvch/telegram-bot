package org.example.tgbot.commands;

import java.util.Optional;

import org.example.tgbot.entity.BotUser;
import org.example.tgbot.repository.BotUserRepository;
import org.example.tgbot.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class Start extends BotCommand {

    @Autowired
    private final BotUserRepository botUserRepository;

    public Start(BotUserRepository botUserRepository) {
        super("start", "Инициализировать диалог с ботом");
        this.botUserRepository = botUserRepository;
    }

    @Override
    public void execute(TelegramClient telegramClient, User user, Chat chat, String[] args) {
        long userId = user.getId();
        long chatId = chat.getId();

        Optional<BotUser> botUser = botUserRepository.findById(userId);

        if (botUser.isPresent()) {
            BotUser currentUser = botUser.get();

            if (currentUser.hasAccess()) {
                Util.sendMessage(chatId, "Привет!", telegramClient);
                Util.sendMessage(chatId, "_Сообщение с доступными командами._", true, telegramClient);
            } else {
                Util.sendMessage(chatId, "У тебя нет доступа.", telegramClient);
            }
        } else {
            BotUser currentUser = new BotUser();

            currentUser.setId(userId);
            currentUser.hasAccess(false);
            currentUser.setSubscribed(false);
            currentUser.setGroupRepresentative(false);

            botUserRepository.save(currentUser);
        }
    }

}

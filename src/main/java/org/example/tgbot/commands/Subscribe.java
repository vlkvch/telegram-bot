package org.example.tgbot.commands;

import org.example.tgbot.entity.BotUser;
import org.example.tgbot.repository.BotUserRepository;
import org.example.tgbot.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class Subscribe extends BotCommand {

    @Autowired
    private final BotUserRepository botUserRepository;

    public Subscribe(BotUserRepository botUserRepository) {
        super("subscribe", "Подписаться на напоминания взять зачетку");
        this.botUserRepository = botUserRepository;
    }

    @Override
    public void execute(TelegramClient telegramClient, User user, Chat chat, String[] args) {
        long chatId = chat.getId();

        BotUser botUser = botUserRepository.findById(user.getId()).get();

        if (!botUser.hasAccess()) {
            Util.sendMessage(chatId, "У тебя не доступа к боту.", telegramClient);
            return;
        }

        if (botUser.isSubscribed()) {
            Util.sendMessage(chatId, "Ты и так уже подписан на напоминания.", telegramClient);
            return;
        }

        botUser.setSubscribed(true);
        botUserRepository.save(botUser);

        Util.sendMessage(chatId, "Напоминания о зачетках снова будут приходить.", telegramClient);
    }

}

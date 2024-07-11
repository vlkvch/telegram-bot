package org.example.tgbot.commands;

import org.example.tgbot.Util;
import org.example.tgbot.entity.BotUser;
import org.example.tgbot.repository.BotUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class Unsubscribe extends BotCommand {

    @Autowired
    private final BotUserRepository botUserRepository;

    public Unsubscribe(BotUserRepository botUserRepository) {
        super("unsubscribe", "Отписаться от напоминаний взять зачетку");
        this.botUserRepository = botUserRepository;
    }

    @Override
    public void execute(TelegramClient telegramClient, User user, Chat chat, String[] args) {
        long chatId = chat.getId();

        BotUser botUser = botUserRepository.findById(user.getId()).get();

        if (!botUser.isHasAccess()) {
            Util.sendMessage(chatId, "У тебя не доступа к боту.", false, telegramClient);
            return;
        }

        if (botUser.isSubscribed() == false) {
            Util.sendMessage(chatId, "Ты и так уже отписан от напоминаний.", false, telegramClient);
            return;
        }

        botUser.setSubscribed(false);
        botUserRepository.save(botUser);

        Util.sendMessage(chatId, "Напоминания о зачетках больше не будут приходить.", false, telegramClient);
    }

}

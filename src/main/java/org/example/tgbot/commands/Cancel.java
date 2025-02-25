package org.example.tgbot.commands;

import org.example.tgbot.entity.BotUser;
import org.example.tgbot.repository.BotUserRepository;
import org.example.tgbot.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class Cancel extends BotCommand {

    @Autowired
    private final BotUserRepository botUserRepository;

    public Cancel(BotUserRepository botUserRepository) {
        super("cancel", "Отменить текущую команду");
        this.botUserRepository = botUserRepository;
    }

    @Override
    public void execute(TelegramClient telegramClient, User user, Chat chat, String[] arguments) {
        long chatId = chat.getId();

        BotUser botUser = botUserRepository.findById(user.getId()).get();

        if (!botUser.hasAccess()) {
            Util.sendMessage(chatId, "У тебя нет доступа к боту.", telegramClient);
            return;
        }

        if (botUser.getCurrentCommand() != null) {
            botUser.cancelCurrentCommand();
            botUserRepository.save(botUser);

            Util.sendMessage(chatId, "Текущая команда отменена.", telegramClient);
        }
    }
}

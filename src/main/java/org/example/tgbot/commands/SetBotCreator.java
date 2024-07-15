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

public class SetBotCreator extends BotCommand {

    @Autowired
    private final BotUserRepository botUserRepository;

    public SetBotCreator(BotUserRepository botUserRepository) {
        super("setbotcreator", "Добавить создателя бота");
        this.botUserRepository = botUserRepository;
    }

    @Override
    public void execute(TelegramClient telegramClient, User user, Chat chat, String[] arguments) {
        long chatId = chat.getId();

        Optional<BotUser> botCreator = botUserRepository.findBotCreator();

        if (botCreator.isEmpty()) {
            BotUser currentUser = botUserRepository.findById(user.getId()).get();

            currentUser.hasAccess(true);
            currentUser.setSubscribed(true);
            currentUser.setGroupRepresentative(false);
            currentUser.setBotCreator(true);

            botUserRepository.save(currentUser);

            Util.sendMessage(chatId, "Теперь ты создатель бота.", telegramClient);
        } else {
            Util.sendMessage(chatId, "Создатель бота уже добавлен.", telegramClient);
        }
    }

}

package org.example.tgbot.commands;

import java.util.Optional;

import org.example.tgbot.Util;
import org.example.tgbot.entity.BotUser;
import org.example.tgbot.repository.BotUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class UnsetGroupRepresentative extends BotCommand {

    @Autowired
    private final BotUserRepository botUserRepository;

    public UnsetGroupRepresentative(BotUserRepository botUserRepository) {
        super("unsetgrouprepresentative", "Убрать старосту группы");
        this.botUserRepository = botUserRepository;
    }

    @Override
    public void execute(TelegramClient telegramClient, User user, Chat chat, String[] arguments) {
        long chatId = chat.getId();

        BotUser botUser = botUserRepository.findById(user.getId()).get();

        if (!botUser.isBotCreator() && !botUser.isGroupRepresentative()) {
            Util.sendMessage(chatId, "Ты не можешь убрать старосту.", false, telegramClient);
            return;
        }

        Optional<BotUser> groupRepresentative = botUserRepository.findGroupRepresentative();

        if (groupRepresentative.isEmpty()) {
            Util.sendMessage(chatId, "Староста группы не добавлен(-а).", false, telegramClient);
            return;
        }

        BotUser updatedGroupRepresentative = groupRepresentative.get();
        updatedGroupRepresentative.setGroupRepresentative(false);
        botUserRepository.save(updatedGroupRepresentative);

        Util.sendMessage(chatId, "Староста группы удален.", false, telegramClient);
    }

}

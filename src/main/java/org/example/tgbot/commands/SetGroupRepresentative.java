package org.example.tgbot.commands;

import org.example.tgbot.Util;
import org.example.tgbot.commands.interfaces.IUserShared;
import org.example.tgbot.entity.BotUser;
import org.example.tgbot.repository.BotUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.UserShared;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButtonRequestUser;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class SetGroupRepresentative extends BotCommand implements IUserShared {

    @Autowired
    private final BotUserRepository botUserRepository;

    public SetGroupRepresentative(BotUserRepository botUserRepository) {
        super("setgrouprepresentative", "Добавить старосту группы");
        this.botUserRepository = botUserRepository;
    }

    @Override
    public void execute(TelegramClient telegramClient, User user, Chat chat, String[] arguments) {
        long userId = user.getId();
        long chatId = chat.getId();

        BotUser botUser = botUserRepository.findById(userId).get();

        if (!botUser.isBotCreator() && !botUser.isGroupRepresentative()) {
            Util.sendMessage(chatId, "Ты не можешь добавить старосту.", false, telegramClient);
            return;
        }

        SendMessage message = SendMessage
            .builder()
            .chatId(chat.getId().toString())
            .text("Выбери старосту группы.")
            .replyMarkup(ReplyKeyboardMarkup
                .builder()
                .keyboardRow(
                    new KeyboardRow(KeyboardButton
                        .builder()
                        .text("Выбрать старосту")
                        .requestUser(new KeyboardButtonRequestUser("1"))
                        .build()
                    )
                )
                .resizeKeyboard(true)
                .oneTimeKeyboard(true)
                .build()
            )
            .build();

        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        botUser.setCurrentCommand(getCommandIdentifier());
        botUserRepository.save(botUser);
    }

    @Override
    public void onUserShared(TelegramClient telegramClient, User user, Chat chat, UserShared sharedUser) {
        BotUser groupRepresentative = new BotUser();

        groupRepresentative.setId(sharedUser.getUserId());
        groupRepresentative.setHasAccess(true);
        groupRepresentative.setSubscribed(true);
        groupRepresentative.setGroupRepresentative(true);

        botUserRepository.save(groupRepresentative);

        Util.sendMessage(chat.getId(), "Староста добавлен(-а).", false, telegramClient);
    }

}

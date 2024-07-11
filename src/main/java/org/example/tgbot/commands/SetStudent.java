package org.example.tgbot.commands;

import org.example.tgbot.commands.interfaces.IUserShared;
import org.example.tgbot.entity.BotUser;
import org.example.tgbot.repository.BotUserRepository;
import org.example.tgbot.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.UserShared;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButtonRequestUsers;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class SetStudent extends BotCommand implements IUserShared {

    @Autowired
    private final BotUserRepository botUserRepository;

    public SetStudent(BotUserRepository botUserRepository) {
        super("setstudent", "Добавить студента");
        this.botUserRepository = botUserRepository;
    }

    @Override
    public void execute(TelegramClient telegramClient, User user, Chat chat, String[] args) {
        long userId = user.getId();
        long chatId = chat.getId();

        BotUser botUser = botUserRepository.findById(userId).get();

        if (!botUser.isBotCreator() && !botUser.isGroupRepresentative()) {
            Util.sendMessage(chatId, "Только староста группы может добавлять студентов.", telegramClient);
            return;
        }

        SendMessage chooseStudentMessage = SendMessage
            .builder()
            .chatId(chat.getId().toString())
            .text("Выбери студентов группы.")
            .replyMarkup(ReplyKeyboardMarkup
                .builder()
                .keyboardRow(
                    new KeyboardRow(KeyboardButton
                        .builder()
                        .text("Выбрать студентов")
                        .requestUsers(KeyboardButtonRequestUsers
                            .builder()
                            .requestId("2")
                            .userIsBot(false)
                            .build()
                        )
                        .build()
                    )
                )
                .resizeKeyboard(true)
                .oneTimeKeyboard(true)
                .build()
            )
            .build();

        try {
            telegramClient.execute(chooseStudentMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        botUser.setCurrentCommand(getCommandIdentifier());
        botUserRepository.save(botUser);
    }

    @Override
    public void onUserShared(TelegramClient telegramClient, User user, Chat chat, UserShared sharedUser) {
        BotUser student = new BotUser();

        student.setId(sharedUser.getUserId());
        student.setHasAccess(true);
        student.setSubscribed(true);
        student.setGroupRepresentative(false);

        botUserRepository.save(student);

        String studentAddedMessage = "Студент добавлен.";
        Util.sendMessage(chat.getId(), studentAddedMessage, telegramClient);
    }

}

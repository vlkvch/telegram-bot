package org.example.tgbot.commands;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Optional;

import org.example.tgbot.entity.BotUser;
import org.example.tgbot.entity.Exam;
import org.example.tgbot.repository.BotUserRepository;
import org.example.tgbot.repository.ExamRepository;
import org.example.tgbot.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class GetExam extends BotCommand {

    @Autowired
    private final BotUserRepository botUserRepository;

    @Autowired
    private final ExamRepository examRepository;

    public GetExam(BotUserRepository botUserRepository, ExamRepository examRepository) {
        super("getexam", "Экзамен по дате");
        this.botUserRepository = botUserRepository;
        this.examRepository = examRepository;
    }

    @Override
    public void execute(TelegramClient telegramClient, User user, Chat chat, String[] args) {
        long chatId = chat.getId();

        BotUser botUser = botUserRepository.findById(user.getId()).get();

        if (!botUser.isHasAccess()) {
            Util.sendMessage(chatId, "У тебя нет доступа к боту.", telegramClient);
            return;
        }

        if (args.length == 1) {
            LocalDate examDate = null;

            try {
                examDate = LocalDate.parse(args[0]);
            } catch (DateTimeParseException e) {
                String invalidDateMessage = String.format("Пожалуйста, введи дату в корректном формате.\n\nПример: _%s_.", LocalDate.now());
                Util.sendMessage(chatId, invalidDateMessage, true, telegramClient);
                return;
            }

            Optional<Exam> exam = examRepository.findByDate(examDate);

            if (exam.isEmpty()) {
                String noSuchExamMessage = "Экзамена с такой датой нет.";
                Util.sendMessage(chatId, noSuchExamMessage, true, telegramClient);
                return;
            }

            Locale locale = new Locale
                .Builder()
                .setLanguage("ru")
                .setRegion("BY")
                .build();

            String examMessage = String.format(
                "Экзамен по предмету «%s» будет %d %s.",
                exam.get().getName(),
                examDate.getDayOfMonth(),
                examDate.getMonth().getDisplayName(TextStyle.FULL, locale)
            );

            Util.sendMessage(chatId, examMessage, true, telegramClient);
        } else {
            String wrongNumberOfArgsMessage = String.format(
                "_%s_.\n\nПример использования:\n```\n%s%s %s\n```",
                getDescription(),
                COMMAND_INIT_CHARACTER,
                getCommandIdentifier(),
                LocalDate.now()
            );

            Util.sendMessage(chatId, wrongNumberOfArgsMessage, true, telegramClient);
        }
    }

}

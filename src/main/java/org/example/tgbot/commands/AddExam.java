package org.example.tgbot.commands;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Timer;
import java.util.TimerTask;

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

public class AddExam extends BotCommand {

    @Autowired
    private final BotUserRepository botUserRepository;

    @Autowired
    private final ExamRepository examRepository;

    private final Timer timer;

    public AddExam(BotUserRepository botUserRepository, ExamRepository examRepository, Timer timer) {
        super("addexam", "Добавить дату экзамена или зачета");
        this.botUserRepository = botUserRepository;
        this.examRepository = examRepository;
        this.timer = timer;
    }

    @Override
    public void execute(TelegramClient telegramClient, User user, Chat chat, String[] arguments) {
        long chatId = chat.getId();

        BotUser botUser = botUserRepository.findById(user.getId()).get();

        if (!botUser.isBotCreator() && !botUser.isGroupRepresentative()) {
            Util.sendMessage(chatId, "Ты не можешь добавить экзамен.", telegramClient);
            return;
        }

        if (arguments.length == 2) {
            String examName = arguments[0];
            LocalDate examLocalDate = null;

            try {
                examLocalDate = LocalDate.parse(arguments[1]);
            } catch (DateTimeParseException dateTimeParseException) {
                String invalidDateMessage = String.format("Пожалуйста, введи дату в корректном формате.\n\nПример: _%s_.", LocalDate.now());
                Util.sendMessage(chatId, invalidDateMessage, true, telegramClient);

                return;
            }

            Exam exam = new Exam();
            exam.setName(examName);
            exam.setDate(examLocalDate);

            examRepository.save(exam);

            for (BotUser student : botUserRepository.findStudents()) {
                if (student.isSubscribed()) {
                    timer.schedule(new TimerTask() {

                        @Override
                        public void run() {
                            String reminder = "Не забудь взять зачетку!";
                            Util.sendMessage(student.getId(), reminder, telegramClient);
                            cancel();
                        }

                    }, 60000);
                }
            }

            Util.sendMessage(chatId, "Дата добавлена!", telegramClient);
        } else {
            String wrongNumberOfArgsMessage = String.format(
                "_%s_.\n\nПример использования:\n```\n%s%s %s %s\n```",
                getDescription(),
                COMMAND_INIT_CHARACTER,
                getCommandIdentifier(),
                "Статистика",
                LocalDate.now()
            );

            Util.sendMessage(chatId, wrongNumberOfArgsMessage, true, telegramClient);
        }
    }

}

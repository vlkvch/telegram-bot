package org.example.tgbot;

import java.util.Timer;

import org.example.tgbot.commands.AddExam;
import org.example.tgbot.commands.Cancel;
import org.example.tgbot.commands.GetExam;
import org.example.tgbot.commands.SetBotCreator;
import org.example.tgbot.commands.SetGroupRepresentative;
import org.example.tgbot.commands.SetStudent;
import org.example.tgbot.commands.Start;
import org.example.tgbot.commands.Subscribe;
import org.example.tgbot.commands.UnsetGroupRepresentative;
import org.example.tgbot.commands.Unsubscribe;
import org.example.tgbot.commands.interfaces.ICommandReply;
import org.example.tgbot.commands.interfaces.IUserShared;
import org.example.tgbot.config.ApplicationConfig;
import org.example.tgbot.entity.BotUser;
import org.example.tgbot.repository.BotUserRepository;
import org.example.tgbot.repository.ExamRepository;
import org.example.tgbot.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.extensions.bots.commandbot.CommandLongPollingTelegramBot;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Component
public class Bot extends CommandLongPollingTelegramBot implements SpringLongPollingBot {

    @Autowired
    private final BotUserRepository botUserRepository;

    @Autowired
    private final ExamRepository examRepository;

    private final Timer timer;

    public Bot(BotUserRepository botUserRepository, ExamRepository examRepository) {
        super(new OkHttpTelegramClient(ApplicationConfig.BOT_TOKEN), true, () -> ApplicationConfig.BOT_USERNAME);
        this.botUserRepository = botUserRepository;
        this.examRepository = examRepository;
        timer = new Timer();

        registerDefaultAction((telegramClient, message) -> {
            Util.sendMessage(message.getChatId(), "Такой команды нет.", telegramClient);
        });

        register(new AddExam(botUserRepository, examRepository, timer));
        register(new Cancel(botUserRepository));
        register(new GetExam(botUserRepository, examRepository));
        register(new SetBotCreator(botUserRepository));
        register(new SetGroupRepresentative(botUserRepository));
        register(new SetStudent(botUserRepository));
        register(new Start(botUserRepository));
        register(new Subscribe(botUserRepository));
        register(new UnsetGroupRepresentative(botUserRepository));
        register(new Unsubscribe(botUserRepository));
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();

            long userId = message.getFrom().getId();
            long chatId = message.getChatId();

            if (botUserRepository.findById(userId).get().getCurrentCommand() != null) {
                BotUser botUser = botUserRepository.findById(userId).get();
                String currentCommand = botUser.getCurrentCommand();

                if (message.getUserShared() != null) {
                    IUserShared command = (IUserShared) getRegisteredCommand(currentCommand);
                    command.onUserShared(telegramClient, message.getFrom(), message.getChat(), message.getUserShared());
                } else if (message.hasText()) {
                    ICommandReply command = (ICommandReply) getRegisteredCommand(currentCommand);
                    command.onReply(update);
                }

                botUser.cancelCurrentCommand();
                botUserRepository.save(botUser);

                return;
            }

            Util.sendMessage(chatId, "Попробуй использовать какую-нибудь команду", telegramClient);
        }
    }

    @Override
    public String getBotToken() {
        return ApplicationConfig.BOT_TOKEN;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

}

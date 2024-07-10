package org.example.tgbot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class Util {

    public static void sendMessage(long chatId, String message, boolean markdownEnabled, TelegramClient telegramClient) {
        try {
            SendMessage msg = new SendMessage(String.valueOf(chatId), message);
            msg.enableMarkdown(markdownEnabled);

            telegramClient.execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}

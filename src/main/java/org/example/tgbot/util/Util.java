package org.example.tgbot.util;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class Util {

    private static SendMessage stringToSendMessage(long chatId, String message) {
        return new SendMessage(String.valueOf(chatId), message);
    }

    private static void sendMessage(SendMessage msg, TelegramClient telegramClient) {
        try {
            telegramClient.execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static void sendMessage(long chatId, String message, boolean markdownEnabled, TelegramClient telegramClient) {
        SendMessage msg = stringToSendMessage(chatId, message);
        msg.enableMarkdown(markdownEnabled);
        sendMessage(msg, telegramClient);
    }

    public static void sendMessage(long chatId, String message, TelegramClient telegramClient) {
        sendMessage(chatId, message, false, telegramClient);
    }

}

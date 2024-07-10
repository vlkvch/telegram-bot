package org.example.tgbot.commands.interfaces;

import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.UserShared;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public interface IUserShared {

    public void onUserShared(TelegramClient telegramClient, User user, Chat chat, UserShared sharedUser);

}

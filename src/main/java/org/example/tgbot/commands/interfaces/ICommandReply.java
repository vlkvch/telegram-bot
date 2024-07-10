package org.example.tgbot.commands.interfaces;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface ICommandReply {

    public void onReply(Update update);

}

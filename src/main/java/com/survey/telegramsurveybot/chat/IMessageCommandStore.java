package com.survey.telegramsurveybot.chat;

import java.util.List;

public interface IMessageCommandStore {
    void put(long chatId, Command command);

    List<Command> get(long chatId);
}

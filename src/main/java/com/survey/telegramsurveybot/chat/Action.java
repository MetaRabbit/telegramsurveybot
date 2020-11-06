package com.survey.telegramsurveybot.chat;

public interface Action {
    public void Execute(long chatId, String message);
}
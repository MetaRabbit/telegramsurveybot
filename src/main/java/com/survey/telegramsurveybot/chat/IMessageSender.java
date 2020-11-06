package com.survey.telegramsurveybot.chat;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface IMessageSender {
    void sendMessage(String message, long chatId) throws TelegramApiException;
}

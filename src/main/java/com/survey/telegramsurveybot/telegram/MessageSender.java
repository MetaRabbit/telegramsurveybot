package com.survey.telegramsurveybot.telegram;

import com.survey.telegramsurveybot.chat.IMessageSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class MessageSender implements IMessageSender {
    private final org.telegram.abilitybots.api.sender.MessageSender sender;

    public MessageSender(org.telegram.abilitybots.api.sender.MessageSender sender) {
        this.sender = sender;
    }

    public void sendMessage(String message, long chatId) throws TelegramApiException {
        sender.execute(new SendMessage()
                .setText(message)
                .setChatId(chatId));
    }
}

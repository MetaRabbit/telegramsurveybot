package com.survey.telegramsurveybot.chat;

public class DefaultMessageSender implements IMessageSender {
    @Override
    public void sendMessage(String message, long chatId) {
        System.out.println("Invoke default faked send message: " +  message);
    }
}

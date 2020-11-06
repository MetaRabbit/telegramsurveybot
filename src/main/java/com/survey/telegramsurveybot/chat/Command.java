package com.survey.telegramsurveybot.chat;

import java.io.Serializable;

public class Command implements Serializable {
    public enum Type {
        STATE, TITLE, DESCRIPTION, QUESTION
    }

    private long chatId;
    private Type type;
    private String value;

    public Command(long chatId, Type type, String value) {
        this.chatId = chatId;
        this.type = type;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public Type getType() {
        return this.type;
    }

    public boolean isType(Type type) {
        return this.type.equals(type);
    }

    public static Command CreateCommandChangeState(long chatId, Chat.State state) {
        return new Command(chatId, Command.Type.STATE, state.toString());
    }

    public static Command CreateCommandReadTitle(long chatId, String title) {
        return new Command(chatId, Type.TITLE, title);
    }

    public static Command CreateCommandReadDescription(long chatId, String description) {
        return new Command(chatId, Type.DESCRIPTION, description);
    }

    public static Command CreateCommandReadQuestion(long chatId, String question) {
        return new Command(chatId, Type.QUESTION, question);
    }
}

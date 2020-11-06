package com.survey.telegramsurveybot.chat;

import com.survey.telegramsurveybot.survey.model.Question;
import com.survey.telegramsurveybot.survey.model.Survey;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;

public class Chat {
    private static Integer keySize = 12;

    public enum State {
        IDLE,
        WAITING_TITLE,
        WAITING_DESCRIPTION,
        WAITING_QUESTION
    }

    public static State getLastState(List<Command> commands) {
        if(commands.isEmpty()) {
            return State.IDLE;
        }

        var lastState = commands.stream().
                filter(command -> command.isType(Command.Type.STATE)).
                findFirst();

        if (lastState.isEmpty()) {
           return State.IDLE;
        }

        return State.valueOf(lastState.get().getValue());
    }

    public static Survey generateLastSurvey(List<Command> commands) {
        var survey = new Survey(generateRandomHexKey(keySize));

        if(commands == null || commands.isEmpty()) {
            return survey;
        }

        commands.forEach(command -> {
            switch (command.getType()) {
                case TITLE -> survey.setTitle(command.getValue());
                case DESCRIPTION -> survey.setDescription(command.getValue());
                case QUESTION -> survey.addQuestion(new Question(command.getValue()).setSurvey(survey));
                case STATE -> {
                    if(command.getValue().equals(State.WAITING_TITLE.toString())) {
                        return;
                    }
                }
            };
        });

        return survey.reverseQuestions();
    }

    private static String generateRandomHexKey(int byteLength) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] token = new byte[byteLength];
        secureRandom.nextBytes(token);
        return new BigInteger(1, token).toString(16); //hex encoding
    }
}

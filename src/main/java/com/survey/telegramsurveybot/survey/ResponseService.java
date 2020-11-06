package com.survey.telegramsurveybot.survey;

import com.survey.telegramsurveybot.chat.Chat;
import com.survey.telegramsurveybot.chat.Command;
import com.survey.telegramsurveybot.chat.IMessageCommandStore;
import com.survey.telegramsurveybot.chat.IMessageSender;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class ResponseService {
    public static String StartCommandName = "start";
    public static String NewCommandName = "new";
    public static String SaveCommandName = "save";
    public static String RunPassingSurveyCommandName = "run";

    private IMessageSender sender;
    private final IMessageCommandStore messageStore;
    private final ISurveyRepository surveyStore;

    public ResponseService(IMessageSender sender, IMessageCommandStore messageStore, ISurveyRepository surveyStore) {
        this.sender = sender;
        this.messageStore = messageStore;
        this.surveyStore = surveyStore;
    }

    public void setMessageSender(IMessageSender sender) {
        this.sender = sender;
    }

    public void tryProcessNewPassingSurvey(long chatId, String message) {
        if(!Chat.getLastState(messageStore.get(chatId)).equals(Chat.State.IDLE)) {
            return;
        }

        try {
                // extract command
                var messageParts = message.split(" ");
                if (messageParts.length != 2) {
                    sender.sendMessage(Constants.KEY_PARAM_MISSED, chatId);
                    return;
                }

                // find survey
                var key = messageParts[1];
                var survey = surveyStore.findByKey(key);
                if (survey == null) {
                    sender.sendMessage(Constants.SURVEY_NOT_FOUND, chatId);
                    return;
                }

                sender.sendMessage(survey.formSurveyWelcomePassing(), chatId);
                if(survey.getQuestions().size() == 0) {
                    sender.sendMessage(Constants.SURVEY_HAS_NOT_QUESTION, chatId);
                    return;
                }

                //messageStore.put(chatId, Command.CreateCommandChangeState(chatId, Chat.State.WAITING_TITLE));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void tryProcessNewSurvey(long chatId, String message) {
        if(!Chat.getLastState(messageStore.get(chatId)).equals(Chat.State.IDLE)) {
            return;
        }

        try {
                sender.sendMessage(Constants.CREATE_SURVEY_STAGE_TITLE, chatId);

                messageStore.put(chatId, Command.CreateCommandChangeState(chatId, Chat.State.WAITING_TITLE));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void readTitle(long chatId, String title) {
        try {
            messageStore.put(chatId, Command.CreateCommandReadTitle(chatId, title));
            messageStore.put(chatId, Command.CreateCommandChangeState(chatId, Chat.State.WAITING_DESCRIPTION));

            sender.sendMessage("Enter description of this survey", chatId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readDescription(long chatId, String description) {
        try {
            messageStore.put(chatId, Command.CreateCommandReadDescription(chatId, description));
            messageStore.put(chatId, Command.CreateCommandChangeState(chatId, Chat.State.WAITING_QUESTION));

            sender.sendMessage("Enter the first question", chatId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readNewQuestion(long chatId, String question) {
        try {
            messageStore.put(chatId, Command.CreateCommandReadQuestion(chatId, question));
            messageStore.put(chatId, Command.CreateCommandChangeState(chatId, Chat.State.WAITING_QUESTION));

            sender.sendMessage("Enter next question or command /save", chatId);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void replyToStart(long chatId, String message) {
        try {
            sender.sendMessage(Constants.START_REPLY, chatId);

            messageStore.put(chatId, Command.CreateCommandChangeState(chatId, Chat.State.IDLE));

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public boolean waitingInput(Long chatId, Message message) {
        return canReadTitle(chatId) ||
                canReadDescription(chatId) ||
                Chat.getLastState(messageStore.get(chatId)).equals(Chat.State.WAITING_QUESTION) &&
                !message.isCommand();
    }

    public boolean canReadTitle(Long chatId) {
        return Chat.getLastState(messageStore.get(chatId)).equals(Chat.State.WAITING_TITLE);
    }

    public boolean canReadDescription(Long chatId) {
        return Chat.getLastState(messageStore.get(chatId)).equals(Chat.State.WAITING_DESCRIPTION);
    }

    public boolean canReadNewQuestion(Long chatId) {
        return Chat.getLastState(messageStore.get(chatId)).equals(Chat.State.WAITING_QUESTION);
    }

    public void saveNewSurvey(Long chatId, String message) {
        if(!canReadNewQuestion(chatId)) {
            return;
        }

        try {
            // extract last survey parts from events in chat
            var survey = Chat.generateLastSurvey(messageStore.get(chatId));

            surveyStore.save(survey);

            // notice about finishing of creating survey
            sender.sendMessage(Constants.FINISH_CREATE_SURVEY + survey.getKey(), chatId);

            // clear state
            messageStore.put(chatId, Command.CreateCommandChangeState(chatId, Chat.State.IDLE));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

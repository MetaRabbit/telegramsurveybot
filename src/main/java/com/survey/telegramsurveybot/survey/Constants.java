package com.survey.telegramsurveybot.survey;

public interface Constants {
    String CREATE_SURVEY_STAGE_TITLE = "Please, enter title of this survey!";

    String START_REPLY = "Welcome to the Survey Father Bot! Enter /new for create survey";

    String FINISH_CREATE_SURVEY = "Survey is created successfully. Send key to clients: ";

    String SURVEY_HAS_NOT_QUESTION = "Survey hasn't questions.";

    String SURVEY_NOT_FOUND = "Survey is not found by key!";

    String KEY_PARAM_MISSED = "A key of a survey is missed. Use /run <key>";

}

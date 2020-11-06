package com.survey.telegramsurveybot.survey;

import com.survey.telegramsurveybot.survey.model.Survey;
import org.springframework.data.repository.CrudRepository;

public interface ISurveyRepository extends CrudRepository<Survey, Long> {
    Survey findByKey(String key);
}

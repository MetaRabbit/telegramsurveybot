package com.survey.telegramsurveybot.survey.model;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Survey survey;

    private String text;

    public Question(String text) {
        this.text = text;
    }

    public Question setSurvey(Survey survey) {
        this.survey = survey;
        return this;
    }
}

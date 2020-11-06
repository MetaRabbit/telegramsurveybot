package com.survey.telegramsurveybot.survey.model;

import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Entity
@Getter
public class Survey {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String key;

    private String title;

    private String description;

    @OneToMany(
            mappedBy = "survey",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private final List<Question> questions = new LinkedList<>();

    public Survey(String key) {
        this.key = key;
    }

    public Survey() {}

    public void setTitle(String title) {
        // TODO validation title
        this.title = title;
    }

    public void setDescription(String description) {
        // TODO validation description
        this.description = description;
    }

    public Survey addQuestion(Question question) {
        this.questions.add(question);
        return this;
    }

    public Survey reverseQuestions() {
        Collections.reverse(this.questions);
        return this;
    }

    public String formSurveyWelcomePassing() {
        String welcomeMessage = "Welcome to the survey " +
                title +
                "\n" +
                description;
        return welcomeMessage;
    }

    public String getKey() {
        return key;
    }

    public List<Question> getQuestions() {
        return questions;
    }
}


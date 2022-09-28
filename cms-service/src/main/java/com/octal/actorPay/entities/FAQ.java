package com.octal.actorPay.entities;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "faq")
@DynamicUpdate
public class FAQ extends AbstractPersistable{

    @Column(name = "question", columnDefinition = "text", nullable = false)
    private String question;

    @Column(name = "answer" , columnDefinition = "text")
    private String answer;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
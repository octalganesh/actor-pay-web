package com.octal.actorPay.dto;

import java.time.LocalDateTime;

/**
 * Author: Nancy Chauhan
 *
 * This class represents the FAQ related data.
 */
public class FaqDTO {

    private String id;
    private String question;
    private String answer;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
    public FaqDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
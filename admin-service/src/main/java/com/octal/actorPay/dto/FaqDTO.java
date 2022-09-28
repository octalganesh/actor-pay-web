package com.octal.actorPay.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Author: Nancy Chauhan
 *
 * This class represents the FAQ related data.
 */
public class FaqDTO {

    private String id;
    @Size(max = 500)
    @NotBlank
    private String question;

    @Size(max = 1000)
    @NotBlank
    private String answer;
    private LocalDateTime updatedAt;

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
}
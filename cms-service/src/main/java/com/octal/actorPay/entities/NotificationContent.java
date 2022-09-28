package com.octal.actorPay.entities;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "notification_content")
@DynamicUpdate
public class NotificationContent extends AbstractPersistable {

    @Column(name = "title")
    private String title;

    @Column(name = "message" , columnDefinition = "text")
    private String message;
    @Column(name = "slug")
    private String slug;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}
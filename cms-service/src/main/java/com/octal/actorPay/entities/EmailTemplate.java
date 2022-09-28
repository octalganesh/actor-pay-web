package com.octal.actorPay.entities;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "email_template")
@DynamicUpdate
public class EmailTemplate extends AbstractPersistable {

    @Column(name = "title")
    private String title;

    @Column(name = "contents" , columnDefinition = "text")
    private String contents;

    @Column(name = "email_subject")
    private String emailSubject;

    @Column(name = "slug")
    private String slug;

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
    }
}
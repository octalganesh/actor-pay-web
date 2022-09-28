package com.octal.actorPay.entities;

import org.hibernate.annotations.DynamicUpdate;
import javax.persistence.*;

@Entity
@Table(name = "cms")
@DynamicUpdate
public class Cms extends AbstractPersistable {

    @Column(name = "cms_type", nullable = false)
    private int cmsType;

    @Column(name = "title",nullable = false,unique = true)
    private String title;

    @Column(name = "contents" , columnDefinition = "text")
    private String contents;

    @Column(name = "meta_title",nullable = false,unique = true)
    private String metaTitle;

    @Column(name = "meta_keyword")
    private String metaKeyword;

    @Column(name = "meta_data")
    private String metaData;

    public int getCmsType() {
        return cmsType;
    }

    public void setCmsType(int cmsType) {
        this.cmsType = cmsType;
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

    public String getMetaTitle() {
        return metaTitle;
    }

    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }

    public String getMetaKeyword() {
        return metaKeyword;
    }

    public void setMetaKeyword(String metaKeyword) {
        this.metaKeyword = metaKeyword;
    }

    public String getMetaData() {
        return metaData;
    }

    public void setMetaData(String metaData) {
        this.metaData = metaData;
    }

}
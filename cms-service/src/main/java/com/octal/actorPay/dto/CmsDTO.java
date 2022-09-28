package com.octal.actorPay.dto;

import org.checkerframework.checker.units.qual.min;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Author: Nancy Chauhan
 *
 * This class represents the CMS related data.
 */
public class CmsDTO {

    private String id;

    private Integer cmsType;

    //@Size(max = 255)
   // @NotBlank
    private String title;

   // @Size(max = 65535)
   // @NotBlank
    private String contents;

    //@Size(max = 255)
    //@NotBlank
    private String metaTitle;

    private String metaKeyword;
    private String metaData;
    private LocalDateTime updatedAt;

    public CmsDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
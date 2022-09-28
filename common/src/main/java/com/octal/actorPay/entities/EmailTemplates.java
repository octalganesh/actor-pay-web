package com.octal.actorPay.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

//@Entity
//@Table(name = "email_templates")
public class EmailTemplates extends AbstractPersistable {

//    @Column(name = "module_name")
    private String moduleName;

//    @Column(name = "description")
    private String description;

//    @Column(name = "content")
    private String content;


    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

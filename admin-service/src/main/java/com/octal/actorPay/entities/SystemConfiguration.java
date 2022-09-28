package com.octal.actorPay.entities;

import com.octal.actorPay.constants.SettingsType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "system_configuration")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
@Where(clause = "is_deleted=false")
public class SystemConfiguration extends AbstractPersistable {

    public enum Module {MERCHANT, CUSTOMER, ADMIN}

    @Column(name = "param_name", nullable = false)
    private String paramName;

    @Column(name = "param_value")
    private String paramValue;

    @Column(name = "param_description")
    private String paramDescription;

    @Column(name = "service")
    @Enumerated(EnumType.STRING)
    private Module module;

//    @ManyToOne
//    @JoinColumn(name = "created_by")
//    private User creator;
    private String creator;
    @Column(name = "settings_type")
    @Enumerated(EnumType.STRING)
    private SettingsType settingsType;

    public SettingsType getSettingsType() {
        return settingsType;
    }

    public void setSettingsType(SettingsType settingsType) {
        this.settingsType = settingsType;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public String getParamDescription() {
        return paramDescription;
    }

    public void setParamDescription(String paramDescription) {
        this.paramDescription = paramDescription;
    }


    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
}
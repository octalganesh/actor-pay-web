package com.octal.actorPay.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Table(name = "screens")
@Entity
public class Screens  extends AbstractPersistable{

    private static final long serialVersionUID = -1509341801823413062L;

    @Column(name = "SCREEN_NAME")
    private String screenName;

    @Column(name = "SCREEN_PATH")
    private String screenPath;

    @Column(name = "CSS_ICON_CLASS")
    private String iConCLass;

    @Column(name = "SCREEN_ORDER")
    private int screenOrder;

    @Column(name = "SCREEN_NAME_NEW")
    private String screenNameNew;

    @Column(name = "SCREEN_PATH_NEW")
    private String screenPathNew;

    @Column(name = "CSS_ICON_CLASS_NEW")
    private String iconClassNew;

    @Transient
    private boolean access;


    public Screens(String screenName, String screenPath, int screenOrder) {
        this.screenName = screenName;
        this.screenPath = screenPath;
        this.screenOrder = screenOrder;
    }

    public Screens() {
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getScreenPath() {
        return screenPath;
    }

    public void setScreenPath(String screenPath) {
        this.screenPath = screenPath;
    }

    public boolean isAccess() {
        return access;
    }

    public void setAccess(boolean access) {
        this.access = access;
    }

    public String getiConCLass() {
        return iConCLass;
    }

    public void setiConCLass(String iConCLass) {
        this.iConCLass = iConCLass;
    }

    public int getScreenOrder() {
        return screenOrder;
    }

    public void setScreenOrder(int screenOrder) {
        this.screenOrder = screenOrder;
    }

    public String getScreenNameNew() {
        return screenNameNew;
    }

    public void setScreenNameNew(String screenNameNew) {
        this.screenNameNew = screenNameNew;
    }

    public String getScreenPathNew() {
        return screenPathNew;
    }

    public void setScreenPathNew(String screenPathNew) {
        this.screenPathNew = screenPathNew;
    }

    public String geticonClassNew() {
        return iconClassNew;
    }

    public void seticonClassNew(String iconClassNew) {
        this.iconClassNew = iconClassNew;
    }

}

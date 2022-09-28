package com.octal.actorPay.listners.events;

import com.octal.actorPay.entities.User;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

/**
 * @author naveen.kumawat
 * RegistrationCompleteEvent - publish event when user is successfully registered
 */
public class RegistrationCompleteEvent extends ApplicationEvent {

    private final String appUrl;
    private final Locale locale;
    private final User user;

    /**
     * @param user - newly created user object
     * @param locale - language
     * @param appUrl - service url
     */
    public RegistrationCompleteEvent(final User user, final Locale locale, final String appUrl) {
        super(user);
        this.user = user;
        this.locale = locale;
        this.appUrl = appUrl;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public Locale getLocale() {
        return locale;
    }

    public User getUser() {
        return user;
    }
}
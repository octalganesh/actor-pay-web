package com.octal.actorPay.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Enumeration;
import java.util.regex.Pattern;

public final class RequestWrapper extends HttpServletRequestWrapper {

    public RequestWrapper(HttpServletRequest servletRequest) {
        super(servletRequest);
    }


    public String getHeader(String name) {
        String value = super.getHeader(name);
        if (value == null)
            return null;
        return value;

    }

}

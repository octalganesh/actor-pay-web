package com.octal.actorPay.utils;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {
    /**
     * This method is used to generate random number for the given bound value
     *
     * @param bound -  is used to get a random number between 0(inclusive) and the number passed in this argument(n), exclusive
     * @return - it returns generated random number
     */
    public static String getRandomNumber(int bound) {
        Random random = new Random();
        return String.valueOf(random.nextInt(bound));
    }

    public static String getRandomNumber(int bound, int length) {
        Random random = new Random();
        return String.format("%0" + length + "d", random.nextInt(bound));
    }

    public static String getHttpRequestHeader(HttpServletRequest httpServletRequest, String headerName) {
        return httpServletRequest.getHeader(headerName);
    }
    public static int daysBetween(LocalDateTime from, LocalDateTime to) {
        int days = Long.valueOf(ChronoUnit.DAYS.between(from, to)).intValue();
        return days;
    }
    public static String getCurrentUser(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getHeader("userName");
    }

    public static boolean isNumeric(String number) {
        return StringUtils.isNumeric(number);
    }

    public static boolean isValidMobileNo(String mobileNo) {
        Pattern mobilePattern = Pattern.compile("(\\+91)[0-9]{10}");
        Matcher m = mobilePattern.matcher(mobileNo);
        return m.matches();
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern emailPatter = Pattern.compile(emailRegex);
        return emailPatter.matcher(email).matches();
    }

    public static boolean isExpired(LocalDateTime date) {
        if (date.isBefore(LocalDateTime.now())) {
            return (Boolean.TRUE);
        }
        return (Boolean.FALSE);
    }
}
package com.octal.actorPay.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.swing.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {

    public static String generate4DigitOTP() {
        Random rnd = new Random();
        int number = rnd.nextInt(9999);
        return String.format("%04d", number);
    }

    public static boolean isEmpty(String string) {
        if (null == string) return true;
        return string.length() == 0;
    }

    public static boolean isEmpty(Long value) {
        if (null == value) return true;
        return value <= 0;
    }

    public static boolean isEmpty(Integer value) {
        if (null == value) return true;
        return value <= 0;
    }

    public static boolean isEmpty(Double value) {
        if (null == value) return true;
        return value <= 0;
    }

    public static boolean isValidEmail(String email) {
        String emailExp = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,10}$";
        Pattern pattern = Pattern.compile(emailExp, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static String getEncodedPassword(String password) {
        if (isEmpty(password)) {
            return null;
        }
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }

    public static String generate6DigitNumber() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }

    public static String formatMoneyAmount(double moneyAmount) {
        DecimalFormat format = new DecimalFormat("##.00");
        return format.format(moneyAmount);
    }

    public static String md5encryption(String text) {
        if (isEmpty(text)) {
            return null;
        }
        String hashtext = null;
        try {
            String plaintext = text;
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(plaintext.getBytes());
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            hashtext = bigInt.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
        } catch (Exception e1) {
            JOptionPane.showMessageDialog(null, e1.getClass().getName() + ": " + e1.getMessage());
        }
        return hashtext;
    }

    public static String getRandomPassword() {
        return "1";
    }

    public static boolean isDateInBetweenIncludingEndPoints(final Date min, final Date max) {
        Calendar calendarMin = Calendar.getInstance();
        calendarMin.setTime(min);
        calendarMin.set(Calendar.DATE, 1);

        Calendar calendarMax = Calendar.getInstance();
        calendarMax.setTime(max);
        calendarMax.set(Calendar.DATE, 1);

        Calendar calendarNow = Calendar.getInstance();
        calendarNow.setTimeInMillis(0L);
        calendarNow.set(Calendar.HOUR_OF_DAY, Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        calendarNow.set(Calendar.MINUTE, Calendar.getInstance().get(Calendar.MINUTE));
        calendarNow.set(Calendar.DATE, 1);

        return !(calendarNow.getTime().before(calendarMin.getTime()) || calendarNow.getTime().after(calendarMax.getTime()));
    }

    public static String convertNumberTo10Digit(Long number) {
        return String.format("%010d", number);
    }

    public static String getPaymentRequestTransactionId(Long number) {
        return "TXN-" + convertNumberTo10Digit(number);
    }

    public static boolean isNumber(String data) {
        try {
            Long.parseLong(data);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String get10CharCouponCode() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();
        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return generatedString.toUpperCase();
    }

    public static String getFacebookUserImage(String facebookUserId) {
        return "http://graph.facebook.com/" + facebookUserId + "/picture?type=" + "large";
    }

    public static String getStringDate(Date date, String format) {
//        DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
        DateFormat dateFormat = new SimpleDateFormat(format);
        Date currentDate = new Date();
        String currentDateString = dateFormat.format(currentDate);
        return currentDateString;
    }
}

package com.octal.actorPay.service.impl;

import com.google.gson.Gson;
import com.octal.actorPay.dto.FcmUserNotificationDTO;
import com.octal.actorPay.dto.UserNotificationListDTO;
import com.octal.actorPay.dto.enums.NotificationTypeEnum;
import com.octal.actorPay.entities.SystemUserNotification;
import com.octal.actorPay.model.FcmNotificationBean;
import com.octal.actorPay.repositories.SystemUserNotificationRepository;
import com.octal.actorPay.service.FcmService;
import com.octal.actorPay.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Component;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class FcmServiceImpl implements FcmService {

    @Autowired
    private SystemUserNotificationRepository systemUserNotificationRepository;

    private final String FcmAuthKey = "AAAAaOyC-ok:APA91bGlWiGm9uaryM_Lx-Rvfvyarz05b4nHwOGIwxxe1xanEzvPZ58sIlaP5JFVh-H42pkHCZpLGi6Pgeq1osiENpYG02umj_W5bfGyc-96T1PHh0OumAz3puQiosEuUeAUYA3_tTkG";   //Key 1
//    private final String FcmAuthKey = "AAAAaOyC-ok:APA91bF3i0td9vP4UAcYR3UaF_Y4KkyTJmz604tjiPo1E38kHMcgBVq-2VGwrNZeRJax_W73Kc2ODnD1MweERfl9uXNsLIvuQ9Ze8tNJuRuM39E4FVfkqWVMejLEO0gF7oI13OOqESyO"; //Key 2

    public void sendNotification(String deviceType, String deviceToken,
                                 FcmNotificationBean.Notification notification, FcmNotificationBean.Data data) {
//        ThreadUtils.executePrimaryThreadPool(() -> {
        new Thread(() -> {
            if (!TextUtils.isEmpty(deviceToken) && !deviceToken.equalsIgnoreCase("WEB")) {
                pushFCMNotification(deviceType, deviceToken, notification, data);
            }
        }).start();
//        });
    }

    @Override
    public void sendNotificationToUser(String title, String message,
                                       String imageUrl,
                                       NotificationTypeEnum notificationType, String notificationTypeId,
                                       FcmUserNotificationDTO systemUser) {
        if (systemUser == null) return;
        SystemUserNotification notificationRec = new SystemUserNotification();
        try {
            notificationRec.setNotificationTitle(title);
            notificationRec.setNotificationMessage(message);
            notificationRec.setNotificationImageUrl(imageUrl);
            notificationRec.setNotificationType(notificationType.toString());
            notificationRec.setNotificationTypeId(notificationTypeId);
            notificationRec.setSystemUserId(systemUser.getId());
            systemUserNotificationRepository.save(notificationRec);
        } catch (Exception e) {
            System.out.println("Error While send notification to user: " + e.getMessage());
        }
        if (!TextUtils.isEmpty(systemUser.getDeviceType())) {
            FcmNotificationBean.Notification notification = new FcmNotificationBean.Notification(notificationRec.getNotificationTitle(), notificationRec.getNotificationMessage());
            FcmNotificationBean.Data data = new FcmNotificationBean.Data(notificationRec.getNotificationTitle(), notificationRec.getNotificationMessage(),
                    notificationRec.getNotificationType(), notificationRec.getNotificationTypeId());
            if (systemUser.getDeviceType().equalsIgnoreCase("ANDROID")) {
                sendNotification("A", systemUser.getFcmDeviceToken(), notification, data);
            } else {
                sendNotification("IPHONE", systemUser.getFcmDeviceToken(), notification, data);
            }
        }
    }

    @Override
    public UserNotificationListDTO getUserNotification(UserNotificationListDTO.ListRequest request) {
        Pageable pageable = PageRequest.of(request.getPageNo(), request.getPageSize(), Sort.by("createdAt").descending());
        UserNotificationListDTO response = new UserNotificationListDTO();
        List<UserNotificationListDTO.Record> responseList = new ArrayList<>();
        Page<SystemUserNotification> recordList = systemUserNotificationRepository.findBySystemUserId(request.getUserId(),pageable);
        for(SystemUserNotification systemUserNotification : recordList){
            UserNotificationListDTO.Record record = new UserNotificationListDTO.Record();
            record.setMessage(systemUserNotification.getNotificationMessage());
            record.setNotificationImageUrl(systemUserNotification.getNotificationImageUrl());
            long s = Duration.between(systemUserNotification.getCreatedAt().toInstant(), new Date().toInstant()).getSeconds();
            int day = (int) TimeUnit.SECONDS.toDays(s);
            long hour = TimeUnit.SECONDS.toHours(s) - (day * 24);
            long minute = TimeUnit.SECONDS.toMinutes(s) - (TimeUnit.SECONDS.toHours(s) * 60);
            long second = TimeUnit.SECONDS.toSeconds(s) - (TimeUnit.SECONDS.toMinutes(s) * 60);
            if (day > 0) {
                record.setTime(day + "days ago");
            } else if (day == 0 && hour > 0) {
                record.setTime(hour + "h ago");
            } else if (hour == 0 && minute > 0) {
                record.setTime(minute + "min ago");
            } else if (minute == 0) {
                record.setTime("Just Now");
            } else {
                record.setTime("Just Now");
            }
            record.setType(systemUserNotification.getNotificationType().toString());
            record.setTypeId(systemUserNotification.getNotificationTypeId());
            record.setSeen(systemUserNotification.isSeen());
            record.setUserId(systemUserNotification.getSystemUserId());
            responseList.add(record);
        }
        response.setItems(responseList);
        response.setPageNumber(request.getPageNo());
        response.setPageSize(request.getPageSize());
        response.setTotalItems(recordList.getTotalElements());
        response.setTotalPages(recordList.getTotalPages());
        return response;
    }


    private boolean pushFCMNotification(String deviceType, String userDeviceIdKey,
                                        FcmNotificationBean.Notification notification, FcmNotificationBean.Data data) {

        if (TextUtils.isEmpty(FcmAuthKey)) {
            return false;
        }
        try {
            String authKey = FcmAuthKey;   // You FCM AUTH key
            String FMCurl = "https://fcm.googleapis.com/fcm/send";
            URL url = new URL(FMCurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "key=" + authKey);
            conn.setRequestProperty("Content-Type", "application/json");
            //conn.setRequestProperty("Content-Type", "text/plain");

            FcmNotificationBean fcmNotificationBean = new FcmNotificationBean();
            fcmNotificationBean.setTo(userDeviceIdKey);
//            if (notification != null) {
//                if (deviceType != null && deviceType.equals("IPHONE")) {
//                    fcmNotificationBean.setNotification(notification);
//                }
//            }
            if (data != null) {
                fcmNotificationBean.setData(data);
                if (data.isBackground()) {
                    fcmNotificationBean.setContent_available(false);
                    fcmNotificationBean.setMutable_content(true);
                    fcmNotificationBean.setCategory("IMAGE");
                } else {
                    fcmNotificationBean.setContent_available(true);
                    fcmNotificationBean.setMutable_content(false);
                }
            }
            System.out.println("Notification Data: ");
            System.out.println(new Gson().toJson(fcmNotificationBean));
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(new Gson().toJson(fcmNotificationBean));
            wr.flush();
            conn.getInputStream();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
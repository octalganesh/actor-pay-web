package com.octal.actorPay.constants;

/**
 * @author - Naveen Kumawat
 * this class contain all the constants values
 */
public class EndPointConstants {
    public static final String ADMIN_MICROSERVICE_URL = "admin-service";
    public static final String USER_MICROSERVICE = "user-service";
    public static final String BASE_API = "/api";

    public interface CMSServiceConstants {
        String CMS_MICROSERVICE = "cms-service";
        String CMS_BASE_URL = "http://localhost:8088/";
        String GET_EMAIL_TEM_BY_ID = "/email-template/by/id";
    }
}
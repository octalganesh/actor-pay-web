package com.octal.actorPay.constants;

/**
 * @author - Naveen Kumawat
 * this class contain all the constants values
 */
public class EndPointConstants {
    public static final String ADMIN_MICROSERVICE_URL = "admin-service";
    public static final String USER_MICROSERVICE = "user-service";
    public static final String BASE_API = "/api";

    public interface UserServiceConstants {
        String FETCH_USER_BY_USERNAME = "/users/auth/details/by/name/{username}";
        String ADD_USER_API = "/users/add/new/user";
        String UPDATE_USER_API = "/users/update";
        String DELETE_USER_BY_IDS_API = "/users/delete/by/ids";
        String GET_USER_DETAILS_URL = "/users/by/id/{id}";
        String GET_ALL_USERS_URL = "/users/get/all/user/paged";
        String FETCH_USER_BASE_URL = "http://localhost:8084/";
    }
}
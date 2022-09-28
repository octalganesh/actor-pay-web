package com.octal.actorPay.constants;

/**
 * @author - Naveen Kumawat
 * This class contain all the constants values
 */
public class EndPointConstants {
    public static final String ADMIN_MICROSERVICE = "admin-service";
    public static final String USER_MICROSERVICE = "user-service";
    public static final String MERCHANT_MICROSERVICE = "merchant-service";
    public static final String BASE_API = "/api";

    public interface UserServiceConstants {
        String FETCH_USER_BY_EMAIL = "/users/auth/details/by/email/{email}";
        String FETCH_USER_BASE_URL = "http://localhost:8084/";
    }

    public interface AdminServiceConstants {
        String FETCH_ADMIN_BY_EMAIL = "/auth/details/by/email/{email}";
        String FETCH_ADMIN_BASE_URL = "http://localhost:8083/";
    }

    public interface MerchantServiceConstants {
        String FETCH_MERCHANT_BY_EMAIL = "/merchant/auth/details/by/email/{email}";
        String FETCH_MERCHANT_BASE_URL = "http://localhost:8086/";
    }
}

package com.octal.actorPay.constants;

/**
 * @author - Naveen Kumawat
 * this class contain all the constants values
 */
public class EndPointConstants {
    public static final String ADMIN_MICROSERVICE_URL = "admin-service";
    public static final String USER_MICROSERVICE = "user-service";
    public static final String BASE_API = "/api";

    //TODO create separate-2 class for each service
    public interface UserServiceConstants {
        String FETCH_USER_BY_USERNAME = "/users/auth/details/by/name/{username}";
        String ADD_USER_API = "/users/add/new/user";
        String UPDATE_USER_API = "/users/update";
        String DELETE_USER_BY_IDS_API = "/users/delete/by/ids";
        String GET_USER_DETAILS_URL = "/users/by/id/{id}";
        String GET_ALL_USERS_URL = "/users/get/all/user/paged";
        String FETCH_USER_BASE_URL = "http://localhost:8084/";
        String CHANGE_USER_STATUS = "/users/change/status";
    }
    public interface MerchantServiceConstants {
        String MERCHANT_MICROSERVICE = "merchant-service";
        String MERCHANT_BASE_URL = "http://localhost:8086/";
        String GET_ALL_MERCHANT_URL = "/merchant/get/all/paged";

        String GET_ALL_CATEGORIES_URL = "/get/all/categories/paged";
        String GET_ALL_SUB_CATEGORIES_URL = "/get/all/subcategories/paged";
        String GET_ALL_ACTIVE_CATEGORIES_URL = "/get/all/active/categories";
        String ADD_NEW_CATEGORIES = "/add/new/category";
        String ADD_NEW_SUB_CATEGORIES = "/create/new/subcategory";
        String UPDATE_CATEGORIES = "/update/category";
        String UPDATE_SUB_CATEGORIES = "/update/subcategory";
        String DELETE_CATEGORIES = "/delete/categories/by/ids";
        String DELETE_SUB_CATEGORIES = "/delete/subcategories/by/ids";
        String CHANGE_CATEGORY_STATUS = "/change/category/status";
        String CHANGE_SUB_CATEGORY_STATUS = "/change/subcategory/status";

        String GET_ALL_CATEGORIES_LIST_URL = "/get/all/categories";
    }

    public interface GlobalServiceConstants {
        String GLOBAL_MICROSERVICE = "global-service";
        String GLOBAL_BASE_URL = "http://localhost:8089/";
//        String GET_ALL_CATEGORIES_URL = "/get/all/categories/paged";
//        String GET_ALL_SUB_CATEGORIES_URL = "/get/all/subcategories/paged";
//        String GET_ALL_ACTIVE_CATEGORIES_URL = "/get/all/active/categories";
//        String ADD_NEW_CATEGORIES = "/create/new/category";
//        String ADD_NEW_SUB_CATEGORIES = "/create/new/subcategory";
//        String UPDATE_CATEGORIES = "/update/category";
//        String UPDATE_SUB_CATEGORIES = "/update/subcategory";
//        String DELETE_CATEGORIES = "/delete/categories/by/ids";
//        String DELETE_SUB_CATEGORIES = "/delete/subcategories/by/ids";
//        String CHANGE_CATEGORY_STATUS = "/change/category/status";
//        String CHANGE_SUB_CATEGORY_STATUS = "/change/subcategory/status";
        String GET_ALL_GLOBAL_SETTINGS = "/v1/global/setting/read";
        String ADD_UPDATE_GLOBAL_SETTINGS = "/v1/global/setting/create/or/update";
//        String GET_ALL_CATEGORIES_LIST_URL = "/get/all/categories";
//
        String GET_MONEY_TRANSFER_LIMIT_DATA_URL = "/v1/money/transfer/get/money/limit";
        String CREATE_UPDATE_MONEY_TRANSFER_LIMIT_DATA_URL = "/v1/money/transfer/create/or/update";
    }

        public interface CMSServiceConstants {
        String CMS_MICROSERVICE = "cms-service";
        String CMS_BASE_URL = "http://localhost:8088/";
        String GET_ALL_FAQ_URL = "/faq/all/paged";
        String GET_ALL_CMS_URL = "/cms/all";
        String CMS_UPDATE_URL = "/cms/update";
        String EMAIL_TEMP_UPDATE_URL = "/email-template/update";
        String READ_CMS_BY_ID_URL = "/cms/by/id";
        String GET_LOYAL_REWARD_SETTING_DATA_URL = "/get/loyal/reward/points/setting/data";
        String CREATE_UPDATE_LOYAL_REWARD_SETTING_DATA_URL = "/loyal/reward/points/create/or/update";
        String GET_ALL_EMAIL_TEMPLATES_URL = "/email-template/all";
        String GET_FAQ_BY_ID_URL = "/faq/by/id";
        String ADD_NEW_FAQ = "/faq/add/new";
        String UPDATE_FAQ_URL = "/faq/update";
        String DELETE_FAQ_BY_IDs = "/faq/delete/by/ids";
        String GET_EMAIL_TEM_BY_ID = "/email-template/by/id";

    }
}
package com.octal.actorPay.transformer;

import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.entities.MerchantReportHistory;
import com.octal.actorPay.entities.Outlet;
import com.octal.actorPay.entities.Product;
import com.octal.actorPay.entities.Role;
import com.octal.actorPay.entities.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;


/**
 * PagedItemsTransformer provide pagination and sorting details, which contain
 * entity and entities sorting field
 *
 * @author Naveen
 */
public class PagedItemsTransformer implements Serializable {

    private static final long serialVersionUID = 3626239235906072993L;
    private static final String ID = "id";
    private static final String USER_CREATE_DATE = "createdAt";
    private static final String CREATE_DATE = "createdAt";


    /**
     * SORTING class contains entity and entity's sorting field,
     */
    private static final Map<Class<?>, Map<String, String>> SORTING = new HashMap<Class<?>, Map<String, String>>() {

        private static final long serialVersionUID = 1L;

        {
            put(User.class, new HashMap<String, String>() {
                private static final long serialVersionUID = 1L;

                {
                    put(USER_CREATE_DATE, USER_CREATE_DATE);
                    put("status", "isActive");
                    put("firstName", "firstName");
                    put("lastName", "lastName");
                    put("email", "email");
                    put("contactNumber", "contactNumber");
                }
            });

            put(Product.class, new HashMap<String, String>() {
                private static final long serialVersionUID = 1L;
                {
                    put(CREATE_DATE, CREATE_DATE);
                    put(CommonConstant.ACTUAL_PRICE, CommonConstant.ACTUAL_PRICE);
                    put(CommonConstant.DEAL_PRICE, CommonConstant.DEAL_PRICE);
                    put(CommonConstant.STOCK_COUNT, CommonConstant.STOCK_COUNT);
                    put(CommonConstant.NAME,CommonConstant.NAME);
                    put(CommonConstant.STATUS,CommonConstant.STATUS);
                }
            });

            put(Outlet.class, new HashMap<String, String>() {
                private static final long serialVersionUID = 1L;

                {
                    put(USER_CREATE_DATE, USER_CREATE_DATE);
                    put("title", "title");
                    put("licenceNumber", "licenceNumber");
                    put("resourceType", "resourceType");
                    put("status", "isActive");

                }
            });

            put(Role.class, new HashMap<String, String>() {
                private static final long serialVersionUID = 1L;

                {
                    put(USER_CREATE_DATE, USER_CREATE_DATE);
                    put("status", "isActive");
                    put("name", "name");
                    put("description", "description");
                }
            });

            put(MerchantReportHistory.class, new HashMap<String, String>() {
                private static final long serialVersionUID = 1L;

                {
                    put(USER_CREATE_DATE, USER_CREATE_DATE);
                    put("reportName", "reportName");
                    put("reportURL", "reportURL");
                    put("reportType", "reportType");
                }
            });

        }
    };

    /**
     * this method provides pagination regarding information
     *
     * @param pagedInfo - this object contain pagination information like page
     *                  number, page item
     * @return - return - returns page request object with provided page info
     */
    public static PageRequest toPageRequest(Class<?> entity, PagedItemInfo pagedInfo) {

        return PageRequest.of(pagedInfo.page, pagedInfo.items,
                Sort.by(new Sort.Order(pagedInfo.isSortingAsc ? ASC : DESC, resolve(entity, pagedInfo.sortingField))));
    }


    /**
     * resolve method validates the entity type and entity sorting field, if sorting
     * is not available on field then it throws error with message 'Unknown field to
     * sort'
     *
     * @param entity    - entity name
     * @param filedName - sorting field name
     * @return - returns valid sorting field name
     */
    private static String resolve(Class<?> entity, String filedName) {
        final Map<String, String> map = SORTING.get(entity);
        if (map == null) {
            throw new IllegalArgumentException("Unknown entity type to sort: " + entity);
        }
        String field = map.get(filedName);
        if (field == null) {
            throw new IllegalArgumentException("Unknown field to sort: " + filedName);
        }
        return field;
    }

}
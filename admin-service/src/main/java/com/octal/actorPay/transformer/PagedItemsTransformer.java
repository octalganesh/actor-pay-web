package com.octal.actorPay.transformer;

import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.entities.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.octal.actorPay.constants.CommonConstant.CREATE_DATE;
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


    /**
     * SORTING class contains entity and entity's sorting field,
     */
    private static final Map<Class<?>, Map<String, String>> SORTING = new HashMap<Class<?>, Map<String, String>>() {

        private static final long serialVersionUID = 1L;

        {
            put(User.class, new HashMap<String, String>() {
                private static final long serialVersionUID = 1L;

                {
                    put(CREATE_DATE, CREATE_DATE);
                    put("status", "isActive");
                    put("name", "firstName");
                }
            });

            put(Screens.class, new HashMap<String, String>() {
                private static final long serialVersionUID = 1L;
                {
                    put("name", "screenName");
                }
            });

            put(Role.class, new HashMap<String, String>() {
                private static final long serialVersionUID = 1L;
                {
                    put("updatedAt", "updatedAt");
                    put("name", "name");
                    put("createdAt", "createdAt");
                    put("active", "isActive");

                }
            });

            put(Categories.class, new HashMap<String, String>() {
                private static final long serialVersionUID = 1L;

                {
                    put(CREATE_DATE, CREATE_DATE);
                    put(CommonConstant.STATUS, CommonConstant.STATUS);
                    put(CommonConstant.NAME, CommonConstant.NAME);

                }
            });
            put(SubCategories.class, new HashMap<String, String>() {
                private static final long serialVersionUID = 1L;

                {
                    put(CREATE_DATE, CREATE_DATE);
                    put(CommonConstant.STATUS, CommonConstant.STATUS);
                    put(CommonConstant.NAME, CommonConstant.NAME);
                }
            });
            put(Tax.class, new HashMap<String, String>() {
                private static final long serialVersionUID = 1L;

                {
                    put(CREATE_DATE, CREATE_DATE);
                    put(CommonConstant.STATUS, CommonConstant.STATUS);
                    put(CommonConstant.HSN_CODE, CommonConstant.HSN_CODE);
                    put(CommonConstant.TAX_PERCENTAGE,CommonConstant.TAX_PERCENTAGE);
                }
            });
            put(Offer.class, new HashMap<String, String>() {
                private static final long serialVersionUID = 1L;

                {
                    put(CREATE_DATE, CREATE_DATE);
                    put(CommonConstant.NAME, CommonConstant.NAME);
                    put(CommonConstant.OFFER_TITLE, CommonConstant.OFFER_TITLE);
                    put(CommonConstant.OFFER_IN_PERCENTAGE, CommonConstant.OFFER_IN_PERCENTAGE);
                    put(CommonConstant.OFFER_IN_PRICE, CommonConstant.OFFER_IN_PRICE);
                    put(CommonConstant.OFFER_START_DATE, CommonConstant.OFFER_START_DATE);
                    put(CommonConstant.OFFER_END_DATE, CommonConstant.OFFER_END_DATE);
                    put(CommonConstant.OFFER_TYPE, CommonConstant.OFFER_TYPE);
                    put(CommonConstant.NUMBER_OF_USAGE, CommonConstant.NUMBER_OF_USAGE);
                    put(CommonConstant.ORDER_PER_DAY,CommonConstant.ORDER_PER_DAY);
                    put(CommonConstant.STATUS,CommonConstant.STATUS);
                }
            });

            put(LoyaltyRewards.class, new HashMap<String, String>() {
                private static final long serialVersionUID = 1L;

                {
                    put(CREATE_DATE, CREATE_DATE);
                    put(CommonConstant.EVENT, CommonConstant.EVENT);
                    put(CommonConstant.PRICE_LIMIT, CommonConstant.PRICE_LIMIT);
                    put(CommonConstant.REWARD_POINT, CommonConstant.REWARD_POINT);
                    put(CommonConstant.SINGLE_USER_LIMIT, CommonConstant.SINGLE_USER_LIMIT);
                }
            });

            put(LoyaltyRewardsHistory.class, new HashMap<String, String>() {
                private static final long serialVersionUID = 1L;

                {
                    put(CREATE_DATE, CREATE_DATE);
                    put(CommonConstant.EVENT, CommonConstant.EVENT);
                    put(CommonConstant.LOYALTY_ORDER_ID, CommonConstant.LOYALTY_ORDER_ID);
                    put(CommonConstant.REWARD_POINT, CommonConstant.REWARD_POINT);
                    put(CommonConstant.LOYALTY_TRANSACTION_ID, CommonConstant.LOYALTY_TRANSACTION_ID);
                    put(CommonConstant.LOYALTY_REASON, CommonConstant.LOYALTY_REASON);
                }
            });

            put(SystemConfiguration.class, new HashMap<String, String>() {
                private static final long serialVersionUID = 1L;
                {
                    put(CREATE_DATE, CREATE_DATE);
                    put(CommonConstant.STATUS, CommonConstant.STATUS);
                    put(CommonConstant.SystemConfigurationConstants.PARAM_NAME, CommonConstant.SystemConfigurationConstants.PARAM_NAME);
                    put(CommonConstant.SystemConfigurationConstants.PARAM_VALUE, CommonConstant.SystemConfigurationConstants.PARAM_VALUE);
                    put(CommonConstant.SystemConfigurationConstants.PARAM_DESCRIPTION, CommonConstant.SystemConfigurationConstants.PARAM_DESCRIPTION);
                    put(CommonConstant.SystemConfigurationConstants.MODULE, CommonConstant.SystemConfigurationConstants.MODULE);
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
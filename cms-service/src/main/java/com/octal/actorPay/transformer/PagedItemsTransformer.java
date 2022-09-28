package com.octal.actorPay.transformer;

import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.entities.Cms;
import com.octal.actorPay.entities.EmailTemplate;
import com.octal.actorPay.entities.FAQ;
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
 */
public class PagedItemsTransformer implements Serializable {

    private static final long serialVersionUID = 3626239235906072993L;
    private static final String ID = "id";


    /**
     * SORTING class contains entity and entity's sorting field,
     */
    private static final Map<Class<?>, Map<String, String>> SORTING = new HashMap<Class<?>, Map<String, String>>() {

        private static final long serialVersionUID = 1L;

        {
            put(Cms.class, new HashMap<String, String>() {
                private static final long serialVersionUID = 1L;

                {
                    put(CommonConstant.CMS_UPDATE_DATE, CommonConstant.CMS_UPDATE_DATE);
                    put(CommonConstant.CMS_TITLE, CommonConstant.CMS_TITLE);
                }
            });
            put(EmailTemplate.class, new HashMap<String, String>() {
                private static final long serialVersionUID = 1L;

                {
                    put(CommonConstant.CMS_UPDATE_DATE, CommonConstant.CMS_UPDATE_DATE);
                    put(CommonConstant.CMS_TITLE, CommonConstant.CMS_TITLE);
                    put(CommonConstant.CMS_SUBJECT,CommonConstant.CMS_SUBJECT);
                }
            });
            put(FAQ.class, new HashMap<String, String>() {
                private static final long serialVersionUID = 1L;

                {
                    put(CommonConstant.CMS_UPDATE_DATE, CommonConstant.CMS_UPDATE_DATE);
                    put(CommonConstant.FAQ_QUESTION,CommonConstant.FAQ_QUESTION);
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
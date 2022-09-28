package com.octal.actorPay.transformer;

import com.octal.actorPay.dto.PagedItemInfo;
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
    private static final String CREATED_AT = "createdAt";

    /**
     * SORTING class contains entity and entity's sorting field,
     */
    private static final Map<Class<?>, Map<String, String>> SORTING = new HashMap<Class<?>, Map<String, String>>() {

        private static final long serialVersionUID = 1L;

        {
//            put(Categories.class, new HashMap<String, String>() {
//                private static final long serialVersionUID = 1L;
//
//                {
//                    put(CREATED_AT, CREATED_AT);
//                    put("name", "name");
//                    put("status", "isActive");
//
//                }
//            });
//            put(SubCategories.class, new HashMap<String, String>() {
//                private static final long serialVersionUID = 1L;
//
//                {
//                    put(CREATED_AT, CREATED_AT);
//                    put("name", "name");
//                    put("status", "isActive");
//                    put("category", "categories.name");
//                }
//            });

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
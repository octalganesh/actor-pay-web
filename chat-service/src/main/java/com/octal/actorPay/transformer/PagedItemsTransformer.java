package com.octal.actorPay.transformer;

import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.model.Chat;
import com.octal.actorPay.model.ChatRoom;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.octal.actorPay.constants.CommonConstant.CREATE_DATE;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

public class PagedItemsTransformer implements Serializable {

    private static final long serialVersionUID = 3626239235906072993L;

    private static final Map<Class<?>, Map<String, String>> SORTING = new HashMap<Class<?>, Map<String, String>>() {

        private static final long serialVersionUID = 1L;

        {
            put(Chat.class, new HashMap<String, String>() {
                private static final long serialVersionUID = 1L;

                {
                    put(CREATE_DATE, CREATE_DATE);
                    put("senderName", "senderName");
                    put("chatRoomId", "chatRoomId");
                    put("message", "message");
                }
            });
        }

        {
            put(ChatRoom.class, new HashMap<String, String>() {
                private static final long serialVersionUID = 1L;

                {
                    put(CREATE_DATE, CREATE_DATE);
                    put("orderId", "orderId");
                    put("userName", "userName");
                }
            });
        }
    };

    public static PageRequest toPageRequest(Class<?> entity, PagedItemInfo pagedInfo) {

        return PageRequest.of(pagedInfo.page, pagedInfo.items,
                Sort.by(new Sort.Order(pagedInfo.isSortingAsc ? ASC : DESC, resolve(entity, pagedInfo.sortingField))));
    }

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

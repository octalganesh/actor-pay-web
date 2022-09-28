package com.octal.actorPay.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserNotificationListDTO {
    private int totalPages;
    private long totalItems;
    private List<Record> items;
    private int pageNumber;
    private int pageSize;

    @Data
    public static class Record{
        private String userId;
        private String userImageUrl;
        private String userName;
        private String message;
        private String typeId;
        private String type;
        private String notificationImageUrl;
        private String time;
        private Boolean seen;
    }

    @Data
    public static class ListRequest{
        private Integer pageNo;
        private Integer pageSize;
        private String sortBy;
        private boolean asc;
        private String userId;
    }


}

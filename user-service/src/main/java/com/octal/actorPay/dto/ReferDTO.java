package com.octal.actorPay.dto;

import com.octal.actorPay.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReferDTO {

    private String referalCode;

    private String inviteToUserEmail;

    private User fromUserId;

    private String fromUserEmail;


}

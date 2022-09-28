package com.octal.actorPay.dto.payments;

import lombok.Data;

@Data
public class ViewBankResponse extends BankAccountResponse {

    private String contactId;
    private String fundAccountId;
    private String accountType;
    private String ifscCode;

}

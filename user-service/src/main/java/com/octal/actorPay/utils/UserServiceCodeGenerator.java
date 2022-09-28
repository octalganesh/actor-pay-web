package com.octal.actorPay.utils;

import com.octal.actorPay.config.UserSettingsConfig;
import com.octal.actorPay.entities.DisputeItem;
import com.octal.actorPay.entities.OrderDetails;
import com.octal.actorPay.repositories.DisputeItemRepository;
import com.octal.actorPay.repositories.OrderDetailsRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class UserServiceCodeGenerator {

    private OrderDetailsRepository orderDetailsRepository;

    private DisputeItemRepository disputeItemRepository;

    private UserSettingsConfig userSettingsConfig;

    public UserServiceCodeGenerator(OrderDetailsRepository orderDetailsRepository, 
                                    DisputeItemRepository disputeItemRepository,UserSettingsConfig userSettingsConfig) {
        this.orderDetailsRepository = orderDetailsRepository;
        this.disputeItemRepository=disputeItemRepository;
        this.userSettingsConfig=userSettingsConfig;
    }


    public String getReceipt() {
        String  newReceipt = "";
        String receipt = "";
        do {
            newReceipt = getCode(userSettingsConfig.getOrderReceipt());
            receipt = orderDetailsRepository.findByReceiptId(newReceipt);

            if (StringUtils.isBlank(receipt)) {
                receipt = newReceipt;
            }
        } while (!StringUtils.isNotBlank(receipt));
        return receipt;
    }

    public String getNewCode() {
        String code = "";
        OrderDetails orderDetails = null;
        do {
            String newCode = getCode(userSettingsConfig.getOrderCode());
            orderDetails = orderDetailsRepository.findByOrderNoAndDeletedFalse(newCode);
            if (orderDetails == null) {
                code = newCode;
            }
        } while (orderDetails != null);
        return code;
    }

    private String getCode(String prefix) {
        Random r = new Random(System.currentTimeMillis());
        int id = ((1 + r.nextInt(2)) * 1000000 + r.nextInt(1000000));
        String codePrefix = prefix;
        return codePrefix + id;
    }

    public String getDisputeCode() {
        String code = "";
        DisputeItem disputeItem = null;
        do {
            String newCode = getCode(userSettingsConfig.getDisputeCode());
            disputeItem = disputeItemRepository.findByDisputeCodeAndDeletedFalse(newCode);
            if (disputeItem == null) {
                code = newCode;
            }
        } while (disputeItem != null);
        return code;
    }

}

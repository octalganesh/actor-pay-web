package com.octal.actorPay.utils;

import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.constants.OrderStatus;
import com.octal.actorPay.dto.PercentageCharges;
import com.octal.actorPay.entities.OrderItem;
import com.octal.actorPay.entities.OrderItemCommission;
import org.springframework.stereotype.Component;

@Component
public class PercentageCalculateManager {

    public PercentageCharges calculatePercentage(OrderItem orderItem, String chargeType) throws Exception {
        double percentage = 0l;
        double taxableValue = orderItem.getTaxableValue();
        double productPrice = orderItem.getProductPrice();
        double amount = 0;
        OrderItemCommission orderItemCommission = orderItem.getOrderItemCommission();
        if (chargeType.equalsIgnoreCase(OrderStatus.RETURNING.name())) {
            percentage = orderItemCommission.getReturnFee();
            amount = productPrice * orderItem.getProductQty();
        }
        if (chargeType.equalsIgnoreCase(OrderStatus.CANCELLED.name())) {
            percentage = orderItemCommission.getCancellationFee();
            amount = productPrice * orderItem.getProductQty();
        }
        if (chargeType.equalsIgnoreCase(CommonConstant.ADMIN_COMMISSION)) {
            percentage = orderItemCommission.getAdminCommission();
            amount = taxableValue * orderItem.getProductQty();
        }
        double percentageAmount = (amount * percentage) / 100;
        PercentageCharges percentageCharges = new PercentageCharges();
        percentageCharges.setPercentageCharges(percentageAmount);
        percentageCharges.setBalanceAmount(amount - percentageAmount);
        percentageCharges.setOriginalAmount(amount);
        percentageCharges.setPercentage(percentage);
        return percentageCharges;
    }
}

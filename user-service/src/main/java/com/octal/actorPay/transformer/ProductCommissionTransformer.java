package com.octal.actorPay.transformer;

import com.octal.actorPay.dto.ProductCommissionDTO;
import com.octal.actorPay.entities.ProductCommission;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.function.Function;

public class ProductCommissionTransformer {

    public static Function<ProductCommissionDTO, ProductCommission>
            DTO_ENTITY = (productCommissionDTO) -> {

        ProductCommission productCommission = new ProductCommission();
        productCommission.setCommissionPercentage(productCommissionDTO.getCommissionPercentage());
        productCommission.setActorCommissionAmt(productCommissionDTO.getActorCommissionAmt());
        productCommission.setProductId(productCommissionDTO.getProductId());
        productCommission.setOrderNo(productCommissionDTO.getOrderNo());
        productCommission.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        productCommission.setMerchantId(productCommissionDTO.getMerchantId());
        productCommission.setMerchantEarnings(productCommissionDTO.getMerchantEarnings());
        productCommission.setActive(Boolean.TRUE);
        productCommission.setQuantity(productCommissionDTO.getQuantity());
        productCommission.setSettlementStatus(productCommissionDTO.getSettlementStatus());
        productCommission.setOrderStatus(productCommissionDTO.getOrderStatus());
        return productCommission;
    };

    public static Function<ProductCommission, ProductCommissionDTO>
            ENTITY_DTO = (productCommission) -> {

        ProductCommissionDTO productCommissionDTO = new ProductCommissionDTO();
        productCommissionDTO.setId(productCommission.getId());
        productCommissionDTO.setCommissionPercentage(productCommission.getCommissionPercentage());
        productCommissionDTO.setActorCommissionAmt(productCommission.getActorCommissionAmt());
        productCommissionDTO.setProductId(productCommission.getProductId());
        productCommissionDTO.setOrderNo(productCommission.getOrderNo());
        productCommissionDTO.setCreatedAt(productCommission.getCreatedAt());
        productCommissionDTO.setActive(productCommission.getActive());
        productCommissionDTO.setMerchantId(productCommission.getMerchantId());
        productCommissionDTO.setMerchantEarnings(productCommission.getMerchantEarnings());
        productCommissionDTO.setQuantity(productCommission.getQuantity());
        productCommissionDTO.setSettlementStatus(productCommission.getSettlementStatus());
        productCommissionDTO.setOrderStatus(productCommission.getOrderStatus());
        return productCommissionDTO;
    };
}

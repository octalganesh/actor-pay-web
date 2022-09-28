package com.octal.actorPay.service;


import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.ProductCommissionDTO;
import com.octal.actorPay.dto.ProductCommissionReport;
import com.octal.actorPay.dto.request.ProductCommissionFilterRequest;
import com.octal.actorPay.entities.OrderItem;
import com.octal.actorPay.entities.ProductCommission;

import java.util.List;

public interface ProductCommissionService {

    void save(List<ProductCommission> productCommissions);

    ProductCommission findByOrderItemAndDeletedFalse(OrderItem orderItem);

    ProductCommissionReport getAllProductCommission(PagedItemInfo pagedInfo, ProductCommissionFilterRequest filterRequest);

    void updateProductCommissionStatus(String settlementStatus, List<String> Ids) throws Exception;

    List<ProductCommissionDTO> findBySettlementStatus(String settlementStatus);

    Float getAdminTotalCommissionByOrderStatus(String orderStatus);

}

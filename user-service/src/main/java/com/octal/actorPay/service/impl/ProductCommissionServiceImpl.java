package com.octal.actorPay.service.impl;

import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.constants.OrderStatus;
import com.octal.actorPay.dto.*;
import com.octal.actorPay.dto.request.ProductCommissionFilterRequest;
import com.octal.actorPay.entities.OrderItem;
import com.octal.actorPay.entities.ProductCommission;
import com.octal.actorPay.exceptions.ActorPayException;
import com.octal.actorPay.feign.clients.MerchantClient;
import com.octal.actorPay.repositories.OrderItemRepository;
import com.octal.actorPay.repositories.ProductCommissionRepository;
import com.octal.actorPay.service.ProductCommissionService;
import com.octal.actorPay.specification.GenericSpecificationsBuilder;
import com.octal.actorPay.specification.SpecificationFactory;
import com.octal.actorPay.transformer.OrderItemTransformer;
import com.octal.actorPay.transformer.PagedItemsTransformer;
import com.octal.actorPay.transformer.ProductCommissionTransformer;
import com.octal.actorPay.utils.CommonUtils;
import com.octal.actorPay.utils.UserFeignHelper;
import feign.FeignException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductCommissionServiceImpl implements ProductCommissionService {

    private ProductCommissionRepository productCommissionRepository;

    private OrderItemRepository orderItemRepository;

    private MerchantClient merchantClient;

    private UserFeignHelper userFeignHelper;


    @Autowired
    private SpecificationFactory<ProductCommission> productCommissionSpecificationFactory;

    public ProductCommissionServiceImpl(ProductCommissionRepository productCommissionRepository,
                                        OrderItemRepository orderItemRepository,
                                        MerchantClient merchantClient, UserFeignHelper userFeignHelper) {
        this.productCommissionRepository = productCommissionRepository;
        this.orderItemRepository = orderItemRepository;
        this.merchantClient = merchantClient;
        this.userFeignHelper = userFeignHelper;
    }

    @Override
    public void save(List<ProductCommission> productCommissions) {
        List<ProductCommission> saveProductCommissionList = productCommissionRepository.saveAll(productCommissions);
        System.out.println("Instance class name " + saveProductCommissionList);
    }

    @Override
    public ProductCommission findByOrderItemAndDeletedFalse(OrderItem orderItem) {
        return productCommissionRepository.findByOrderItemAndDeletedFalse(orderItem);
    }


    @Override
    public ProductCommissionReport getAllProductCommission(PagedItemInfo pagedInfo, ProductCommissionFilterRequest filterRequest) {
        GenericSpecificationsBuilder<ProductCommission> builder = new GenericSpecificationsBuilder<>();
        // prepare search query
        double totalMerchantEarnings = 0d;
        double totalAdminCommission = 0d;
        prepareProductCommissionSearchQuery(filterRequest, builder);
        final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(ProductCommission.class, pagedInfo);

        Page<ProductCommission> pagedResult = productCommissionRepository.findAll(builder.build(), pageRequest);
        List<ProductCommissionDTO> commissionDTOS = new ArrayList<>();
        for (ProductCommission productCommission : pagedResult.getContent()) {
            ProductCommissionDTO dto = ProductCommissionTransformer.ENTITY_DTO.apply(productCommission);
            OrderItem orderItem = productCommission.getOrderItem();
            OrderItemDTO orderItemDTO = OrderItemTransformer.ORDER_ITEM_TO_ORDER_ITEM_DTO.apply(orderItem);
            dto.setOrderItemDTO(orderItemDTO);
            String productName = getProductName(dto.getProductId());
            String merchantName = getMerchantName(dto.getMerchantId());
            dto.setProductName(productName);
            dto.setMerchantName(merchantName);
            commissionDTOS.add(dto);
            totalMerchantEarnings = totalMerchantEarnings + dto.getMerchantEarnings();
            totalAdminCommission = totalAdminCommission + dto.getActorCommissionAmt();
        }
        Double merchantSettledTotal = commissionDTOS.stream()
                .filter(i -> i.getSettlementStatus().equals(CommonConstant.COMMISSION_SETTLED))
                .mapToDouble(i -> i.getMerchantEarnings())
                .sum();

        Double merchantPendingTotal = commissionDTOS.stream()
                .filter(i -> i.getSettlementStatus().equals(CommonConstant.COMMISSION_PENDING))
                .mapToDouble(i -> i.getMerchantEarnings())
                .sum();

        Double merchantCancelledTotal = commissionDTOS.stream()
                .filter(i -> i.getSettlementStatus().equals(CommonConstant.COMMISSION_CANCELLED))
                .mapToDouble(i -> i.getMerchantEarnings())
                .sum();

        Double adminSettledTotal = commissionDTOS.stream()
                .filter(i -> i.getSettlementStatus().equals(CommonConstant.COMMISSION_SETTLED))
                .mapToDouble(i -> i.getActorCommissionAmt())
                .sum();

        Double adminPendingTotal = commissionDTOS.stream()
                .filter(i -> i.getSettlementStatus().equals(CommonConstant.COMMISSION_PENDING))
                .mapToDouble(i -> i.getActorCommissionAmt())
                .sum();


        Double adminCancelledTotal = commissionDTOS.stream()
                .filter(i -> i.getSettlementStatus().equals(CommonConstant.COMMISSION_CANCELLED))
                .mapToDouble(i -> i.getActorCommissionAmt())
                .sum();

        DecimalFormat df = new DecimalFormat("#.00");
//        public ProductCommissionReport(int totalPage, long itemCount, List<T> items,
//        int pageNumber, int pageSize,
//        String merchantSettledTotal,String merchantPendingTotal,
//                String merchantCancelledTotal,String adminSettledTotal,
//                String adminPendingTotal, String adminCancelledTotal)
        return new ProductCommissionReport<>(pagedResult.getTotalPages(),
                pagedResult.getTotalElements(), commissionDTOS, pagedInfo.page, pagedInfo.items,
                df.format(merchantSettledTotal), df.format(merchantPendingTotal),
                df.format(merchantCancelledTotal), df.format(adminSettledTotal),
                df.format(adminPendingTotal), df.format(adminCancelledTotal));
    }

    @Transactional
    @Override
    public void updateProductCommissionStatus(String settlementStatus, List<String> ids) throws Exception {

        List<ProductCommission> productCommissions = productCommissionRepository.findByIdIn(ids);
        for (ProductCommission productCommission : productCommissions) {
            OrderItem orderItem = productCommission.getOrderItem();
            if (!orderItem.getOrderItemStatus().equals(OrderStatus.DELIVERED.name())) {
                throw new ActorPayException("Can't settle the Order until Order Status is DELIVERED");
            }
            MerchantSettingsDTO merchantSettingsDTO = userFeignHelper
                    .getConfigByKey(CommonConstant.RETURN_DAYS, orderItem.getMerchantId());
            int configDays = Integer.parseInt(merchantSettingsDTO.getParamValue());
            int actualDays = CommonUtils.daysBetween(orderItem.getCreatedAt(), LocalDateTime.now());
            if (orderItem.getOrderItemStatus().equals(OrderStatus.DELIVERED.name())) {
                if (actualDays > configDays) {
                    productCommission.setOrderStatus(orderItem.getOrderItemStatus());
                    productCommission.setSettlementStatus(CommonConstant.COMMISSION_SETTLED);
                    productCommissionRepository.save(productCommission);
                } else {
                    throw new ActorPayException("Return period is still not completed. Can't change Product commission to settled");
                }
            }
        }


//        productCommissionRepository.updateProductCommissionStatus(settlementStatus,ids);
    }


    @Override
    public List<ProductCommissionDTO> findBySettlementStatus(String settlementStatus) {
        List<ProductCommissionDTO> productCommissionDTOList = new ArrayList<>();
        List<ProductCommission> productCommissionList = productCommissionRepository.findBySettlementStatus(settlementStatus);
        Optional.ofNullable(productCommissionList).ifPresent(commissionList -> {
            commissionList.forEach(productCommission -> {
                ProductCommissionDTO productCommissionDTO = new ProductCommissionDTO();
                BeanUtils.copyProperties(productCommission, productCommissionDTO);
                productCommissionDTOList.add(productCommissionDTO);
            });
        });
        return productCommissionDTOList;
    }

    @Override
    public Float getAdminTotalCommissionByOrderStatus(String orderStatus) {
        return productCommissionRepository.totalAdminCommissionByOrderStatus(orderStatus);
    }

    private void prepareProductCommissionSearchQuery(ProductCommissionFilterRequest filterRequest,
                                                     GenericSpecificationsBuilder<ProductCommission> builder) {
//
        builder.with(productCommissionSpecificationFactory.isEqual("deleted", false));
        try {
            if (StringUtils.isNotBlank(filterRequest.getMerchantId())) {
                builder.with(productCommissionSpecificationFactory.isEqual("merchantId", filterRequest.getMerchantId()));
            }
        } catch (FeignException fe) {
            System.out.println(String.format("Error while fetching Merchant Name %s", fe.getMessage()));
            builder.with(productCommissionSpecificationFactory.isEqual("merchantId", ""));
        }
        try {
            if (StringUtils.isNotBlank(filterRequest.getProductName())) {
                ResponseEntity<ApiResponse> responseEntity = merchantClient.findByName(filterRequest.getProductName());
                if (responseEntity.getStatusCode().is2xxSuccessful()) {
                    ApiResponse apiResponse = responseEntity.getBody();
                    String productId = (String) apiResponse.getData();
                    builder.with(productCommissionSpecificationFactory.isEqual("productId", productId));
                }
            }
        } catch (FeignException fe) {
            System.out.println(String.format("Error while fetching Product Name %s", fe.getMessage()));
            builder.with(productCommissionSpecificationFactory.isEqual("productId", ""));
        }

        if (StringUtils.isNotBlank(filterRequest.getOrderStatus())) {
            builder.with(productCommissionSpecificationFactory.like("orderStatus", filterRequest.getOrderStatus()));
        }
        if (StringUtils.isNotBlank(filterRequest.getOrderNo())) {
            builder.with(productCommissionSpecificationFactory.like("orderNo", filterRequest.getOrderNo()));
        }
        if (StringUtils.isNotBlank(filterRequest.getSettlementStatus())) {
            builder.with(productCommissionSpecificationFactory.like("settlementStatus", filterRequest.getSettlementStatus()));
        }
        if (filterRequest.getStartDate() != null) {
            builder.with(productCommissionSpecificationFactory.isGreaterThan("createdAt", filterRequest.getStartDate().atStartOfDay()));
        }
        if (filterRequest.getEndDate() != null) {
            builder.with(productCommissionSpecificationFactory.isLessThan("createdAt", filterRequest.getEndDate().plusDays(1).atStartOfDay()));
        }
        if (filterRequest.getStatus() != null) {
            builder.with(productCommissionSpecificationFactory.isEqual("isActive", filterRequest.getStatus()));
        }
        if (filterRequest.getMerchantEarnings() != null) {
            builder.with(productCommissionSpecificationFactory.isGreaterThanOrEquals("merchantEarnings",
                    filterRequest.getMerchantEarnings()));
        }
        /*if (filterRequest.getMerchantEarningsRangeFrom() != null) {
            builder.with(productCommissionSpecificationFactory
                    .isGreaterThanOrEquals("merchantEarningsRangeFrom", filterRequest.getMerchantEarningsRangeFrom()));
        }
        if (filterRequest.getMerchantEarningsRangeTo() != null) {
            builder.with(productCommissionSpecificationFactory
                    .isLessThanOrEquals("merchantEarningsRangeTo", filterRequest.getMerchantEarningsRangeTo()));
        }*/

        if (filterRequest.getActorCommissionAmtRangeFrom() != null) {
            builder.with(productCommissionSpecificationFactory
                    .isGreaterThanOrEquals("actorCommissionAmtRangeFrom", filterRequest.getActorCommissionAmtRangeFrom()));
        }
        if (filterRequest.getActorCommissionAmtRangeTo() != null) {
            builder.with(productCommissionSpecificationFactory
                    .isLessThanOrEquals("actorCommissionAmtRangeTo", filterRequest.getActorCommissionAmtRangeTo()));
        }
    }

    private String getProductName(String productId) {
        try {
            ResponseEntity<ApiResponse> responseEntity = userFeignHelper.getProductNameById(productId);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return (String) responseEntity.getBody().getData();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
        return "";
    }

    private String getMerchantName(String merchantId) {
        try {
            ResponseEntity<ApiResponse> responseEntity = userFeignHelper.getMerchantNameById(merchantId);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return (String) responseEntity.getBody().getData();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
        return "";
    }
}

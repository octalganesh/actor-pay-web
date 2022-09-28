package com.octal.actorPay.listners;

import com.google.gson.Gson;
import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.constants.OrderStatus;
import com.octal.actorPay.constants.StockStatus;
import com.octal.actorPay.dto.*;
import com.octal.actorPay.entities.*;
import com.octal.actorPay.feign.clients.AdminClient;
import com.octal.actorPay.feign.clients.CMSClient;
import com.octal.actorPay.feign.clients.NotificationClient;
import com.octal.actorPay.listners.events.OrderEvent;
import com.octal.actorPay.listners.events.OrderEventSource;
import com.octal.actorPay.listners.events.OrderStatusEvent;
import com.octal.actorPay.repositories.CancelOrderRepository;
import com.octal.actorPay.service.OrderService;
import com.octal.actorPay.service.ProductCommissionService;
import com.octal.actorPay.service.UserPGService;
import com.octal.actorPay.utils.PercentageCalculateManager;
import com.octal.actorPay.utils.UserFeignHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Component
public class OrderStatusListener implements ApplicationListener<OrderStatusEvent> {

    private OrderService orderService;

    private ProductCommissionService productCommissionService;

    private AdminClient adminClient;

    private CancelOrderRepository cancelOrderRepository;

    private PercentageCalculateManager percentageCalculateManager;

    private UserFeignHelper userFeignHelper;

    private UserPGService userPGService;

    @Autowired
    private NotificationClient notificationClient;

    @Autowired
    private CMSClient cmsClient;

    public OrderStatusListener(OrderService orderService, AdminClient adminClient,
                               ProductCommissionService productCommissionService,
                               CancelOrderRepository cancelOrderRepository,
                               PercentageCalculateManager percentageCalculateManager,
                               UserFeignHelper userFeignHelper, UserPGService userPGService) throws Exception {
        this.orderService = orderService;
        this.adminClient = adminClient;
        this.productCommissionService = productCommissionService;
        this.cancelOrderRepository = cancelOrderRepository;
        this.percentageCalculateManager = percentageCalculateManager;
        this.userFeignHelper = userFeignHelper;
        this.userPGService = userPGService;
    }

    @Override
    public void onApplicationEvent(OrderStatusEvent event) {
        final User user = event.getUser();
        try {
            FcmUserNotificationDTO.Request request = event.getFcmRequest();
            ApiResponse response = cmsClient.getNotificationContent(request.getNotificationType().toString());
            if (response.getStatus().equalsIgnoreCase(String.valueOf(101))) {
                //Content Not Found!!
                System.out.println("Content Not Found!!");
            } else {
                Gson gson = new Gson();
                String jsonResponse = gson.toJson(response.getData());
                NotificationContentDTO notificationContentDTO = gson.fromJson(jsonResponse, NotificationContentDTO.class);
                request.setTitle(notificationContentDTO.getTitle());
                request.setMessage(notificationContentDTO.getMessage());
            }

            FcmUserNotificationDTO fcmUser = new FcmUserNotificationDTO();
            fcmUser.setId(user.getId());
            if (user.getUserDeviceDetails() != null) {
                fcmUser.setFcmDeviceToken(user.getUserDeviceDetails().getDeviceToken());
                fcmUser.setDeviceType(user.getUserDeviceDetails().getDeviceType());
                request.setSystemUser(fcmUser);
//                request.setNotificationTypeId(user.getId());
                request.setImageUrl("");
                notificationClient.sendPushNotification(request);
                System.out.println("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//    private void processRefund(Double refundAmount, OrderDetails orderDetails) throws Exception {
//        ApiResponse apiResponse = userPGService.getPGPaymentDetails(orderDetails.getId());
//        ObjectMapper mapper = new ObjectMapper();
//        PgDetailsDTO pgDetailsDTO = mapper.convertValue(apiResponse.getData(), PgDetailsDTO.class);
//        RefundRequest refundRequest = new RefundRequest();
//        refundRequest.setRefundAmount(refundAmount);
//        refundRequest.setPaymentId(pgDetailsDTO.getPaymentId());
//        ApiResponse refundResponse = userPGService.refund(refundRequest);
//        mapper = new ObjectMapper();
//        RefundResponse refundDetails = mapper.convertValue(refundResponse.getData(), RefundResponse.class);
//        System.out.println("####### Refund Response Amount  " + refundDetails.getAmount());
//        System.out.println("####### Refund Response Status " + refundDetails.getStatus());
//    }

    private List<ProductCommission> saveOrderCommission(OrderDetails orderDetails) throws Exception {
        List<ProductCommission> productCommissions = new ArrayList<>();
        String orderNo = orderDetails.getOrderNo();
        String merchantId = orderDetails.getMerchantId();
        List<OrderItem> orderItems = orderDetails.getOrderItems();
        for (OrderItem orderItem : orderItems) {
            PercentageCharges percentageCharges = percentageCalculateManager
                    .calculatePercentage(orderItem, CommonConstant.ADMIN_COMMISSION);
            ProductCommission productCommission = new ProductCommission();
            productCommission.setActorCommissionAmt(percentageCharges.getPercentageCharges());
            productCommission.setCommissionPercentage(percentageCharges.getPercentage());
            productCommission.setMerchantEarnings(percentageCharges.getBalanceAmount());
            productCommission.setCommissionPercentage(percentageCharges.getPercentage());
            productCommission.setProductId(orderItem.getProductId());
            productCommission.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
            productCommission.setOrderNo(orderNo);
            productCommission.setOrderItem(orderItem);
            productCommission.setMerchantId(merchantId);
            productCommission.setQuantity(orderItem.getProductQty());
            productCommission.setSettlementStatus(CommonConstant.COMMISSION_PENDING);
            productCommission.setOrderStatus(orderItem.getOrderItemStatus());
            productCommission.setActive(Boolean.TRUE);
            productCommissions.add(productCommission);
        }
        return productCommissions;
    }

    public void doCancel(List<OrderItem> cancellingItems, CancelOrderDTO cancelOrderDTO, String orderNo) throws Exception {
        double totalRefundAmount = 0d;
        double totalOriginalAmount = 0d;
        double totalCharges = 0d;
        OrderDetails orderDetails = orderService.findOrderByNo(orderNo);
        List<CancelOrderItem> cancelOrderItems = new ArrayList<>();
        String cancellationType = cancelOrderDTO.getCancellationRequest();

        for (OrderItem orderItem : cancellingItems) {
            orderItem.setActive(false);
            CancelOrderItem cancelOrderItem = new CancelOrderItem();
            cancelOrderItem.setOrderItemId(orderItem.getId());
            cancelOrderItem.setActive(Boolean.TRUE);
            cancelOrderItem.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
            cancelOrderItem.setRefundAmount(orderItem.getTotalPrice());
            PercentageCharges percentageCharges = percentageCalculateManager
                    .calculatePercentage(orderItem, cancellationType);
            double charges = percentageCharges.getPercentageCharges();
            totalCharges = totalCharges + charges;
            cancelOrderItem.setChargeAmount(charges);
            double refundAmount = percentageCharges.getBalanceAmount();
            cancelOrderItem.setRefundAmount(refundAmount);
            totalRefundAmount = totalRefundAmount + refundAmount;
            double originalAmount = percentageCharges.getOriginalAmount();
            totalOriginalAmount = totalOriginalAmount + originalAmount;
            cancelOrderItem.setOriginalAmount(originalAmount);
            cancelOrderItem.setCancelReason(cancelOrderDTO.getCancelReason());
            cancelOrderItems.add(cancelOrderItem);
        }
        CancelOrder cancelOrder = cancelOrderRepository.findCancelOrdersByOrderId(orderDetails);
        if (cancelOrder == null) {
            cancelOrder = new CancelOrder();
            cancelOrder.setOrderDetails(orderDetails);
        }
        System.out.println("### Total Refund Amount " + totalRefundAmount);
        System.out.println("#### Total  Original Amount " + totalOriginalAmount);
        cancelOrder.setOrderDetails(cancelOrder.getOrderDetails());
        cancelOrder.setCharges(totalCharges);
        cancelOrder.setOriginalAmount(totalOriginalAmount);
        cancelOrder.setRefundAmount(totalRefundAmount);
        cancelOrder.setOrderDetails(orderDetails);
        cancelOrder.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        cancelOrder.setImage(cancelOrderDTO.getImage());
        cancelOrder.setCancelReason(cancelOrderDTO.getCancelReason());
        final CancelOrder cancelOrder1 = cancelOrder;
        cancelOrderItems.stream().forEach(c -> c.setCancelOrder(cancelOrder1));
        cancelOrder.setCancelOrderItems(cancelOrderItems);
        cancelOrderRepository.save(cancelOrder);
    }

    private void reverseStock(String productId, Integer qty) throws Exception {
        ProductDTO productDTO = userFeignHelper.getProductById(productId);
        if (productDTO != null) {
            productDTO.setStockCount(productDTO.getStockCount() + qty);
            productDTO.setStockStatus(StockStatus.IN_STOCK.name());
            ResponseEntity<ApiResponse> responseEntity =
                    userFeignHelper.updateProductStock(productId, productDTO.getStockCount(), productDTO.getStockStatus());
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                System.out.println("#### PRODUCT STOCK UPDATED SUCCESSFULLY ####");
            }
        }
    }

    private void manageProductStock(List<OrderItem> orderItems, String eventType) throws Exception {
        if (CommonConstant.ORDER_EVENT_PLACE_ORDER.equalsIgnoreCase(eventType)) {
            if (orderItems != null && orderItems.size() > 0) {
                for (OrderItem orderItem : orderItems) {
                    updateStock(orderItem.getProductId(), orderItem.getProductQty());
                }
            }
        }
        if (CommonConstant.ORDER_EVENT_CANCEL_ORDER.equalsIgnoreCase(eventType)) {
            if (orderItems != null && orderItems.size() > 0) {
                for (OrderItem orderItem : orderItems) {
                    reverseStock(orderItem.getProductId(), orderItem.getProductQty());
                }
            }
        }
    }

    public void updateStock(String productId, Integer qty) throws Exception {
        Integer stockCount = 0;
        String stockStatus = "";
        ProductDTO productDTO = userFeignHelper.getProductById(productId);
        if (productDTO != null) {
            if (productDTO.getStockCount() > 0) {
                productDTO.setStockCount(productDTO.getStockCount() - qty);
                stockCount = productDTO.getStockCount();
            }
            if (productDTO.getStockCount() < 0) {
                stockStatus = StockStatus.OUT_OF_STOCK.name();
            } else {
                stockStatus = StockStatus.IN_STOCK.name();
            }
            ResponseEntity<ApiResponse> responseEntity =
                    userFeignHelper.updateProductStock(productId, stockCount, stockStatus);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                System.out.println("#### PRODUCT STOCK UPDATED SUCCESSFULLY ####");
            }
        }
    }
}

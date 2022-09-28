package com.octal.actorPay.service;

import com.octal.actorPay.dto.*;
import com.octal.actorPay.dto.payments.OrderRequest;
import com.octal.actorPay.dto.payments.OrderResponse;
import com.octal.actorPay.dto.request.OrderFilterRequest;
import com.octal.actorPay.dto.request.OrderNoteFilterRequest;
import com.octal.actorPay.entities.CancelOrder;
import com.octal.actorPay.entities.OrderDetails;
import com.octal.actorPay.entities.OrderItem;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.exceptions.ObjectNotFoundException;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderService {

    OrderDetailsDTO placeOrder(CartDTO cartDTO, ShippingAddressDTO shippingAddressDTO, User user) throws Exception;

    PageItem<OrderDetailsDTO> getAllOrders(PagedItemInfo pagedInfo, User user, OrderFilterRequest filterRequest);

    PageItem<OrderDetailsDTO> getAllOrders(PagedItemInfo pagedInfo, OrderFilterRequest filterRequest);

    OrderDetailsDTO getOrderByOrderNo(String id,String userName,String userType, String orderNo) throws ObjectNotFoundException;

    PageItem<OrderDetailsDTO> getAllOrdersByOrderStatus(PagedItemInfo pagedInfo, User user, String status);

    OrderDetails findOrderByNo(String orderNo) ;

    CancelOrderDTO getCancelledOrderDetails(OrderDetails orderDetails);

    List<OrderNoteDTO> findByOrderId(String orderId);

    String getOrderStatusById(@Param("orderId") String id);

    List<OrderItem> doUpdateOrderStatus(OrderDetails orderDetails, String status,
                             OrderNoteDTO orderNoteDTO,String userType,List<String> orderItemIds,String id) throws Exception;
    OrderDetails findByOrderNoAndMerchantIdAndDeletedFalse(String orderNo,String merchantId);
    OrderDetails findByOrderNoAndCustomer(String oderNo,User customer);
    void setGlobalOrderStatus(String orderNo);

    OrderNoteDTO saveOrderNote(OrderNoteDTO orderNoteDTO, OrderDetails orderDetails, String
            userType, String userName,Boolean sendNotification);
    List<OrderNoteDTO> getAllOrderNotes(String userType,
                                        OrderNoteFilterRequest filterRequest);

    OrderDetails findByOrderNo(String orderNo,String userType,String id);

    OrderItemDTO findOrderItemById(String id);

    ApiResponse checkout(OrderRequest orderRequest);

    void processRefund(Double refundAmount, OrderDetails orderDetails) throws Exception;

    void saveOrderItem(OrderItem orderItem);

    CancelOrder findCancelOrdersByOrderId(OrderDetails orderDetails);

    ApplyReferralDTO applyReferral(String orderNo, ReferralSettingDTO referralSettingDTO);

    List<OrderDetailsDTO> getAllOrdersForReport(OrderFilterRequest filterRequest);
}

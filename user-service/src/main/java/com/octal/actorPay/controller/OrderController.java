package com.octal.actorPay.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.constants.OrderStatus;
import com.octal.actorPay.dto.*;
import com.octal.actorPay.dto.enums.NotificationTypeEnum;
import com.octal.actorPay.dto.payments.OrderRequest;
import com.octal.actorPay.dto.request.OrderFilterRequest;
import com.octal.actorPay.entities.OrderDetails;
import com.octal.actorPay.entities.OrderItem;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.entities.UserDeviceDetails;
import com.octal.actorPay.exceptions.ObjectNotFoundException;
import com.octal.actorPay.feign.clients.MerchantClient;
import com.octal.actorPay.listners.events.OrderEvent;
import com.octal.actorPay.listners.events.OrderEventSource;
import com.octal.actorPay.listners.events.OrderStatusEvent;
import com.octal.actorPay.repositories.UserRepository;
import com.octal.actorPay.service.CartItemService;
import com.octal.actorPay.service.OrderService;
import com.octal.actorPay.service.UploadService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.management.Notification;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController extends PagedItemsController {

    private OrderService orderService;

    @Autowired
    private MerchantClient merchantClient;

    private CartItemService cartItemService;

    private UserRepository userRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private UploadService uploadService;

    public OrderController(OrderService orderService, CartItemService cartItemService,
                           UserRepository userRepository) {
        this.orderService = orderService;
        this.cartItemService = cartItemService;
        this.userRepository = userRepository;
    }

    @PostMapping
    synchronized ResponseEntity<ApiResponse> placeOrder(@RequestBody @Valid ShippingAddressDTO shippingAddressDTO,
                                                        HttpServletRequest request) throws ObjectNotFoundException, Exception {
        String userName = request.getHeader("userName");
        if (userName != null && userName.length() > 0) {
            User user = userRepository.findByEmailAndIsActiveTrueAndDeletedFalse(userName).orElse(null);
            CartDTO cartDTO = cartItemService.getActiveCartItemByUser(user);
            if (cartDTO == null || cartDTO.getCartItemDTOList().isEmpty()) {
                throw new ObjectNotFoundException("Your Cart is empty");
            }
            if (StringUtils.isBlank(shippingAddressDTO.getPaymentMethod())) {
                throw new RuntimeException("paymentMethod: must not be blank");
            }
            OrderDetailsDTO orderDetailsDTO = orderService.placeOrder(cartDTO, shippingAddressDTO, user);
            return new ResponseEntity<>(new ApiResponse(String.format("Your Order is successfully placed Order No: %s ", orderDetailsDTO.getOrderNo()), orderDetailsDTO,
                    String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse("Your Order is not able to place please contact customer support ", null,
                    String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    private List<String> validate(ShippingAddressDTO shippingAddressDTO) {

        List<String> errorList = new ArrayList<>();
        String addressLine1 = shippingAddressDTO.getAddressLine1();
        String zipCode = shippingAddressDTO.getZipCode();
        String city = shippingAddressDTO.getCity();
        String state = shippingAddressDTO.getState();
        String country = shippingAddressDTO.getCountry();
        String primaryContactNumber = shippingAddressDTO.getPrimaryContactNumber();
        if (addressLine1 == null || addressLine1.trim().length() == 0)
            errorList.add("addressLine1 is required");
        if (zipCode == null || zipCode.trim().length() == 0)
            errorList.add("zipCode is required");
        if (city == null || zipCode.trim().length() == 0)
            errorList.add("city is required");
        if (state == null || state.trim().length() == 0)
            errorList.add("state is required");
        if (country == null || country.trim().length() == 0) {
            errorList.add("country is required");
        }
        if (primaryContactNumber == null || primaryContactNumber.length() == 0) {
            errorList.add("primaryContactNumber is required");
        }
        return errorList;
    }

    @PostMapping("/list/paged")
    ResponseEntity<ApiResponse> viewAllOrder(@RequestParam(defaultValue = "0") Integer pageNo,
                                             @RequestParam(defaultValue = "10") Integer pageSize,
                                             @RequestParam(defaultValue = "createdAt") String sortBy,
                                             @RequestParam(defaultValue = "false") boolean asc,
                                             @RequestBody OrderFilterRequest filterRequest, HttpServletRequest request)
            throws Exception {
        String userName = request.getHeader("userName");
        try {
            PageItem<OrderDetailsDTO> orderDetailsDTOList = null;
            final PagedItemInfo pagedInfo = createPagedInfo(pageNo, pageSize, sortBy, asc, null);
            if (StringUtils.isNotBlank(filterRequest.getUserId())) {
                orderDetailsDTOList = orderService.getAllOrders(pagedInfo, filterRequest);
            } else {
                if ((userName != null && userName.length() > 0)) {
                    User user = userRepository.findByEmailAndIsActiveTrueAndDeletedFalse(userName).orElse(null);
                    if (user != null) {
                        filterRequest.setCustomerEmail(user.getEmail());
                        orderDetailsDTOList = orderService.getAllOrders(pagedInfo, user, filterRequest);
                    }
                }
            }
            if (orderDetailsDTOList == null || orderDetailsDTOList.items.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse("No orders found", orderDetailsDTOList,
                        String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ApiResponse("Order Details", orderDetailsDTOList,
                        String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
            }
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
            if (e != null) {
                if (e.getMessage().contains("Unknown field to sort")) {
                    PageItem<OrderDetailsDTO> orderDetailsDTOList = new
                            PageItem<OrderDetailsDTO>(0, 0l, new ArrayList<>(), 0, 0);
                    return new ResponseEntity<>(new ApiResponse("Order Details", orderDetailsDTOList,
                            String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
                }
            }
            e.printStackTrace();
        }
        return null;
    }

    @PostMapping("/list/report")
    ResponseEntity<ApiResponse> getAllOrdersForReport(@RequestBody OrderFilterRequest filterRequest) throws Exception {
        try {
            List<OrderDetailsDTO> orderDetailsDTOList = orderService.getAllOrdersForReport(filterRequest);
            if (orderDetailsDTOList == null) {
                return new ResponseEntity<>(new ApiResponse("No orders found", orderDetailsDTOList,
                        String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ApiResponse("Order Details", orderDetailsDTOList,
                        String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
            }
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("/orderstatus/{status}")
    ResponseEntity<ApiResponse> viewAllOrderByOrderStatus(@PathVariable("status") String status,
                                                          @RequestParam(defaultValue = "0") Integer pageNo,
                                                          @RequestParam(defaultValue = "10") Integer pageSize,
                                                          @RequestParam(defaultValue = "createdAt") String sortBy,
                                                          @RequestParam(defaultValue = "true") boolean asc, HttpServletRequest request) {
        String userName = request.getHeader("userName");
        final PagedItemInfo pagedInfo = createPagedInfo(pageNo, pageSize, sortBy, asc, null);
        if (userName != null && userName.length() > 0) {
            User user = userRepository.findByEmailAndIsActiveTrueAndDeletedFalse(userName).orElse(null);
            PageItem<OrderDetailsDTO> orderDetailsDTOList = orderService.getAllOrdersByOrderStatus(pagedInfo, user, status);
            if (orderDetailsDTOList == null || orderDetailsDTOList.items.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse("No orders found", orderDetailsDTOList,
                        String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ApiResponse("Order Details", orderDetailsDTOList,
                        String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
            }
        }
        return null;
    }

    @GetMapping("/{orderNo}")
    ResponseEntity<ApiResponse> viewOrder(@PathVariable("orderNo") String orderNo,
                                          @RequestParam(name = "userType", required = false) String userType,
                                          @RequestParam(name = "id", required = false) String id,
                                          HttpServletRequest request) throws ObjectNotFoundException {
        String userName = request.getHeader("userName");
        OrderDetailsDTO orderDetailsDTO = orderService.getOrderByOrderNo(id, userName, userType, orderNo);
        if (orderDetailsDTO != null) {
            return new ResponseEntity<>(new ApiResponse(String.format("Your Order Detail for Order No %s ", orderNo), orderDetailsDTO,
                    String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse(String.format("Order is not found for order no %s contact customer support ", orderNo), null,
                    String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/applyReferral/{orderNo}")
    ApiResponse applyReferral( @PathVariable("orderNo") String orderNo,
                               @RequestBody ReferralSettingDTO referralSettingDTO) throws ObjectNotFoundException {
        ApplyReferralDTO orderDetailsDTO = orderService.applyReferral(orderNo, referralSettingDTO);
        return new ApiResponse(String.format("Apply Referral Detail for Order No %s ", orderNo), orderDetailsDTO,
                    String.valueOf(HttpStatus.OK.value()), HttpStatus.OK);
    }

    @PostMapping("/cancel/{orderNo}")
    ResponseEntity<ApiResponse> cancelOrder(@PathVariable("orderNo") String orderNo,
                                            @Valid @RequestPart(name = "cancelOrder") String cancelOrder,
                                            @RequestPart(name = "file", required = false) MultipartFile file,
                                            HttpServletRequest request, @RequestParam(value = "userType", required = false) String userType,
                                            @RequestParam(value = "id", required = false) String id)
            throws Exception {
        String message = "";
        List<OrderItem> cancelledItems = new ArrayList<>();
        String userName = request.getHeader("userName");
        User user = userRepository.findByEmailAndIsActiveTrueAndDeletedFalse(userName).orElse(null);
        ObjectMapper objectMapper = new ObjectMapper();
        CancelOrderDTO cancelOrderDTO = objectMapper.readValue(cancelOrder, CancelOrderDTO.class);
        OrderNoteDTO orderNoteDTO = new OrderNoteDTO();
        orderNoteDTO.setCancellationReason(cancelOrderDTO.getCancelReason());
        orderNoteDTO.setOrderNoteDescription(cancelOrderDTO.getCancelReason());
        orderNoteDTO.setImage(orderNoteDTO.getImage());
        if (cancelOrderDTO.getCancellationRequest().equalsIgnoreCase(OrderStatus.RETURNING.name())) {
            message = "Order Return initiated Successfully";
        }
        if (cancelOrderDTO.getCancellationRequest().equalsIgnoreCase(OrderStatus.CANCELLED.name())) {
            message = "Order Cancelled Successfully";
        }
        if (cancelOrderDTO.getCancellationRequest().equalsIgnoreCase(OrderStatus.RETURNING_ACCEPTED.name())) {
            message = "Order Return Accepted Successfully";
        }
        if (cancelOrderDTO.getCancellationRequest().equalsIgnoreCase(OrderStatus.RETURNING_DECLINED.name())) {
            message = "Order Return Declined Successfully";
        }
        if (StringUtils.isBlank(id) && StringUtils.isBlank(userType)) {
            id = user.getId();
            userType = CommonConstant.USER_TYPE_CUSTOMER;
        }

        OrderDetails orderDetails = orderService.findByOrderNo(orderNo, userType, id);
        cancelledItems = orderService.doUpdateOrderStatus(orderDetails, cancelOrderDTO.getCancellationRequest(), orderNoteDTO,
                userType, cancelOrderDTO.getOrderItemIds(), id);
        OrderEventSource orderEventSource = new OrderEventSource(cancelOrderDTO, cancelledItems, orderNo,
                CommonConstant.ORDER_EVENT_CANCEL_ORDER);
        FcmUserNotificationDTO.Request fcmRequest = new FcmUserNotificationDTO.Request();
        if(cancelOrderDTO.getCancellationRequest().equalsIgnoreCase(OrderStatus.RETURNING.toString())){
            fcmRequest.setNotificationType(NotificationTypeEnum.RETURN_ORDER);
            try {
                ResponseEntity<ApiResponse> merchantDetails = merchantClient.getMerchantByMerchantId(orderDetails.getMerchantId());
                if (merchantDetails.getBody().getData() != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    MerchantDTO merchantData = mapper.convertValue(merchantDetails.getBody().getData(), MerchantDTO.class);
                    FcmUserNotificationDTO.Request merchantFcmRequest = new FcmUserNotificationDTO.Request();
                    merchantFcmRequest.setNotificationType(NotificationTypeEnum.RETURN_REQUEST);
                    merchantFcmRequest.setNotificationTypeId(orderDetails.getOrderNo());
                    User merchantUser = new User();
                    merchantUser.setId(merchantData.getId());
                    UserDeviceDetails merchantDeviceDetails = new UserDeviceDetails();
                    merchantDeviceDetails.setDeviceToken(merchantData.getDeviceToken());
                    merchantDeviceDetails.setDeviceType(merchantData.getDeviceType());
                    merchantUser.setUserDeviceDetails(merchantDeviceDetails);
                    eventPublisher.publishEvent(new OrderStatusEvent(merchantFcmRequest, merchantUser));
                }
            }catch (Exception e){
                System.out.println("Merchant Notification not sent.");
            }
        }else{
            fcmRequest.setNotificationType(NotificationTypeEnum.CANCEL_ORDER);
        }
        fcmRequest.setNotificationTypeId(orderNo);
        eventPublisher.publishEvent(new OrderEvent(orderEventSource,fcmRequest,user));
        return new ResponseEntity<>(new ApiResponse(String.format("%s %s", message, orderNo), "",
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);

    }


    @PutMapping("/status")
    ResponseEntity<ApiResponse> updateOrderStatus(@RequestParam("status") String status,
                                                  @RequestParam("orderNo") String orderNo, HttpServletRequest request,
                                                  @RequestParam(value = "userType", required = false) String userType,
                                                  @RequestParam(value = "id", required = false) String id,
                                                  @RequestBody(required = false) OrderNoteDTO orderNoteDTO)
            throws ObjectNotFoundException, Exception {
        String userName = request.getHeader("userName");
        User user = userRepository.findByEmailAndIsActiveTrueAndDeletedFalse(userName).orElse(null);
        if (StringUtils.isBlank(id) && StringUtils.isBlank(userType)) {
            id = user.getId();
            userType = CommonConstant.USER_TYPE_CUSTOMER;
        }
        if (orderNoteDTO == null) {
            orderNoteDTO = new OrderNoteDTO();
            orderNoteDTO.setOrderItemIds(new ArrayList<>());
        }
        OrderDetails orderDetails = orderService.findByOrderNo(orderNo, userType, id);
        orderService.doUpdateOrderStatus(orderDetails, status, orderNoteDTO,
                userType, orderNoteDTO.getOrderItemIds(), id);

//

//

        return new ResponseEntity<>(new ApiResponse(String.format("Your status updated successfully"), "",
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping("/checkout")
    ResponseEntity<ApiResponse> checkout(@RequestBody OrderRequest orderRequest) {
        ApiResponse apiResponse = orderService.checkout(orderRequest);
        return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);
    }
}

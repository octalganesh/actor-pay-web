package com.octal.actorPay.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.constants.OrderStatus;
import com.octal.actorPay.constants.PGConstant;
import com.octal.actorPay.constants.PaymentMethod;
import com.octal.actorPay.constants.PurchaseType;
import com.octal.actorPay.dto.*;
import com.octal.actorPay.dto.enums.NotificationTypeEnum;
import com.octal.actorPay.dto.payments.OrderRequest;
import com.octal.actorPay.dto.payments.PaymentGatewayResponse;
import com.octal.actorPay.dto.payments.RefundRequest;
import com.octal.actorPay.dto.payments.RefundResponse;
import com.octal.actorPay.dto.payments.WalletRequest;
import com.octal.actorPay.dto.request.LoyaltyRewardsRequest;
import com.octal.actorPay.dto.request.OrderFilterRequest;
import com.octal.actorPay.dto.request.OrderNoteFilterRequest;
import com.octal.actorPay.dto.request.PromoCodeFilter;
import com.octal.actorPay.entities.*;
import com.octal.actorPay.exceptions.ActorPayException;
import com.octal.actorPay.exceptions.ObjectNotFoundException;
import com.octal.actorPay.external.SmsService;
import com.octal.actorPay.feign.clients.AdminClient;
import com.octal.actorPay.feign.clients.MerchantClient;
import com.octal.actorPay.feign.clients.NotificationClient;
import com.octal.actorPay.listners.events.OrderEvent;
import com.octal.actorPay.listners.events.OrderEventSource;
import com.octal.actorPay.listners.events.OrderStatusEvent;
import com.octal.actorPay.repositories.CancelOrderItemRepository;
import com.octal.actorPay.repositories.CancelOrderRepository;
import com.octal.actorPay.repositories.CartItemRepository;
import com.octal.actorPay.repositories.OrderAddressRepository;
import com.octal.actorPay.repositories.OrderDetailsRepository;
import com.octal.actorPay.repositories.OrderItemCommissionRepository;
import com.octal.actorPay.repositories.OrderItemRepository;
import com.octal.actorPay.repositories.OrderNoteRepository;
import com.octal.actorPay.repositories.OrderStatusValidationRepository;
import com.octal.actorPay.repositories.ProductCommissionRepository;
import com.octal.actorPay.repositories.ReferralHistoryRepository;
import com.octal.actorPay.repositories.ShippingAddressRepository;
import com.octal.actorPay.repositories.UserRepository;
import com.octal.actorPay.service.*;
import com.octal.actorPay.specification.GenericSpecificationsBuilder;
import com.octal.actorPay.specification.SpecificationFactory;
import com.octal.actorPay.transformer.OrderDetailsTransformer;
import com.octal.actorPay.transformer.OrderItemTransformer;
import com.octal.actorPay.transformer.PagedItemsTransformer;
import com.octal.actorPay.utils.PGUtils;
import com.octal.actorPay.utils.PercentageCalculateManager;
import com.octal.actorPay.utils.UserFeignHelper;
import com.octal.actorPay.utils.UserServiceCodeGenerator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.octal.actorPay.transformer.OrderDetailsTransformer.*;
import static com.octal.actorPay.transformer.OrderItemTransformer.*;

@Service
public class OrderServiceImpl implements OrderService {

//    @PersistenceContext
//    private EntityManager entityManager;

    private final OrderDetailsRepository orderDetailsRepository;

    private final OrderItemRepository orderItemRepository;

    private final UserRepository userRepository;

    private final UserServiceCodeGenerator codeGenerator;

    private final SmsService smsService;

    private final CartItemRepository cartItemRepository;

    private final MerchantClient merchantClient;

    private final CancelOrderRepository cancelOrderRepository;

    private final UploadService uploadService;

    private final AdminClient adminClient;

    private final NotificationClient notificationClient;

    private final ShippingAddressRepository shippingAddressRepository;

    private final SpecificationFactory<OrderDetails> orderSpecificationFactory;

    private final SpecificationFactory<OrderNote> orderNoteSpecificationFactory;

    private final OrderNoteRepository orderNoteRepository;

    private final UserFeignHelper userFeignHelper;

    private final OrderStatusValidationRepository orderStatusValidationRepository;

    private final OrderAddressRepository orderAddressRepository;

    private final UserWalletService userWalletService;

    private final PGUtils pgUtils;

    private final UserPGService userPGService;

    @Autowired
    private OrderItemCommissionRepository orderItemCommissionRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private ProductCommissionRepository productCommissionRepository;

    @Autowired
    private PercentageCalculateManager percentageCalculateManager;

    @Autowired
    private CancelOrderItemRepository cancelOrderItemRepository;

    @Autowired
    private ReferralHistoryRepository referralHistoryRepository;

    @Autowired
    private CartItemServiceImpl cartItemService;

    public OrderServiceImpl(OrderDetailsRepository orderDetailsRepository,
                            OrderItemRepository orderItemRepository,
                            UserRepository userRepository, UserServiceCodeGenerator codeGenerator,
                            SmsService smsService, CartItemRepository cartItemRepository,
                            MerchantClient merchantClient, CancelOrderRepository cancelOrderRepository,
                            UploadService uploadService, AdminClient adminClient,
                            NotificationClient notificationClient, ShippingAddressRepository shippingAddressRepository,
                            SpecificationFactory<OrderDetails> orderSpecificationFactory,
                            OrderNoteRepository orderNoteRepository,
                            OrderStatusValidationRepository orderStatusValidationRepository,
                            SpecificationFactory<OrderNote> orderNoteSpecificationFactory,
                            UserFeignHelper userFeignHelper,
                            OrderAddressRepository orderAddressRepository,
                            UserWalletService userWalletService, PGUtils pgUtils, UserPGService userPGService) {
        this.orderItemRepository = orderItemRepository;
        this.orderDetailsRepository = orderDetailsRepository;
        this.userRepository = userRepository;
        this.codeGenerator = codeGenerator;
        this.smsService = smsService;
        this.cartItemRepository = cartItemRepository;
        this.merchantClient = merchantClient;
        this.cancelOrderRepository = cancelOrderRepository;
        this.uploadService = uploadService;
        this.adminClient = adminClient;
        this.notificationClient = notificationClient;
        this.shippingAddressRepository = shippingAddressRepository;
        this.orderSpecificationFactory = orderSpecificationFactory;
        this.orderNoteRepository = orderNoteRepository;
        this.orderStatusValidationRepository = orderStatusValidationRepository;
        this.orderNoteSpecificationFactory = orderNoteSpecificationFactory;
        this.userFeignHelper = userFeignHelper;
        this.orderAddressRepository = orderAddressRepository;
        this.userWalletService = userWalletService;
        this.pgUtils = pgUtils;
        this.userPGService = userPGService;
    }

    @Transactional
    @Override
    public OrderDetailsDTO placeOrder(CartDTO cartDTO, ShippingAddressDTO shippingAddressDTO, User user) throws Exception {
        String paymentMethod = shippingAddressDTO.getPaymentMethod();
        String orderNoteDescription = shippingAddressDTO.getOrderNote();
        List<OrderNote> orderNoteList = new ArrayList<>();
        List<OrderItem> orderItems = new ArrayList<>();
        List<OrderItemDTO> orderItemDtos = new ArrayList<>();
        List<String> cartItemIds = new ArrayList<>();
        List<MerchantSettingsDTO> merchantSettingsDTOS = null;
        ShippingAddress shipAddr = null;
        List<ApplyPromoCodeResponse> applyPromoCodeResponseList = new ArrayList<>();
        OrderDetails orderDetails = CART_DTO_TO_ORDER_DETAILS.apply(cartDTO);
        OrderAddress orderAddress = SHIPPING_DTO_TO_ORDER_ADDR_ENTITY.apply(shippingAddressDTO);
        orderAddress.setUser(user);
        orderAddress.setActive(true);
        OrderAddress ordAddr = orderAddressRepository.save(orderAddress);
        try {
            merchantSettingsDTOS =
                    userFeignHelper.getMerchantSettings(CommonConstant.merchantSettings, cartDTO.getMerchantId());
        } catch (Exception e) {
            if (merchantSettingsDTOS == null) {
                List<SystemConfigurationDTO> systemConfigurations = userFeignHelper.getGlobalSettingsDefaultMerchantSettings();
                merchantSettingsDTOS = userFeignHelper.addMerchantSettings(systemConfigurations, orderDetails.getMerchantId());
            }
        }

        orderDetails.setOrderAddress(ordAddr);
        orderDetails.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        orderDetails.setOrderStatus(OrderStatus.SUCCESS.name());
        orderDetails.setCustomer(user);
        orderDetails.setPaymentMethod(shippingAddressDTO.getPaymentMethod().toUpperCase());
        String orderNo = codeGenerator.getNewCode();
        orderDetails.setOrderNo(orderNo);
        orderDetails.setActive(true);
        List<CartItemDTO> cartItemDTOList = cartDTO.getCartItemDTOList();
        for (CartItemDTO cartItemDTO : cartItemDTOList) {
            OrderItemCommission orderItemCommission = getOrderCommission(merchantSettingsDTOS, cartDTO.getMerchantId());
            OrderItem orderItem = CART_ITEM_DTO_TO_ORDER_ITEM.apply(cartItemDTO);
            orderItem.setOrderDetails(orderDetails);
            ResponseEntity<ApiResponse> apiResponseResponseEntity = merchantClient.getProductById(orderItem.getProductId());
            ApiResponse productBodyData = apiResponseResponseEntity.getBody();
            if (productBodyData.getData() != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                ProductDTO productDTO = objectMapper.convertValue(productBodyData.getData(), ProductDTO.class);
                orderItem.setCategoryId(productDTO.getCategoryId());
                orderItem.setSubcategoryId(productDTO.getSubCategoryId());
                orderItem.setActualPrice(productDTO.getActualPrice());
                orderItem.setOrderItemStatus(orderDetails.getOrderStatus());
//                orderItem.setCategoryName(productDTO.getCategoryName());
                orderItem.setSubcategoryId(productDTO.getSubCategoryId());
                orderItem.setOrderItemCommission(orderItemCommission);
                orderItemCommission.setOrderItem(orderItem);
            }

            orderDetails.setOrderItems(orderItems);
            orderItems.add(orderItem);
            cartItemIds.add(cartItemDTO.getCartItemId());
//            if (Objects.nonNull(cartItemDTO.getPromoCodeResponse())) {
//                applyPromoCodeResponseList.add(cartItemDTO.getPromoCodeResponse());
//            }
        }
        checkProductAvailability(orderDetails.getOrderItems());
        //Apply Promo Code
        if (shippingAddressDTO.getPromoCode() != null && shippingAddressDTO.getPromoCode().length() != 0) {
            PromoCodeFilter promoCodeRequest = new PromoCodeFilter();
            promoCodeRequest.setPromoCode(shippingAddressDTO.getPromoCode());
            promoCodeRequest.setAmount((float) orderDetails.getTotalPrice());
            promoCodeRequest.setUserId(orderDetails.getCustomer().getId());
            ResponseEntity<ApiResponse> applyPromoResponse = adminClient.applyPromoCode(promoCodeRequest);
            Gson gson = new Gson();
            String applyPromoResponseJson = gson.toJson(applyPromoResponse.getBody().getData());
            ApplyPromoCodeResponse promoCodeResponse = gson.fromJson(applyPromoResponseJson, ApplyPromoCodeResponse.class);
            orderDetails.setTotalPriceAfterPromo(promoCodeResponse.getAmountAfterDiscount());
            promoCodeResponse.setUserId(user.getId());
            applyPromoCodeResponseList.add(promoCodeResponse);
        }
        OrderDetails newOrderDetails = orderDetailsRepository.save(orderDetails);
        // PAYMENT TRANSFER USING WALLET
        if (StringUtils.isNotBlank(paymentMethod)) {
            if (paymentMethod.equalsIgnoreCase(PaymentMethod.wallet.name())) {
                SystemConfigurationDTO adminEmail = userFeignHelper.getGlobalConfigByKey(CommonConstant.ADMIN_EMAIL);
                WalletRequest walletRequest = new WalletRequest();
                walletRequest.setOrderNo(newOrderDetails.getOrderNo());
                walletRequest.setUserIdentity(adminEmail.getParamValue());
                //Check for Promo apply or not
                if (orderDetails.getTotalPrice() == orderDetails.getTotalPriceAfterPromo()) {
                    walletRequest.setAmount(orderDetails.getTotalPrice());
                } else {
                    walletRequest.setAmount(orderDetails.getTotalPriceAfterPromo());
                }
                walletRequest.setTransactionRemark(shippingAddressDTO.getOrderNote());
//                walletRequest.setBeneficiaryId(configurationDTO.getParamValue());
                walletRequest.setCurrentUserId(user.getId());
                walletRequest.setUserType(CommonConstant.USER_TYPE_CUSTOMER);
                walletRequest.setPurchaseType(PurchaseType.SHOPPING);
                walletRequest.setBeneficiaryUserType(CommonConstant.USER_TYPE_ADMIN);
                walletRequest.setTransactionReason(orderNoteDescription);
                ApiResponse apiResponse = userWalletService.transferMoney(walletRequest);
                System.out.println("#### API Response ##### " + apiResponse.getData());
            } else {
                if (PGConstant.RAZOR_PAYMENT_METHODS.contains(shippingAddressDTO.getPaymentMethod())) {
                    // TODO: Store the PG Server response to Local Database
                    // TODO: Verify the payment gateway signature
                    PaymentGatewayResponse paymentGatewayResponse = shippingAddressDTO.getGatewayResponse();
                    ApiResponse apiResponse = userWalletService.verify(shippingAddressDTO.getGatewayResponse());
                    System.out.println("###### IS VERIFIED DATA ##### " + apiResponse.getData());
                    Boolean isVerified = (Boolean) apiResponse.getData();
                    if (isVerified != null) {
                        if (!isVerified.booleanValue()) {
                            throw new RuntimeException("Error while placing order :- Payment verification is failure");
                        }
                        PgDetailsDTO pgDetailsDTO = pgUtils.buildPgDetails(paymentGatewayResponse, paymentMethod,
                                orderDetails.getId());
                        userPGService.savePgDetails(pgDetailsDTO);
                    }
                }
            }

        } else {
            System.out.println(String.format("##### Payment method is  %s ", paymentMethod));
        }

        OrderNoteDTO orderNoteDTO = new OrderNoteDTO();
        orderNoteDTO.setOrderNoteDescription(shippingAddressDTO.getOrderNote());
//        saveNote(orderNoteDTO, orderDetails, CommonConstant.USER_TYPE_CUSTOMER,
//                user.getId(), user.getFirstName() + " " + user.getLastName());
        orderNoteDTO.setOrderNoteDescription(orderNoteDTO.getOrderNoteDescription() == null ? "Order Placed " + orderDetails.getOrderStatus()
                : "Order Placed " + orderDetails.getOrderStatus() + orderNoteDTO.getOrderNoteDescription());
        saveOrderNote(orderNoteDTO, orderDetails, CommonConstant.USER_TYPE_CUSTOMER, user.getId(),false);
        if (newOrderDetails != null) {

            ResponseEntity<ApiResponse> rewardEntity = adminClient.getRewardByEvent("BUY_DEALS", user.getId());
            if (Objects.nonNull(rewardEntity) && Objects.nonNull(rewardEntity.getBody()) && Objects.nonNull(rewardEntity.getBody().getData())) {
                ObjectMapper mapper = new ObjectMapper();
                LoyaltyRewardsRequest rewardResponse = mapper.convertValue(rewardEntity.getBody().getData(), LoyaltyRewardsRequest.class);
                if (newOrderDetails.getTotalPrice() >= rewardResponse.getPriceLimit()) {
                    LoyaltyRewardHistoryResponse updateRequest = new LoyaltyRewardHistoryResponse();
                    updateRequest.setReason(rewardResponse.getEvent());
                    updateRequest.setEvent(rewardResponse.getEvent());
                    updateRequest.setOrderId(newOrderDetails.getId());
                    updateRequest.setType("CREDIT");
                    updateRequest.setRewardPoint(rewardResponse.getRewardPoint());
                    updateRequest.setUserId(user.getId());
                    updateRequest.setTransactionId("");
                    adminClient.updateReward(updateRequest);
                }
            }

            OrderDetailsDTO orderDetailsDTO = convertToOrderDetailsDTO(newOrderDetails);
            if (!applyPromoCodeResponseList.isEmpty()) {
                applyPromoCodeResponseList.forEach(
                        v -> {
                            v.setOrderId(newOrderDetails.getId());
                        }
                );
                adminClient.savePromoCode(applyPromoCodeResponseList);
                if (applyPromoCodeResponseList.size() > 0) {
                    orderDetailsDTO.setPromoCodeResponse(applyPromoCodeResponseList.get(0));
                }
            }
            ResponseEntity responseEntity = smsService.sendSMSNotification(String.format("Your order has been successfully placed %s ", orderNo),
                    Arrays.asList(user.getContactNumber()));
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                System.out.print("SMS Sent Successfully");
            } else {
                System.out.print("Due to System error, not able to send SMS Notification");
            }
            cartItemRepository.deleteCartItem(cartItemIds, user);
            try {
                ResponseEntity<ApiResponse> merchantDetails = merchantClient.getMerchantByMerchantId(orderDetails.getMerchantId());
                if (StringUtils.isNotBlank(orderDetailsDTO.getOrderId())) {
                    OrderEventSource orderEventSource = new OrderEventSource(orderDetailsDTO.getOrderNo(), CommonConstant.ORDER_EVENT_PLACE_ORDER);
                    FcmUserNotificationDTO.Request fcmRequest = new FcmUserNotificationDTO.Request();
                    fcmRequest.setNotificationType(NotificationTypeEnum.ORDER_PLACE);
                    fcmRequest.setNotificationTypeId(orderDetailsDTO.getOrderId());
                    eventPublisher.publishEvent(new OrderEvent(orderEventSource, fcmRequest, user));
                }
                if (merchantDetails.getBody().getData() != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    MerchantDTO merchantData = mapper.convertValue(merchantDetails.getBody().getData(), MerchantDTO.class);
                    FcmUserNotificationDTO.Request fcmRequest = new FcmUserNotificationDTO.Request();
                    fcmRequest.setNotificationType(NotificationTypeEnum.ORDER_RECEIVED);
                    fcmRequest.setNotificationTypeId(orderDetailsDTO.getOrderId());
                    User merchantUser = new User();
                    merchantUser.setId(merchantData.getId());
                    UserDeviceDetails merchantDeviceDetails = new UserDeviceDetails();
                    merchantDeviceDetails.setDeviceToken(merchantData.getDeviceToken());
                    merchantDeviceDetails.setDeviceType(merchantData.getDeviceType());
                    merchantUser.setUserDeviceDetails(merchantDeviceDetails);
                    eventPublisher.publishEvent(new OrderStatusEvent(fcmRequest, merchantUser));
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            } finally {
                return orderDetailsDTO;
            }
        } else {
            throw new RuntimeException("Error while placing order :- contact customer support");
        }
    }

    @Override
    public PageItem<OrderDetailsDTO> getAllOrders(PagedItemInfo pagedInfo, User user, OrderFilterRequest
            orderFilterRequest) {
        GenericSpecificationsBuilder<OrderDetails> builder = new GenericSpecificationsBuilder<>();
        final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(OrderDetails.class, pagedInfo);
//        Page<OrderDetails> orderDetailsList = orderDetailsRepository.findByCustomerAndDeletedFalse(pageRequest, user);
        prepareOrderSearchQuery(orderFilterRequest, builder);
        Page<OrderDetails> pagedResult = orderDetailsRepository.findAll(builder.build(), pageRequest);
        List<OrderDetailsDTO> orderDetailsDTOS = new ArrayList<>();
        for (OrderDetails orderDetails : pagedResult.getContent()) {
            OrderDetailsDTO orderDetailsDTO = convertToOrderDetailsDTO(orderDetails);
            //
            CancelOrder cancelOrder = cancelOrderRepository.findCancelOrdersByOrderId(orderDetails);
            if (cancelOrder != null) {
                List<CancelOrderItemDTO> cancelOrderItemDTOList = new ArrayList<>();
                CancelOrderDTO cancelOrderDTO = CANCEL_ORDER_TO_CANCEL_ORDER_DTO.apply(cancelOrder);
                List<CancelOrderItem> cancelOrderItems = cancelOrder.getCancelOrderItems();
                for (CancelOrderItem cancelOrderItem : cancelOrderItems) {
                    CancelOrderItemDTO cancelOrderItemDTO = CANCEL_ORDER_ITEM_TO_DTO.apply(cancelOrderItem);
                    cancelOrderItemDTOList.add(cancelOrderItemDTO);
                }
                cancelOrderDTO.setCancelOrderItemDTOs(cancelOrderItemDTOList);
                orderDetailsDTO.setCancelOrderDTO(cancelOrderDTO);
            }
            //
            List<OrderNote> orderNotes = orderDetails.getOrderNoteList();
            if (orderNotes != null) {
                List<OrderNoteDTO> orderNoteDTOS = new ArrayList<>();
                for (OrderNote orderNote : orderNotes) {
                    OrderNoteDTO orderNoteDTO = ORDER_NOTE_ENTITY_TO_DTO.apply(orderNote);
                    orderNoteDTOS.add(orderNoteDTO);
                }
                List<OrderNoteDTO> sortedList = orderNoteDTOS.stream().sorted(Comparator.comparing(OrderNoteDTO::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
                orderDetailsDTO.setOrderNotesDtos(sortedList);
            }
            orderDetailsDTO.setPromoCodeResponse(getPromoCodeDetails(orderDetails));
            orderDetailsDTOS.add(orderDetailsDTO);
        }
        return new PageItem<>(pagedResult.getTotalPages(), pagedResult.getTotalElements(), orderDetailsDTOS, pagedInfo.page,
                pagedInfo.items);
    }

    @Override
    public PageItem<OrderDetailsDTO> getAllOrders(PagedItemInfo pagedInfo, OrderFilterRequest orderFilterRequest) {
        GenericSpecificationsBuilder<OrderDetails> builder = new GenericSpecificationsBuilder<>();
        final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(OrderDetails.class, pagedInfo);
        prepareOrderSearchQuery(orderFilterRequest, builder);
        Page<OrderDetails> pagedResult = orderDetailsRepository.findAll(builder.build(), pageRequest);
        List<OrderDetailsDTO> orderDetailsDTOS = new ArrayList<>();
        for (OrderDetails orderDetails : pagedResult.getContent()) {
            OrderDetailsDTO orderDetailsDTO = convertToOrderDetailsDTO(orderDetails);
            //
            CancelOrder cancelOrder = cancelOrderRepository.findCancelOrdersByOrderId(orderDetails);
            if (cancelOrder != null) {
                List<CancelOrderItemDTO> cancelOrderItemDTOList = new ArrayList<>();
                CancelOrderDTO cancelOrderDTO = CANCEL_ORDER_TO_CANCEL_ORDER_DTO.apply(cancelOrder);
                List<CancelOrderItem> cancelOrderItems = cancelOrder.getCancelOrderItems();
                for (CancelOrderItem cancelOrderItem : cancelOrderItems) {
                    CancelOrderItemDTO cancelOrderItemDTO = CANCEL_ORDER_ITEM_TO_DTO.apply(cancelOrderItem);
                    cancelOrderItemDTOList.add(cancelOrderItemDTO);
                }
                cancelOrderDTO.setCancelOrderItemDTOs(cancelOrderItemDTOList);
                orderDetailsDTO.setCancelOrderDTO(cancelOrderDTO);
            }
            //
            List<OrderNote> orderNotes = orderDetails.getOrderNoteList();
            if (orderNotes != null) {
                List<OrderNoteDTO> orderNoteDTOS = new ArrayList<>();
                for (OrderNote orderNote : orderNotes) {
                    OrderNoteDTO orderNoteDTO = ORDER_NOTE_ENTITY_TO_DTO.apply(orderNote);
                    orderNoteDTOS.add(orderNoteDTO);
                }
                List<OrderNoteDTO> sortedList = orderNoteDTOS.stream().sorted(Comparator.comparing(OrderNoteDTO::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
                orderDetailsDTO.setOrderNotesDtos(sortedList);
            }
            orderDetailsDTO.setPromoCodeResponse(getPromoCodeDetails(orderDetails));
            orderDetailsDTOS.add(orderDetailsDTO);
        }
        return new PageItem<>(pagedResult.getTotalPages(), pagedResult.getTotalElements(), orderDetailsDTOS, pagedInfo.page,
                pagedInfo.items);
    }

    @Override
    public List<OrderDetailsDTO> getAllOrdersForReport(OrderFilterRequest orderFilterRequest) {
        GenericSpecificationsBuilder<OrderDetails> builder = new GenericSpecificationsBuilder<>();
        prepareOrderSearchQuery(orderFilterRequest, builder);
        List<OrderDetails> pagedResult = orderDetailsRepository.findAll(builder.build());
        List<OrderDetailsDTO> orderDetailsDTOS = new ArrayList<>();
        for (OrderDetails orderDetails : pagedResult) {
            OrderDetailsDTO orderDetailsDTO = convertToOrderDetailsDTO(orderDetails);
            CancelOrder cancelOrder = cancelOrderRepository.findCancelOrdersByOrderId(orderDetails);
            if (cancelOrder != null) {
                List<CancelOrderItemDTO> cancelOrderItemDTOList = new ArrayList<>();
                CancelOrderDTO cancelOrderDTO = CANCEL_ORDER_TO_CANCEL_ORDER_DTO.apply(cancelOrder);
                List<CancelOrderItem> cancelOrderItems = cancelOrder.getCancelOrderItems();
                for (CancelOrderItem cancelOrderItem : cancelOrderItems) {
                    CancelOrderItemDTO cancelOrderItemDTO = CANCEL_ORDER_ITEM_TO_DTO.apply(cancelOrderItem);
                    cancelOrderItemDTOList.add(cancelOrderItemDTO);
                }
                cancelOrderDTO.setCancelOrderItemDTOs(cancelOrderItemDTOList);
                orderDetailsDTO.setCancelOrderDTO(cancelOrderDTO);
            }
            List<OrderNote> orderNotes = orderDetails.getOrderNoteList();
            if (orderNotes != null) {
                List<OrderNoteDTO> orderNoteDTOS = new ArrayList<>();
                for (OrderNote orderNote : orderNotes) {
                    OrderNoteDTO orderNoteDTO = ORDER_NOTE_ENTITY_TO_DTO.apply(orderNote);
                    orderNoteDTOS.add(orderNoteDTO);
                }
                List<OrderNoteDTO> sortedList = orderNoteDTOS.stream().sorted(Comparator.comparing(OrderNoteDTO::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
                orderDetailsDTO.setOrderNotesDtos(sortedList);
            }
            orderDetailsDTO.setPromoCodeResponse(getPromoCodeDetails(orderDetails));
            orderDetailsDTOS.add(orderDetailsDTO);
        }
        return orderDetailsDTOS;
    }

    public ApplyPromoCodeResponse getPromoCodeDetails(OrderDetails orderDetails) {
        //Get Applied Promo Code Details on Order.
        ApplyPromoCodeResponse applyPromoCodeResponse = null;
        ResponseEntity<ApiResponse> promoCodeDetailsResponse = adminClient.getPromoCodeHistoryByOrderId(orderDetails.getId());
        if (promoCodeDetailsResponse.getBody() != null && promoCodeDetailsResponse.getBody().getData() != null) {
            Gson gson = new Gson();
            String promoResponseJson = gson.toJson(promoCodeDetailsResponse.getBody().getData());
            PromoCodeDetailsDTO[] responseList = gson.fromJson(promoResponseJson, PromoCodeDetailsDTO[].class);
            for (PromoCodeDetailsDTO promoCodeDetailsDTO : responseList) {
                applyPromoCodeResponse = new ApplyPromoCodeResponse();
                applyPromoCodeResponse.setPromoCode(promoCodeDetailsDTO.getPromoCode());
                applyPromoCodeResponse.setAmount((float) orderDetails.getTotalPrice());
                applyPromoCodeResponse.setDiscount(promoCodeDetailsDTO.getDiscountAmount());
                applyPromoCodeResponse.setAmountAfterDiscount((float) orderDetails.getTotalPriceAfterPromo());
            }
        }
        return applyPromoCodeResponse;
    }


    @Override
    public PageItem<OrderDetailsDTO> getAllOrdersByOrderStatus(PagedItemInfo pagedInfo, User user, String status) {
        final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(OrderDetails.class, pagedInfo);
//        if(status.equalsIgnoreCase(OrderStatus.CANCELLED.name()) || status.equalsIgnoreCase(OrderStatus.RETURNED.name())) {
//            Page<CancelOrder> cancelOrders = cancelOrderRepository.findCancelOrdersByUser(pageRequest,user);
//        }
        Page<OrderDetails> orderDetailsList = orderDetailsRepository.findByCustomerAndOrderStatusAndDeletedFalse(pageRequest, user, status);
        List<OrderDetailsDTO> orderDetailsDTOS = new ArrayList<>();
        for (OrderDetails orderDetails : orderDetailsList) {
            OrderDetailsDTO orderDetailsDTO = convertToOrderDetailsDTO(orderDetails);
            if (orderDetailsDTO.getOrderStatus().equalsIgnoreCase(OrderStatus.CANCELLED.name()) ||
                    status.equalsIgnoreCase(OrderStatus.RETURNED.name())) {
                CancelOrder cancelOrder = cancelOrderRepository.findCancelOrdersByOrderId(orderDetails);
                CancelOrderDTO cancelOrderDTO = OrderDetailsTransformer.CANCEL_ORDER_TO_CANCEL_ORDER_DTO.apply(cancelOrder);
                orderDetailsDTO.setCancelOrderDTO(cancelOrderDTO);
            }

            orderDetailsDTOS.add(orderDetailsDTO);
        }
        return new PageItem<>(orderDetailsList.getTotalPages(), orderDetailsList.getTotalElements(), orderDetailsDTOS, pagedInfo.page,
                pagedInfo.items);
    }

    public CancelOrderDTO getCancelledOrderDetails(OrderDetails orderDetails) {
        CancelOrder cancelOrder = cancelOrderRepository.findCancelOrdersByOrderId(orderDetails);
        if (cancelOrder != null) {
            List<CancelOrderItemDTO> cancelOrderItemDTOS = new ArrayList<>();
            List<CancelOrderItem> cancelOrderItems = cancelOrder.getCancelOrderItems();
            for (CancelOrderItem cancelOrderItem : cancelOrderItems) {
                CancelOrderItemDTO itemDTO = OrderItemTransformer.CANCEL_ORDER_ITEM_TO_DTO.apply(cancelOrderItem);
                itemDTO.setCancelOrderId(cancelOrder.getId());
                cancelOrderItemDTOS.add(itemDTO);
            }
            CancelOrderDTO cancelOrderDTO = CANCEL_ORDER_TO_CANCEL_ORDER_DTO.apply(cancelOrder);
            cancelOrderDTO.setCancelOrderItemDTOs(cancelOrderItemDTOS);
            return cancelOrderDTO;
        }
        return null;
    }

    @Override
    public OrderDetailsDTO getOrderByOrderNo(String id, String userName, String userType, String orderNo) throws
            ObjectNotFoundException {
        List<OrderItemDTO> orderItemDtos = new ArrayList<>();
        OrderDetails orderDetails = null;
        CancelOrder cancelOrder = null;
        if (StringUtils.isBlank(userType) && StringUtils.isBlank(id)) {
            User user = userRepository.findByEmail(userName).orElse(null);
            orderDetails = orderDetailsRepository.findByOrderNoAndCustomerAndDeletedFalse(orderNo, user);
        } else {
            if (userType.equalsIgnoreCase(CommonConstant.USER_TYPE_MERCHANT)) {
                orderDetails = orderDetailsRepository.findByOrderNoAndMerchantIdAndDeletedFalse(orderNo, id);
            }
            if (userType.equalsIgnoreCase(CommonConstant.USER_TYPE_ADMIN)) {
                orderDetails = orderDetailsRepository.findByOrderNoAndDeletedFalse(orderNo);
            }
        }
        if (orderDetails == null) {
            throw new ObjectNotFoundException(String.format("Order is not found for the Order No %s ", orderNo));
        }
        OrderDetailsDTO orderDetailsDTO = convertToOrderDetailsDTO(orderDetails);
        cancelOrder = cancelOrderRepository.findCancelOrdersByOrderId(orderDetails);
        if (cancelOrder != null) {
            List<CancelOrderItemDTO> cancelOrderItemDTOList = new ArrayList<>();
            CancelOrderDTO cancelOrderDTO = CANCEL_ORDER_TO_CANCEL_ORDER_DTO.apply(cancelOrder);
            orderDetailsDTO.setCancelOrderDTO(cancelOrderDTO);
            List<CancelOrderItem> cancelOrderItems = cancelOrder.getCancelOrderItems();
            for (CancelOrderItem cancelOrderItem : cancelOrderItems) {
                CancelOrderItemDTO cancelOrderItemDTO = CANCEL_ORDER_ITEM_TO_DTO.apply(cancelOrderItem);
                cancelOrderItemDTOList.add(cancelOrderItemDTO);
            }
            cancelOrderDTO.setCancelOrderItemDTOs(cancelOrderItemDTOList);
        }
        List<OrderNote> orderNotes = orderDetails.getOrderNoteList();
        if (orderNotes != null) {
            List<OrderNoteDTO> orderNoteDTOS = new ArrayList<>();
            for (OrderNote orderNote : orderNotes) {
                OrderNoteDTO orderNoteDTO = ORDER_NOTE_ENTITY_TO_DTO.apply(orderNote);
                orderNoteDTOS.add(orderNoteDTO);
            }
            List<OrderNoteDTO> sortedList = orderNoteDTOS.stream().sorted(Comparator.comparing(OrderNoteDTO::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
            orderDetailsDTO.setOrderNotesDtos(sortedList);
        }
        orderDetailsDTO.setPromoCodeResponse(getPromoCodeDetails(orderDetails));
        return orderDetailsDTO;
    }


    @Override
    public OrderDetails findOrderByNo(String orderNo) {
        return orderDetailsRepository.findByOrderNoAndDeletedFalse(orderNo);
    }

    @Override
    public OrderDetails findByOrderNoAndMerchantIdAndDeletedFalse(String orderNo, String merchantId) {
        return orderDetailsRepository.findByOrderNoAndMerchantIdAndDeletedFalse(orderNo, merchantId);
    }

    @Override
    public OrderDetails findByOrderNoAndCustomer(String orderNo, User customer) {
        return orderDetailsRepository.findByOrderNoAndCustomer(orderNo, customer);
    }

    @Transactional
    @Override
    public List<OrderItem> doUpdateOrderStatus(OrderDetails orderDetails, String status,
                                               OrderNoteDTO orderNoteDTO, String userType,
                                               List<String> orderItemIds, String currentId) throws Exception {

        List<OrderItem> orderItems = orderDetails.getOrderItems();
        List<OrderItem> cancellingItems = new ArrayList<>();
        OrderStatusValidation orderStatusValidation = orderStatusValidationRepository
                .findOrderValidation(status, userType).orElse(null);
        if (orderStatusValidation == null) {
            throw new ActorPayException(String.format("Can't update Order Item status to %s", status));
        }
        String orderCondition = orderStatusValidation.getOrderStatusCondition();
        if (StringUtils.isBlank(orderCondition)) {
            throw new ActorPayException(String.format("Can't update Order Item status to %s", status));
        }
        List<String> orderConditions = Arrays.asList(orderCondition.split(","));
        if (orderItemIds != null && !orderItemIds.isEmpty()) {
            for (String id : orderItemIds) {
                OrderItem orderItem = orderItems.stream().filter(ord -> ord.getId().equals(id)).findAny().get();
                cancellingItems.add(orderItem);
            }
        } else {
            cancellingItems.addAll(orderItems);
        }

        for (OrderItem orderItem : cancellingItems) {
            if (userFeignHelper.checkCancellationTime(orderItem)) {
                throw new ActorPayException(String
                        .format("Can't Update the Order Status to Cancel/Return. " +
                                "Order is already delivered and Return time is crossed "));
            }
            if (orderConditions.contains(orderItem.getOrderItemStatus())) {
                orderItemRepository.updateOrderStatusByOrderItemId(orderDetails, orderItem.getId(), status);
            } else {
                throw new ActorPayException(String.format("Can't update Order Item status. The status must be %s ", orderCondition));
            }
        }
        List<String> productNameList = new ArrayList<>();
        for (OrderItem orderItem : cancellingItems) {
            ResponseEntity<ApiResponse> responseEntity =
                    userFeignHelper.getProductNameById(orderItem.getProductId());
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                String productName = (String) responseEntity.getBody().getData();
                productNameList.add(productName);
            }
        }
        String productNames = productNameList.stream().collect(Collectors.joining());
        String orderNoteDescription = orderNoteDTO.getOrderNoteDescription() == null ? "" : orderNoteDTO.getOrderNoteDescription();
        String noteDescription =
                new StringBuffer().append(orderNoteDescription).append(" Order Status updated to ")
                        .append(status).append("  ProductName: ").append(productNames).toString();

        orderNoteDTO.setOrderNoteDescription(noteDescription);
        saveOrderNote(orderNoteDTO, orderDetails, userType, currentId,false);
        setGlobalOrderStatus(orderDetails.getId());
        if (status.equalsIgnoreCase(OrderStatus.RETURNING_ACCEPTED.name())) {
            List<ProductCommission> productCommissions = new ArrayList<>();
            CancelOrder cancelOrder = cancelOrderRepository.findCancelOrdersByOrderId(orderDetails);
            Double refundAmount = cancelOrder.getCancelOrderItems().stream()
                    .map(x -> x.getOriginalAmount())
                    .collect(Collectors.summingDouble(Double::intValue));
            if (orderDetails.getPaymentMethod().equalsIgnoreCase(PaymentMethod.netbanking.name()))
                processRefund(refundAmount, orderDetails);
            if (orderDetails.getPaymentMethod().equalsIgnoreCase(PaymentMethod.wallet.name()))
                System.out.println("### Deduct Amount from Admin Wallet");
            for (OrderItem orderItem : cancellingItems) {
                ProductCommission productCommission = productCommissionRepository.findByOrderItemAndDeletedFalse(orderItem);
                productCommission.setOrderStatus(OrderStatus.RETURNING_ACCEPTED.name());
                productCommissions.add(productCommission);
            }
            productCommissionRepository.saveAll(productCommissions);
            System.out.println("###### SUM OF DOUBLE VALUE  " + refundAmount);
        }
        try {
            if (orderDetails.getCustomer() != null) {
                System.out.println(orderDetails.getCustomer().getUserDeviceDetails());
                FcmUserNotificationDTO.Request fcmRequest = new FcmUserNotificationDTO.Request();
                if(status.equalsIgnoreCase("RETURNING_ACCEPTED")){
                    fcmRequest.setNotificationType(NotificationTypeEnum.RETURNING_ACCEPTED);
                }else if(status.equalsIgnoreCase("RETURNING_DECLINED")){
                    fcmRequest.setNotificationType(NotificationTypeEnum.RETURNING_DECLINED);
                }else if(status.equalsIgnoreCase("RETURNED")){
                    fcmRequest.setNotificationType(NotificationTypeEnum.RETURNED);
                }
                else{
                    fcmRequest.setNotificationType(NotificationTypeEnum.ORDER_STATUS_CHANGE);
                }
                fcmRequest.setNotificationTypeId(orderDetails.getOrderNo());
                eventPublisher.publishEvent(new OrderStatusEvent(fcmRequest, orderDetails.getCustomer()));
            }
        } catch (Exception e) {
            System.out.println("Error while sending notification: " + e.getMessage());
        } finally {
            return cancellingItems;
        }
    }


    @Transactional
    @Override
    public OrderNoteDTO saveOrderNote(OrderNoteDTO orderNoteDTO, OrderDetails orderDetails, String
            userType, String id,Boolean sendNotification) {
        String userName = "";
        if (userType.equalsIgnoreCase(CommonConstant.USER_TYPE_MERCHANT)) {
            MerchantDTO merchantDTO = userFeignHelper.getMerchantId(id);
            userName = merchantDTO.getBusinessName();
        } else if (userType.equalsIgnoreCase(CommonConstant.USER_TYPE_ADMIN)) {
            AdminDTO adminDTO = userFeignHelper.getAdminUserId(id);
            userName = adminDTO.getEmail();
        } else {
            User user = userRepository.findByIdAndIsActiveTrueAndDeletedFalse(id);
            userName = user.getEmail();
        }
        OrderNote orderNote = new OrderNote();
        orderDetails.setId(orderDetails.getId());
        orderNote.setOrderDetails(orderDetails);
        orderNote.setUserType(userType);
        orderNote.setUserId(orderDetails.getCustomer().getId());
        orderNote.setOrderNoteBy(userName);
        orderNote.setMerchantId(orderDetails.getMerchantId());
        orderNote.setActive(Boolean.TRUE);
        orderNote.setOrderNoteDescription(orderNoteDTO.getOrderNoteDescription());
        orderNote.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        OrderNote newOrderNote = orderNoteRepository.save(orderNote);
        try {
            if(sendNotification){
                if (userType.equalsIgnoreCase(CommonConstant.USER_TYPE_MERCHANT)) {
                    User user = orderDetails.getCustomer();
                    if (user != null) {
                        FcmUserNotificationDTO.Request fcmRequest = new FcmUserNotificationDTO.Request();
                        fcmRequest.setNotificationType(NotificationTypeEnum.ORDER_NOTE);
                        fcmRequest.setNotificationTypeId(newOrderNote.getOrderDetails().getOrderNo());
                        eventPublisher.publishEvent(new OrderStatusEvent(fcmRequest, user));
                    }
                } else if (userType.equalsIgnoreCase(CommonConstant.USER_TYPE_CUSTOMER)) {
                    ResponseEntity<ApiResponse> merchantDetails = merchantClient.getMerchantByMerchantId(orderDetails.getMerchantId());
                    if (merchantDetails.getBody().getData() != null) {
                        ObjectMapper mapper = new ObjectMapper();
                        MerchantDTO merchantData = mapper.convertValue(merchantDetails.getBody().getData(), MerchantDTO.class);
                        FcmUserNotificationDTO.Request fcmRequest = new FcmUserNotificationDTO.Request();
                        fcmRequest.setNotificationType(NotificationTypeEnum.ORDER_NOTE);
                        fcmRequest.setNotificationTypeId(newOrderNote.getOrderDetails().getOrderNo());
                        User merchantUser = new User();
                        merchantUser.setId(merchantData.getId());
                        UserDeviceDetails merchantDeviceDetails = new UserDeviceDetails();
                        merchantDeviceDetails.setDeviceToken(merchantData.getDeviceToken());
                        merchantDeviceDetails.setDeviceType(merchantData.getDeviceType());
                        merchantUser.setUserDeviceDetails(merchantDeviceDetails);
                        eventPublisher.publishEvent(new OrderStatusEvent(fcmRequest, merchantUser));
                    }
                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            return OrderItemTransformer.ENTITY_TO_ORDER_NOTE_DTO.apply(newOrderNote);
        }
    }

    @Override
    public OrderDetails findByOrderNo(String orderNo, String userType, String id) {

        OrderDetails orderDetails = null;
        if (CommonConstant.USER_TYPE_CUSTOMER.equalsIgnoreCase(userType)) {
            User user = userRepository.findByIdAndIsActiveTrueAndDeletedFalse(id);
            orderDetails = orderDetailsRepository.findByOrderNoAndCustomer(orderNo, user);
        } else if (CommonConstant.USER_TYPE_MERCHANT.equalsIgnoreCase(userType)) {
            orderDetails = orderDetailsRepository.findByOrderNoAndMerchantId(orderNo, id);
        } else {
            orderDetails = orderDetailsRepository.findByOrderNoAndDeletedFalse(orderNo);
        }
        return orderDetails;
    }

    @Override
    public List<OrderNoteDTO> getAllOrderNotes(String userType, OrderNoteFilterRequest filterRequest) {

        List<OrderNoteDTO> orderNoteDTOList = new ArrayList<>();
        GenericSpecificationsBuilder<OrderNote> builder = new GenericSpecificationsBuilder<>();
        prepareOrderNoteSearchQuery(filterRequest, builder);
        List<OrderNote> orderNoteList = orderNoteRepository.findAll(builder.build());

        for (OrderNote orderNote : orderNoteList) {
            OrderNoteDTO orderNoteDTO = OrderItemTransformer.ENTITY_TO_ORDER_NOTE_DTO.apply(orderNote);
            orderNoteDTOList.add(orderNoteDTO);
        }
        return orderNoteDTOList;
    }

    @Transactional
    public void setGlobalOrderStatus(String orderId) {
        OrderDetails oDetails = orderDetailsRepository.findById(orderId).orElse(null);
        List<OrderItem> orderItems = oDetails.getOrderItems();
        String orderStatus = "";
        List<OrderItem> retStream = orderItems.stream().filter(orderItem ->
                orderItem.getOrderItemStatus().equalsIgnoreCase(OrderStatus.RETURNED.name())).collect(Collectors.toList());
        List<OrderItem> retingStream = orderItems.stream().filter(orderItem ->
                orderItem.getOrderItemStatus().equalsIgnoreCase(OrderStatus.RETURNING.name())).collect(Collectors.toList());
        List<OrderItem> canStream = orderItems.stream().filter(orderItem ->
                orderItem.getOrderItemStatus().equalsIgnoreCase(OrderStatus.CANCELLED.name())).collect(Collectors.toList());
        List<OrderItem> retAccepted = orderItems.stream().filter(orderItem ->
                orderItem.getOrderItemStatus().equalsIgnoreCase(OrderStatus.RETURNING_ACCEPTED.name())).collect(Collectors.toList());
        List<OrderItem> retDeclined = orderItems.stream().filter(orderItem ->
                orderItem.getOrderItemStatus().equalsIgnoreCase(OrderStatus.RETURNING_DECLINED.name())).collect(Collectors.toList());

        if (orderItems.size() == 1) {
            orderStatus = orderItems.get(0).getOrderItemStatus();
        } else {
            if (retStream.size() >= 1 && retStream.size() != orderItems.size()) {
                orderStatus = OrderStatus.PARTIALLY_RETURNED.name();
            } else if (retingStream.size() >= 1 && retingStream.size() != orderItems.size()) {
                orderStatus = OrderStatus.PARTIALLY_RETURNING.name();
            } else if (canStream.size() >= 1 && canStream.size() != orderItems.size()) {
                orderStatus = OrderStatus.PARTIALLY_CANCELLED.name();
            } else if ((retStream.size() != 0) && (retStream.size() == orderItems.size())) {
                orderStatus = OrderStatus.RETURNED.name();
            } else if ((retingStream.size() != 0) && (retingStream.size() == orderItems.size())) {
                orderStatus = OrderStatus.RETURNING.name();
            } else if ((canStream.size() != 0) && (canStream.size() == orderItems.size())) {
                orderStatus = OrderStatus.CANCELLED.name();
            } else if ((retAccepted.size() != 0) && (retAccepted.size() == orderItems.size())) {
                orderStatus = OrderStatus.RETURNING_ACCEPTED.name();
            } else if (retAccepted.size() >= 1 && canStream.size() != orderItems.size()) {
                orderStatus = OrderStatus.PARTIALLY_RETURNING_ACCEPTED.name();
            } else if ((retDeclined.size() != 0) && (retDeclined.size() == orderItems.size())) {
                orderStatus = OrderStatus.RETURNING_DECLINED.name();
            } else if (retDeclined.size() >= 1 && retDeclined.size() != orderItems.size()) {
                orderStatus = OrderStatus.PARTIALLY_RETURNING_DECLINED.name();
            } else {
                orderStatus = orderItems.get(0).getOrderItemStatus();
            }
        }
        oDetails.setOrderStatus(orderStatus);
        orderDetailsRepository.saveAndFlush(oDetails);
    }

    @Override
    public ApiResponse checkout(OrderRequest orderRequest) {
        orderRequest.setAmount(orderRequest.getAmount());
        orderRequest.setReceipt(codeGenerator.getReceipt());
        orderRequest.setCurrency(PGConstant.IND_CURRENCY);
        ApiResponse apiResponse = userWalletService.createOrder(orderRequest);
        return apiResponse;
    }

    @Override
    public void processRefund(Double refundAmount, OrderDetails orderDetails) throws Exception {
        ApiResponse apiResponse = userPGService.getPGPaymentDetails(orderDetails.getId());
        ObjectMapper mapper = new ObjectMapper();
        PgDetailsDTO pgDetailsDTO = mapper.convertValue(apiResponse.getData(), PgDetailsDTO.class);
        RefundRequest refundRequest = new RefundRequest();
        refundRequest.setRefundAmount(refundAmount);
        refundRequest.setPaymentId(pgDetailsDTO.getPaymentId());
        ApiResponse refundResponse = userPGService.refund(refundRequest);
        mapper = new ObjectMapper();
        RefundResponse refundDetails = mapper.convertValue(refundResponse.getData(), RefundResponse.class);
        System.out.println("####### Refund Response Amount  " + refundDetails.getAmount());
        System.out.println("####### Refund Response Status " + refundDetails.getStatus());
    }

    @Override
    public CancelOrder findCancelOrdersByOrderId(OrderDetails orderDetails) {
        return cancelOrderRepository.findCancelOrdersByOrderId(orderDetails);
    }

    private OrderDetailsDTO convertToOrderDetailsDTO(OrderDetails orderDetails) {
        List<OrderItemDTO> orderItemDtos = new ArrayList<>();
        List<OrderItem> orderItems = orderDetails.getOrderItems();

        ResponseEntity<ApiResponse> merResponseEntity = merchantClient.getMerchantByMerchantId(orderDetails.getMerchantId());
        ApiResponse apiResponse = merResponseEntity.getBody();
        ObjectMapper mapper = new ObjectMapper();
        MerchantDTO merchantDTO = mapper.convertValue(apiResponse.getData(), MerchantDTO.class);
        for (OrderItem orderItem : orderItems) {
            OrderItemDTO orderItemDTO = ORDER_ITEM_TO_ORDER_ITEM_DTO.apply(orderItem);
            orderItemDTO.setProductName(merchantClient.getProductName(orderItem.getProductId()).getBody());
            orderItemDTO.setMerchantName(merchantDTO.getMerchantName());
            orderItemDTO.setActualPrice(orderItem.getActualPrice());
            orderItemDtos.add(orderItemDTO);
        }
        orderDetails.setTotalCgst(cartItemService.doubleFormattingUpTo2Decimal(orderDetails.getTotalCgst()));
        orderDetails.setTotalSgst(cartItemService.doubleFormattingUpTo2Decimal(orderDetails.getTotalSgst()));
        OrderDetailsDTO orderDetailsDTO = ORDER_DETAILS_TO_ORDER_DETAILS_DTO.apply(orderDetails);

        UserDTO userDTO = new UserDTO();
        userDTO.setId(orderDetails.getCustomer().getId());
        userDTO.setEmail(orderDetails.getCustomer().getEmail());
        userDTO.setFirstName(orderDetails.getCustomer().getFirstName());
        userDTO.setLastName(orderDetails.getCustomer().getLastName());
        userDTO.setContactNumber(orderDetails.getCustomer().getContactNumber());
        orderDetailsDTO.setCustomer(userDTO);
        orderDetailsDTO.setOrderItemDtos(orderItemDtos);
        orderDetailsDTO.setMerchantDTO(merchantDTO);
        if (orderDetails.getOrderAddress() != null) {
            OrderAddressDTO orderAddressDTO = ORDER_ADDR_ENTITY_TO_ORDER_ADDR_DTO.apply(orderDetails.getOrderAddress());
            orderDetailsDTO.setShippingAddressDTO(orderAddressDTO);
        }
        return orderDetailsDTO;
    }

    private void prepareOrderSearchQuery(OrderFilterRequest
                                                 filterRequest, GenericSpecificationsBuilder<OrderDetails> builder) {

        builder.with(orderSpecificationFactory.isEqual("deleted", false));
        if (org.apache.commons.lang.StringUtils.isNotBlank(filterRequest.getCustomerEmail())) {

            User user = userRepository.findByEmailAndIsActiveTrueAndDeletedFalse(filterRequest.getCustomerEmail()).orElse(null);
            if (user == null) {
                throw new ActorPayException("Order is not found for the Customer");
            }
            builder.with(orderSpecificationFactory.isEqual("customer", user));
        }
        if (filterRequest.getStatus() != null) {
            builder.with(orderSpecificationFactory.isEqual("isActive", filterRequest.getStatus()));
        }
        if (StringUtils.isNotBlank(filterRequest.getMerchantId())) {
            builder.with(orderSpecificationFactory.isEqual("merchantId", filterRequest.getMerchantId()));
        }
        if (StringUtils.isNotBlank(filterRequest.getMerchantName())) {
            builder.with(orderSpecificationFactory.isEqual("merchantName", filterRequest.getMerchantName()));
        }
        if (filterRequest.getStartDate() != null) {
            builder.with(orderSpecificationFactory.isGreaterThan("createdAt", filterRequest.getStartDate().atStartOfDay()));
        }
        if (filterRequest.getEndDate() != null) {
            builder.with(orderSpecificationFactory.isLessThan("createdAt", filterRequest.getEndDate().plusDays(1).atStartOfDay()));
        }
        if (filterRequest.getTotalPrice() != null) {
            builder.with(orderSpecificationFactory.isEqual("totalPrice", filterRequest.getTotalPrice()));
        }
        if (filterRequest.getPriceRangeFrom() != null) {
            builder.with(orderSpecificationFactory.isGreaterThanOrEquals("totalPrice", filterRequest.getPriceRangeFrom()));
        }
        if (filterRequest.getPriceRangeTo() != null) {
            builder.with(orderSpecificationFactory.isLessThanOrEquals("totalPrice", filterRequest.getPriceRangeTo()));
        }
        if (StringUtils.isNotBlank(filterRequest.getOrderNo())) {
            builder.with(orderSpecificationFactory.isEqual("orderNo", filterRequest.getOrderNo()));
        }
        if (StringUtils.isNotBlank(filterRequest.getOrderId())) {
            builder.with(orderSpecificationFactory.isEqual("id", filterRequest.getOrderId()));
        }
        if (StringUtils.isNotBlank(filterRequest.getOrderStatus())) {
            builder.with(orderSpecificationFactory.isEqual("orderStatus", filterRequest.getOrderStatus()));
        }
    }

    @Override
    public List<OrderNoteDTO> findByOrderId(String orderId) {
        List<OrderNote> orderNotes = new ArrayList<>();
        List<OrderNoteDTO> orderNoteDTOS = new ArrayList<>();
        for (OrderNote orderNote : orderNotes) {
            OrderNoteDTO orderNoteDTO = ORDER_NOTE_ENTITY_TO_DTO.apply(orderNote);
            orderNoteDTOS.add(orderNoteDTO);
        }
        return orderNoteDTOS;
    }

    @Override
    public String getOrderStatusById(String id) {
        String orderStatus = orderDetailsRepository.getOrderStatusById(id);
        return orderStatus;
    }


    private void prepareOrderNoteSearchQuery(OrderNoteFilterRequest
                                                     filterRequest, GenericSpecificationsBuilder<OrderNote> builder) {

        if (org.apache.commons.lang.StringUtils.isNotBlank(filterRequest.getOrderNo())) {
            OrderDetails orderDetails = orderDetailsRepository.findByOrderNoAndDeletedFalse(filterRequest.getOrderNo());
            builder.with(orderNoteSpecificationFactory.isEqual("orderDetails", orderDetails));
        }

        if (filterRequest.getOrderStatus() != null) {
            builder.with(orderNoteSpecificationFactory.isEqual("orderStatus", filterRequest.getOrderStatus()));
        }

        if (StringUtils.isNotBlank(filterRequest.getUserId())) {
            builder.with(orderNoteSpecificationFactory.isEqual("userId", filterRequest.getUserId()));
        }
        if (StringUtils.isNotBlank(filterRequest.getMerchantId())) {
            builder.with(orderNoteSpecificationFactory.isEqual("merchantId", filterRequest.getMerchantId()));
        }
    }

    @Override
    public OrderItemDTO findOrderItemById(String id) {
        OrderItem orderItem = orderItemRepository.findByIdAndDeletedFalse(id);
        OrderItemDTO orderItemDTO = ORDER_ITEM_TO_ORDER_ITEM_DTO.apply(orderItem);
        return orderItemDTO;
    }

    @Override
    public void saveOrderItem(OrderItem orderItem) {
        orderItemRepository.saveAndFlush(orderItem);
    }

    private OrderItemCommission getOrderCommission(List<MerchantSettingsDTO> merchantSettingsDTOS, String
            merchantId) {
        OrderItemCommission orderItemCommission = new OrderItemCommission();
        for (MerchantSettingsDTO settingsDTO : merchantSettingsDTOS) {
            if (settingsDTO.getParamName().equalsIgnoreCase(CommonConstant.ADMIN_COMMISSION))
                orderItemCommission.setAdminCommission(Double.parseDouble(settingsDTO.getParamValue()));
            if (settingsDTO.getParamName().equalsIgnoreCase(CommonConstant.RETURN_FEE))
                orderItemCommission.setReturnFee(Double.parseDouble(settingsDTO.getParamValue()));
            if (settingsDTO.getParamName().equalsIgnoreCase(CommonConstant.CANCELLATION_FEE))
                orderItemCommission.setCancellationFee(Double.parseDouble(settingsDTO.getParamValue()));
            if (settingsDTO.getParamName().equalsIgnoreCase(CommonConstant.RETURN_DAYS))
                orderItemCommission.setReturnDays(Integer.parseInt(settingsDTO.getParamValue()));
        }
        orderItemCommission.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        orderItemCommission.setActive(Boolean.TRUE);
        return orderItemCommission;
    }

    private void checkProductAvailability(List<OrderItem> orderItems) throws Exception {
        for (OrderItem orderItem : orderItems) {
            ProductDTO productDTO = userFeignHelper.getProductById(orderItem.getProductId());
            if (productDTO.getStockCount() != 0) {
                if (productDTO.getStockCount() < orderItem.getProductQty()) {
                    throw new RuntimeException(String.format("Insufficient Product Stock for the Product: %s  " +
                            "Stock availability is %d ", productDTO.getName(), productDTO.getStockCount()));
                }
            } else {
                if (productDTO.getStockCount() <= 0) {
                    throw new RuntimeException(
                            String.format("Product is OUT OF STOCK. Product %s is not available to Purchase: ", productDTO.getName()));
                }
            }

        }
    }

    @Override
    public ApplyReferralDTO applyReferral(String orderNo, ReferralSettingDTO referralSettingDTO) {

        ApplyReferralDTO response = new ApplyReferralDTO();

        OrderDetails orderDetails = orderDetailsRepository.findByOrderNoAndDeletedFalse(orderNo);

        if (orderDetails == null) {
            throw new ObjectNotFoundException(String.format("Order is not found for the Order No %s ", orderNo));
        }

        User user = userRepository.findById(orderDetails.getCustomer().getId()).orElse(null);

        if (user == null) {
            throw new RuntimeException("No referred User is available");
        }

        User referralUser = userRepository.findByReferralCode(user.getFromReferralCode()).orElse(null);

        if (referralUser == null || !referralUser.isActive()) {
            throw new RuntimeException("User who provided referral is not active or does not exist");
        }

        if (referralSettingDTO.getMinOrderValue() > orderDetails.getTotalPrice()) {
            throw new RuntimeException("User is not eligible for referral as order price is less than minimum order value");
        }

        List<OrderDetails> orderDetails1 = orderDetailsRepository.findByCustomerAndDeletedFalse(user);

        if (referralSettingDTO.getOrderNumber() != orderDetails1.size()) {
            // TODO: need to throw an exception
        }

        double amount;

        if (referralSettingDTO.getType().equalsIgnoreCase("Fix")) {
            amount = referralSettingDTO.getDiscount();
        } else {
            amount = orderDetails.getTotalPrice() * referralSettingDTO.getDiscount() / 100;

            if (amount > referralSettingDTO.getMaxDiscount()) {
                amount = (double) referralSettingDTO.getMaxDiscount();
            }

        }
        WalletRequest walletRequest = new WalletRequest();
        walletRequest.setAmount(amount);
        walletRequest.setCurrentUserId(referralUser.getId());
        ApiResponse apiResponse = userWalletService.addRewardToWallet(walletRequest);
        WalletTransactionResponse walletResponse = new ObjectMapper().convertValue(apiResponse.getData(), WalletTransactionResponse.class);

        ReferralHistory referralHistory = new ReferralHistory();
        referralHistory.setAmount(walletRequest.getAmount());
        referralHistory.setUserId(referralUser.getId());
        referralHistory.setReferredUserId(user.getId());
        referralHistory.setReferredUserName(user.getFirstName() + "  " + user.getLastName());
        referralHistory.setWalletTransactionId(walletResponse.getParentTransactionId());
        referralHistory.setCreatedAt(LocalDateTime.now());
        referralHistory.setUpdatedAt(LocalDateTime.now());
        referralHistoryRepository.save(referralHistory);

        response.setOrderNo(orderNo);
        response.setTransferredAmount(walletResponse.getTransferredAmount());
        response.setUserReferredId(referralUser.getId());
        return response;
    }
}

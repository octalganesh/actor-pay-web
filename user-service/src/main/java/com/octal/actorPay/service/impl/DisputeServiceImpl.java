package com.octal.actorPay.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.constants.Disputes;
import com.octal.actorPay.constants.OrderStatus;
import com.octal.actorPay.dto.*;
import com.octal.actorPay.dto.enums.NotificationTypeEnum;
import com.octal.actorPay.dto.request.DisputeFilterRequest;
import com.octal.actorPay.entities.*;
import com.octal.actorPay.exceptions.ActorPayException;
import com.octal.actorPay.feign.clients.MerchantClient;
import com.octal.actorPay.listners.events.OrderEvent;
import com.octal.actorPay.listners.events.OrderEventSource;
import com.octal.actorPay.listners.events.OrderStatusEvent;
import com.octal.actorPay.repositories.*;
import com.octal.actorPay.service.DisputeService;
import com.octal.actorPay.service.UserEmailService;
import com.octal.actorPay.specification.GenericSpecificationsBuilder;
import com.octal.actorPay.specification.SpecificationFactory;
import com.octal.actorPay.transformer.DisputeItemTransformer;
import com.octal.actorPay.transformer.PagedItemsTransformer;
import com.octal.actorPay.utils.PercentageCalculateManager;
import com.octal.actorPay.utils.UserFeignHelper;
import com.octal.actorPay.utils.UserServiceCodeGenerator;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DisputeServiceImpl implements DisputeService {


    private DisputeItemRepository disputeItemRepository;

    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private MerchantClient merchantClient;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    private UserServiceCodeGenerator codeGenerator;

    private DisputeMessageRepository disputeMessageRepository;

    private PercentageCalculateManager percentageCalculateManager;

    private ProductCommissionRepository productCommissionRepository;

    private OrderItemCommissionRepository orderItemCommissionRepository;

    private CancelOrderItemRepository cancelOrderItemRepository;

    private UserEmailService userEmailService;

    private CancelOrderRepository cancelOrderRepository;

    private SpecificationFactory<DisputeItem> disputeItemSpecificationFactory;

    private UserFeignHelper userFeignHelper;


    public DisputeServiceImpl(DisputeItemRepository disputeItemRepository,
                              OrderItemRepository orderItemRepository,
                              UserServiceCodeGenerator codeGenerator,
                              DisputeMessageRepository disputeMessageRepository,
                              PercentageCalculateManager percentageCalculateManager,
                              OrderItemCommissionRepository orderItemCommissionRepository,
                              CancelOrderItemRepository cancelOrderItemRepository,
                              ProductCommissionRepository productCommissionRepository,
                              UserEmailService userEmailService, CancelOrderRepository cancelOrderRepository,
                              SpecificationFactory<DisputeItem> disputeItemSpecificationFactory,
                              UserFeignHelper userFeignHelper,
                              OrderDetailsRepository orderDetailsRepository) {
        this.disputeItemRepository = disputeItemRepository;
        this.orderItemRepository = orderItemRepository;
        this.codeGenerator = codeGenerator;
        this.disputeMessageRepository = disputeMessageRepository;
        this.percentageCalculateManager = percentageCalculateManager;
        this.orderItemCommissionRepository = orderItemCommissionRepository;
        this.cancelOrderItemRepository = cancelOrderItemRepository;
        this.productCommissionRepository = productCommissionRepository;
        this.userEmailService = userEmailService;
        this.cancelOrderRepository = cancelOrderRepository;
        this.disputeItemSpecificationFactory = disputeItemSpecificationFactory;
        this.userFeignHelper = userFeignHelper;
        this.orderDetailsRepository = orderDetailsRepository;
    }

    @Override
    public DisputeItemDTO raiseDispute(DisputeItemDTO disputeItemDTO) {
        OrderItem orderItem = orderItemRepository.findByIdAndDeletedFalse(disputeItemDTO.getOrderItemId());
        if (!orderItem.getOrderItemStatus().equalsIgnoreCase(OrderStatus.RETURNING_DECLINED.name())) {
            throw new RuntimeException("Order status must be RETURNING DECLINED status to raise dispute");
        }
        String disputeCode = codeGenerator.getDisputeCode();
        disputeItemDTO.setDisputeCode(disputeCode);
        disputeItemDTO.setStatus(Disputes.PENDING.name());
        disputeItemDTO.setMerchantId(orderItem.getMerchantId());
        DisputeItem disputeItem = DisputeItemTransformer.DISPUTE_DTO_TO_ENTITY.apply(disputeItemDTO);
        disputeItem.setPenalityPercentage(null);
        disputeItem.setOrderItem(orderItem);
        disputeItem.setOrderNo(orderItem.getOrderDetails().getOrderNo());
        DisputeItem newDisputeItem = disputeItemRepository
                .save(disputeItem);
        DisputeItemDTO newDisputeItemDTO = DisputeItemTransformer.DISPUTE_ENTITY_TO_DTO.apply(newDisputeItem);
        try {
            System.out.println("");
//            ResponseEntity<ApiResponse> merchantDetails = merchantClient.getMerchantByMerchantId(orderItem.getMerchantId());
//            if (org.apache.commons.lang3.StringUtils.isNotBlank(orderDetailsDTO.getOrderId())) {
//                OrderEventSource orderEventSource = new OrderEventSource(orderDetailsDTO.getOrderNo(), CommonConstant.ORDER_EVENT_PLACE_ORDER);
//                FcmUserNotificationDTO.Request fcmRequest = new FcmUserNotificationDTO.Request();
//                fcmRequest.setNotificationType(NotificationTypeEnum.ORDER_PLACE);
//                fcmRequest.setNotificationTypeId(orderDetailsDTO.getOrderId());
//                eventPublisher.publishEvent(new OrderEvent(orderEventSource, fcmRequest, user));
//            }
//            if (merchantDetails.getBody().getData() != null) {
//                ObjectMapper mapper = new ObjectMapper();
//                MerchantDTO merchantData = mapper.convertValue(merchantDetails.getBody().getData(), MerchantDTO.class);
//                FcmUserNotificationDTO.Request fcmRequest = new FcmUserNotificationDTO.Request();
//                fcmRequest.setNotificationType(NotificationTypeEnum.ORDER_RECEIVED);
//                fcmRequest.setNotificationTypeId(orderDetailsDTO.getOrderId());
//                User merchantUser = new User();
//                merchantUser.setId(merchantData.getId());
//                UserDeviceDetails merchantDeviceDetails = new UserDeviceDetails();
//                merchantDeviceDetails.setDeviceToken(merchantData.getDeviceToken());
//                merchantDeviceDetails.setDeviceType(merchantData.getDeviceType());
//                merchantUser.setUserDeviceDetails(merchantDeviceDetails);
//                eventPublisher.publishEvent(new OrderStatusEvent(fcmRequest, merchantUser));
//            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            return newDisputeItemDTO;
        }

    }

    @Override
    public void updateDisputeStatus(String disputeId, String status, Double penlity, Boolean disputeFlag) throws Exception {
        DisputeItem disputeItem = disputeItemRepository
                .findByIdAndDeletedFalse(disputeId).orElseThrow(() -> new RuntimeException("The Dispute id not found + " + disputeId));
        disputeItem.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));

        if (status != null) {
            if (status.equalsIgnoreCase(disputeItem.getStatus())) {
                throw new RuntimeException(String.format("The Dispute Item already updated with Status %s ", status));
            } else {
                if (penlity == null &&
                        status.equalsIgnoreCase(Disputes.CLOSED.name())) {
                    throw new RuntimeException("The Penality Amount must equal to zero or greater than zero");
                }
                disputeItem.setStatus(status.toUpperCase());
            }
        }

        if (disputeFlag && disputeItem.isDisputeFlag() == false) {
            disputeItem.setDisputeFlag(disputeFlag);
            userEmailService.sendEmailOnDisputeAlarm(disputeItem);
        }
        if (penlity != null) {
            disputeItem.setPenalityPercentage(penlity);
            OrderItem orderItem = disputeItem.getOrderItem();
            OrderItemCommission orderItemCommission = orderItem.getOrderItemCommission();
            orderItemCommission.setReturnFee(penlity);
            orderItemCommission = orderItemCommissionRepository.save(orderItemCommission);
            ProductCommission productCommission = productCommissionRepository.findByOrderItemAndDeletedFalse(orderItem);
            productCommission.setOrderStatus(orderItem.getOrderItemStatus());
            productCommissionRepository.save(productCommission);
            PercentageCharges percentageCharges = percentageCalculateManager.calculatePercentage(orderItem, OrderStatus.RETURNING.name());
            CancelOrderItem cancelOrderItem = cancelOrderItemRepository.findByOrderItemIdAndDeletedFalse(orderItem.getId());
            double charges = percentageCharges.getPercentageCharges();
            cancelOrderItem.setChargeAmount(charges);
            double refundAmount = percentageCharges.getBalanceAmount();
            cancelOrderItem.setRefundAmount(refundAmount);
            double originalAmount = percentageCharges.getOriginalAmount();
            cancelOrderItem.setOriginalAmount(originalAmount);
            cancelOrderItemRepository.save(cancelOrderItem);
            CancelOrder cancelOrder = cancelOrderRepository.findCancelOrdersByOrderId(cancelOrderItem
                    .getCancelOrder().getOrderDetails());
            double totalAmountCharged = 0d;
            double totalRefundAmount = 0d;
            List<CancelOrderItem> cancelOrderItems = cancelOrder.getCancelOrderItems();
            for (CancelOrderItem cancelOrderItem1 : cancelOrderItems) {
                totalRefundAmount = totalRefundAmount + cancelOrderItem1.getRefundAmount();
                totalAmountCharged = totalAmountCharged = cancelOrderItem1.getChargeAmount();
            }
            cancelOrder.setRefundAmount(totalRefundAmount);
            cancelOrder.setCharges(totalAmountCharged);
            cancelOrderRepository.save(cancelOrder);


        }
        disputeItemRepository.save(disputeItem);
    }

    @Override
    public DisputeItemDTO viewDispute(String disputeId, String id, String userType) {
        DisputeItem disputeItem = null;
        if (userType.equalsIgnoreCase(CommonConstant.USER_TYPE_ADMIN)) {
            disputeItem = disputeItemRepository.findByIdAndDeletedFalse(disputeId).orElseThrow(()
                    -> new RuntimeException("Dispute Id not found " + disputeId));
        } else if (userType.equalsIgnoreCase(CommonConstant.USER_TYPE_MERCHANT)) {
            disputeItem = disputeItemRepository.findByIdAndDeletedFalseAndMerchantId(disputeId, id).orElseThrow(()
                    -> new RuntimeException("Dispute Id not found " + disputeId));
        } else if (userType.equalsIgnoreCase(CommonConstant.USER_TYPE_CUSTOMER)) {
            disputeItem = disputeItemRepository.findByIdAndDeletedFalseAndUserId(disputeId, id).orElseThrow(()
                    -> new RuntimeException("Dispute Id not found " + disputeId));
        }
        DisputeItemDTO disputeItemDTO = DisputeItemTransformer.DISPUTE_ENTITY_TO_DTO.apply(disputeItem);
        // set UserDto
        UserDTO userDTO = new UserDTO();
        Optional<User> allUser = userRepository.findById(disputeItem.getUserId());
        userDTO.setFirstName(allUser.get().getFirstName());
        userDTO.setEmail(allUser.get().getEmail());
        userDTO.setContactNumber(allUser.get().getContactNumber());

        // set MerchantDto
        MerchantDTO merchantDTO = new MerchantDTO();
        ResponseEntity<ApiResponse> merchantByMerchantId = merchantClient.getMerchantByMerchantId(disputeItem.getMerchantId());
        ApiResponse apiResponse = merchantByMerchantId.getBody();
        ObjectMapper mapper = new ObjectMapper();
        MerchantDTO merchantData = mapper.convertValue(apiResponse.getData(), MerchantDTO.class);
        merchantDTO.setMerchantId(merchantData.getMerchantId());
        merchantDTO.setEmail(merchantData.getEmail());
        merchantDTO.setBusinessName(merchantData.getBusinessName());
        merchantDTO.setMerchantType(merchantData.getMerchantType());

        // set OrderItemDTo
        OrderDetailsDTO orderDetailsDTO = new OrderDetailsDTO();
        OrderItem orderById = orderItemRepository.findByIdAndDeletedFalse(disputeItem.getOrderItem().getId());
        orderDetailsDTO.setOrderNo(orderById.getOrderDetails().getOrderNo());
        orderDetailsDTO.setOrderStatus(orderById.getOrderItemStatus());
        orderDetailsDTO.setTotalPrice(orderById.getTotalPrice());

        // set ProductDTo


        List<DisputeMessage> disputeMessageList = disputeMessageRepository
                .findAllMessageByDisputeItem(Sort.by(Sort.Direction.DESC, "createdAt"), disputeItem);
        if (disputeMessageList != null && disputeMessageList.size() > 0) {
            List<DisputeMessageDTO> disputeMessageDTOList = new ArrayList<>();
            for (DisputeMessage disputeMessage : disputeMessageList) {
                DisputeMessageDTO disputeMessageDTO = DisputeItemTransformer.DISPUTE_MSG_ENTITY_TO_DTO.apply(disputeMessage);
                disputeMessageDTOList.add(disputeMessageDTO);
            }
            disputeItemDTO.setDisputeMessages(disputeMessageDTOList);
        }

        disputeItemDTO.setMerchantDTO(merchantDTO);
        disputeItemDTO.setOrderDetailsDTO(orderDetailsDTO);
        disputeItemDTO.setUserDTO(userDTO);
        return disputeItemDTO;
    }

    @Override
    public void sendDisputeMessage(DisputeMessageDTO disputeMessageDTO) {
        DisputeItem disputeItem = disputeItemRepository.findByIdAndDeletedFalse(disputeMessageDTO.getDisputeId())
                .orElseThrow(() -> new RuntimeException("Dispute id not found"));
        if (disputeItem.getStatus().equalsIgnoreCase(Disputes.PENDING.name())) {
            disputeItem.setStatus(Disputes.OPEN.name());
            disputeItemRepository.save(disputeItem);
        }
        DisputeMessage disputeMessage = DisputeItemTransformer.DISPUTE_MSG_DTO_TO_ENTITY.apply(disputeMessageDTO);
        disputeMessage.setDisputeItem(disputeItem);
        disputeMessageRepository.save(disputeMessage);
    }

    @Override
    public List<DisputeItemDTO> getAllDisputeMessages() {
        return null;
    }

    @Override
    public PageItem<DisputeItemDTO> getAllDispute(PagedItemInfo pagedInfo, DisputeFilterRequest filterRequest) {
        List<DisputeItemDTO> disputeItemDTOS = new ArrayList<>();
        final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(OrderDetails.class, pagedInfo);
        GenericSpecificationsBuilder<DisputeItem> builder = new GenericSpecificationsBuilder<>();
        prepareDisputeSearchQuery(filterRequest, builder);

        Page<DisputeItem> pagedResult = disputeItemRepository.findAll(builder.build(), pageRequest);
        List<DisputeItem> disputeItems = pagedResult.getContent();
        for (DisputeItem disputeItem : disputeItems) {
            DisputeItemDTO disputeItemDTO = DisputeItemTransformer.DISPUTE_ENTITY_TO_DTO.apply(disputeItem);
            List<DisputeMessage> disputeMessageList = disputeMessageRepository
                    .findAllMessageByDisputeItem(Sort.by(Sort.Direction.DESC, "createdAt"), disputeItem);
            if (disputeMessageList != null && disputeMessageList.size() > 0) {
                List<DisputeMessageDTO> disputeMessageDTOList = new ArrayList<>();
                for (DisputeMessage disputeMessage : disputeMessageList) {
                    DisputeMessageDTO disputeMessageDTO = DisputeItemTransformer.DISPUTE_MSG_ENTITY_TO_DTO.apply(disputeMessage);
                    disputeMessageDTOList.add(disputeMessageDTO);
                }
                disputeItemDTO.setDisputeMessages(disputeMessageDTOList);
            }
            disputeItemDTOS.add(disputeItemDTO);
        }
        return new PageItem<>(pagedResult.getTotalPages(), pagedResult.getTotalElements(), disputeItemDTOS, pagedInfo.page,
                pagedInfo.items);
    }

    private void prepareDisputeSearchQuery(DisputeFilterRequest filterRequest, GenericSpecificationsBuilder<DisputeItem> builder) {

        builder.with(disputeItemSpecificationFactory.isEqual("deleted", false));
        if (StringUtils.isNotBlank(filterRequest.getMerchantId())) {
            builder.with(disputeItemSpecificationFactory.isEqual("merchantId", filterRequest.getMerchantId()));
        }
        if (StringUtils.isNotBlank(filterRequest.getStatus())) {

            builder.with(disputeItemSpecificationFactory.isEqual("status", filterRequest.getStatus()));
        }
        if (StringUtils.isNotBlank(filterRequest.getUserId())) {
            builder.with(disputeItemSpecificationFactory.like("userId", filterRequest.getUserId()));
        }
        if (filterRequest.getDisputeFlag() != null) {
            builder.with(disputeItemSpecificationFactory.like("disputeFlag", filterRequest.getDisputeFlag()));
        }
        if (filterRequest.getStartDate() != null) {
            builder.with(disputeItemSpecificationFactory.isGreaterThan("createdAt", filterRequest.getStartDate().atStartOfDay()));
        }
        if (filterRequest.getEndDate() != null) {
            builder.with(disputeItemSpecificationFactory.isLessThan("createdAt", filterRequest.getEndDate().plusDays(1).atStartOfDay()));
        }

        if (StringUtils.isNotBlank(filterRequest.getOrderNo())) {
//            OrderDetails orderDetails = orderDetailsRepository.findByOrderNoAndDeletedFalse(filterRequest.getOrderNo());
            builder.with(disputeItemSpecificationFactory.isEqual("orderNo", filterRequest.getOrderNo()));
        }

        if (StringUtils.isNotBlank(filterRequest.getDisputeCode())) {
//            OrderDetails orderDetails = orderDetailsRepository.findByOrderNoAndDeletedFalse(filterRequest.getOrderNo());
            builder.with(disputeItemSpecificationFactory.isEqual("disputeCode", filterRequest.getDisputeCode()));
        }
    }

    @Override
    public DisputeItem findDisputeById(String disputeId) {
        return disputeItemRepository.findByIdAndDeletedFalse(disputeId).orElseThrow(() -> new ActorPayException("Dispute not found"));
    }
}

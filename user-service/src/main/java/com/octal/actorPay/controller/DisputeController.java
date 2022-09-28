package com.octal.actorPay.controller;

import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.constants.Disputes;
import com.octal.actorPay.constants.OrderStatus;
import com.octal.actorPay.constants.PaymentMethod;
import com.octal.actorPay.dto.*;
import com.octal.actorPay.dto.request.DisputeFilterRequest;
import com.octal.actorPay.entities.*;
import com.octal.actorPay.service.DisputeService;
import com.octal.actorPay.service.OrderService;
import com.octal.actorPay.service.UploadService;
import com.octal.actorPay.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/dispute")
public class DisputeController extends PagedItemsController {


    private DisputeService disputeService;
    private UploadService uploadService;
    private UserService userService;
    private OrderService orderService;

    public DisputeController(DisputeService disputeService,
                             UploadService uploadService, UserService userService, OrderService orderService) {
        this.disputeService = disputeService;
        this.uploadService = uploadService;
        this.userService = userService;
        this.orderService = orderService;
    }

    @PostMapping("/raise")
    public ResponseEntity<ApiResponse> raiseDispute(@RequestPart(value = "file", required = false) MultipartFile file,
                                                    @Valid @RequestPart("dispute") DisputeItemDTO disputeItemDTO,
                                                    HttpServletRequest request) throws Exception {

        String userName = request.getHeader("userName");
        User user = userService.getUserByEmailId(userName);
        if (file != null) {
            String s3Path = uploadService.uploadFileToS3(file, "dispute/" + disputeItemDTO.getOrderItemId());
            disputeItemDTO.setImagePath(s3Path);
        }
        disputeItemDTO.setUserId(user.getId());
        DisputeItemDTO newDisputeItemDTO = disputeService.raiseDispute(disputeItemDTO);
        if (newDisputeItemDTO != null) {
            return new ResponseEntity<>(
                    new ApiResponse("Dispute Details", newDisputeItemDTO,
                            String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(
                    new ApiResponse("Error while creating Dispute", newDisputeItemDTO,
                            String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{disputeId}")
    public ResponseEntity<ApiResponse> updateDispute(@PathVariable("disputeId") String disputeId,
                                                     @RequestParam(name = "status", required = false) String status,
                                                     @RequestParam(name = "userType", required = false) String userType,
                                                     @RequestParam(name = "penality", required = false) Double penality,
                                                     @RequestParam(name = "disputeFlag", required = false, defaultValue = "false") Boolean disputeFlag)
            throws Exception {

        List<String> availableStatus = Arrays.asList(Disputes.OPEN.name(), Disputes.CLOSED.name());
        if (status != null && (!availableStatus.contains(status))) {
            throw new RuntimeException("Status can update to either " +
                    availableStatus.get(0) + " or " + availableStatus.get(1));
        }

        if (StringUtils.isBlank(userType) && StringUtils.isNotBlank(status)) {
            throw new RuntimeException("The Current user is not allowed to update the Dispute Status");
        }
        if (StringUtils.isBlank(userType) && penality != null) {
            throw new RuntimeException("Customer can't update the Penality");
        }

        disputeService.updateDisputeStatus(disputeId, status, penality, disputeFlag);

        DisputeItem disputeItem = disputeService.findDisputeById(disputeId);
        if (penality >= 0) {
            OrderItem orderItem = disputeItem.getOrderItem();
            orderItem.setOrderItemStatus(OrderStatus.RETURNING_ACCEPTED.name());
            orderService.saveOrderItem(orderItem);
        }
        OrderDetails orderDetails = orderService.findOrderByNo(disputeItem.getOrderNo());
        orderService.setGlobalOrderStatus(orderDetails.getId());
        CancelOrder cancelOrder = orderService.findCancelOrdersByOrderId(orderDetails);
        Double originalAmount = cancelOrder.getOriginalAmount();
        if (orderDetails.getPaymentMethod().equalsIgnoreCase(PaymentMethod.wallet.name())) {
            // Fund transfer need to implement for wall based payment
            System.out.println("Fund Transfer need to implement for Wallet based Payment");
        } else {
            orderService.processRefund(originalAmount, orderDetails);
        }
        return new ResponseEntity<>(
                new ApiResponse("Dispute Updated Successfully", "",
                        String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);

    }

    @PostMapping("/send/message")
    public ResponseEntity<ApiResponse> sendMessage(@Valid @RequestBody DisputeMessageDTO disputeMessageDTO,
                                                   HttpServletRequest request) {
        String userName = request.getHeader("userName");
        if (StringUtils.isBlank(disputeMessageDTO.getPostedById()) && StringUtils.isBlank(disputeMessageDTO.getUserType())) {
            User user = userService.getUserByEmailId(userName);
            disputeMessageDTO.setPostedById(user.getId());
            disputeMessageDTO.setPostedByName(userName);
            disputeMessageDTO.setUserType(CommonConstant.USER_TYPE_CUSTOMER);
        }
        disputeService.sendDisputeMessage(disputeMessageDTO);
        return new ResponseEntity<>(
                new ApiResponse("Message sent Successfully", "",
                        String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping("/list/paged")
    public ResponseEntity<ApiResponse> getAllDispute(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                                     @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                     @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                                     @RequestParam(name = "asc", defaultValue = "false") boolean asc,
                                                     @RequestParam(name = "userType", required = false) String userType,
                                                     @RequestBody DisputeFilterRequest filterRequest, HttpServletRequest request) {
        final PagedItemInfo pagedInfo = createPagedInfo(pageNo, pageSize, sortBy, asc, null);
        if (StringUtils.isBlank(userType)) {
            String userName = request.getHeader("userName");
            User user = userService.getUserByEmailId(userName);
            userType = CommonConstant.USER_TYPE_CUSTOMER;
            filterRequest.setUserId(user.getId());
        }
        PageItem<DisputeItemDTO> pageResult = disputeService.getAllDispute(pagedInfo, filterRequest);
        return new ResponseEntity<>(
                new ApiResponse("Message sent Successfully", pageResult,
                        String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping("/get/{disputeId}")
    public ResponseEntity<ApiResponse> getDisputeById(
            @PathVariable("disputeId") String disputeId,
            @RequestParam(value = "id", required = false) String id,
            @RequestParam(value = "userType", required = false) String userType,
            HttpServletRequest request) {
        if (StringUtils.isBlank(id)) {
            String userName = request.getHeader("userName");
            User user = userService.getUserByEmailId(userName);
            id = user.getId();
            userType = CommonConstant.USER_TYPE_CUSTOMER;
        }

        DisputeItemDTO disputeItemDTO = disputeService.viewDispute(disputeId, id, userType);
        if (disputeItemDTO != null) {
            return new ResponseEntity<>(
                    new ApiResponse("Dispute Details", disputeItemDTO,
                            String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(
                    new ApiResponse(String.format("Dispute not found for given Id %s ", disputeId), null,
                            String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }
}

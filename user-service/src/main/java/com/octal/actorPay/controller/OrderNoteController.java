package com.octal.actorPay.controller;

import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.OrderNoteDTO;
import com.octal.actorPay.dto.UserDTO;
import com.octal.actorPay.dto.request.OrderNoteFilterRequest;
import com.octal.actorPay.entities.OrderDetails;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.exceptions.ExceptionUtils;
import com.octal.actorPay.service.OrderService;
import com.octal.actorPay.service.UserService;
import feign.FeignException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orderNotes")
public class OrderNoteController {

    private OrderService orderService;
    private UserService userService;

    public OrderNoteController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    //    ResponseEntity<ApiResponse> addOrderNote(@RequestBody OrderNoteDTO orderNoteDTO,
//                                             @RequestParam(name = "userType") String userType,
//                                             @RequestParam(name = "id") String id,
//                                             @RequestParam(name = "userName") String userName);
    @PostMapping("/post")
    public ResponseEntity<ApiResponse>
    addOrderNote(@RequestBody OrderNoteDTO orderNoteDTO,
                 @RequestParam(name = "userType", required = false) String userType,
                 @RequestParam(name = "id", required = false) String id,
                 @RequestParam(name = "userName", required = false) String userName, HttpServletRequest request) {

        String loggedInUser = request.getHeader("userName");
        User user;
        try {
            user = userService.getUserByEmailId(loggedInUser);
        }catch (Exception e){
            OrderDetails orderDetails = orderService.findByOrderNo(orderNoteDTO.getOrderNo(), userType, id);
            UserDTO userDto = userService.getUserById(orderDetails.getCustomer().getId());
            user = userService.getUserByEmailId(userDto.getEmail());
        }
        try {
            if (StringUtils.isBlank(userType) && StringUtils.isBlank(id)) {
                userType = CommonConstant.USER_TYPE_CUSTOMER;
                id = user.getId();
                userName = user.getEmail();
            }
            OrderDetails orderDetails = orderService.findByOrderNo(orderNoteDTO.getOrderNo(), userType, id);
//            if (orderDetails == null) {
//                throw new RuntimeException("Invalid Order Details ");
//            }
            orderNoteDTO.setNoteFlowType(CommonConstant.ORDER_NOTE_FLOW_TYPE);
            OrderNoteDTO newOrderNoteDTO = orderService.saveOrderNote(orderNoteDTO, orderDetails,
                    userType, id,true);
            if (newOrderNoteDTO != null) {
                return new ResponseEntity<ApiResponse>(new ApiResponse("Order Note Details: ",
                        newOrderNoteDTO, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
            } else {
                return new ResponseEntity<ApiResponse>(new ApiResponse("Error while creating Order Note ",
                        newOrderNoteDTO, String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
            }
        } catch (FeignException fe) {
            ApiResponse apiResponse = ExceptionUtils.parseFeignExceptionMessage(fe);
            return new ResponseEntity<ApiResponse>(new ApiResponse(apiResponse.getMessage(), apiResponse.getData(),
                    String.valueOf(HttpStatus.OK.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<ApiResponse>(new ApiResponse(String.format(
                    "Error while creating Order Note ,%s", e.getMessage()),
                    null, String.valueOf(HttpStatus.OK.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<ApiResponse>(new ApiResponse(String.format(
                    "Error while creating Order Note ,%s", e.getMessage()),
                    null, String.valueOf(HttpStatus.OK.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/list/paged")
    public ResponseEntity<ApiResponse> getAllOrderNote(@RequestParam(name = "userType", required = false) String userType,
                                                       @RequestParam(name = "id", required = false) String id,
                                                       @RequestParam(name = "orderNo",required = false) String orderNo,
                                                       HttpServletRequest request) {

        String loggedInUser = request.getHeader("userName");
        OrderNoteFilterRequest filterRequest = new OrderNoteFilterRequest();
        filterRequest.setOrderNo(orderNo);
        User user = userService.getUserByEmailId(loggedInUser);
        if (StringUtils.isBlank(userType) && StringUtils.isBlank(id)) {
            userType = CommonConstant.USER_TYPE_CUSTOMER;
            filterRequest.setUserId(user.getId());
        }
        if(userType.equalsIgnoreCase(CommonConstant.USER_TYPE_MERCHANT)) {
            filterRequest.setMerchantId(id);
        }

        List<OrderNoteDTO> orderNoteDTOList = orderService.getAllOrderNotes(userType,filterRequest);
       List<OrderNoteDTO> sortedList =  orderNoteDTOList.stream().sorted(Comparator.comparing(
                OrderNoteDTO::getCreatedAt,Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());

        return new ResponseEntity<>(new ApiResponse("Order Note Deatils",
                sortedList, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }


}

package com.octal.actorPay.client;

import com.octal.actorPay.configs.FeignSupportConfig;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.OrderNoteDTO;
import com.octal.actorPay.dto.request.OrderFilterRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import static com.octal.actorPay.constants.EndPointConstants.UserServiceConstants.FETCH_USER_BASE_URL;

@FeignClient(name = "user-service-orders", url = FETCH_USER_BASE_URL,
        configuration = {FeignSupportConfig.class})
@Service
public interface MerchantOrderClient {

    @PostMapping("/orders/list/paged")
    ResponseEntity<ApiResponse> viewAllOrder(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                             @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                             @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                             @RequestParam(name = "asc", defaultValue = "false") boolean asc,
                                             @RequestBody OrderFilterRequest filterRequest);

    @PostMapping("/orders/list/report")
    ResponseEntity<ApiResponse> getAllOrdersForReport(@RequestBody OrderFilterRequest filterRequest);

    @PostMapping(value = "/orders/cancel/{orderNo}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<ApiResponse> cancelOrder(@PathVariable(name = "orderNo") String orderNo,
                                            @Valid @RequestPart(name = "cancelOrder") String cancelOrder,
                                            @RequestPart(name = "file", required = false)
                                                    MultipartFile file,
                                            @RequestParam(value = "userType",required = false) String userType,
                                            @RequestParam(value = "id",required = false) String id);

    @GetMapping("/orders/{orderNo}")
    ResponseEntity<ApiResponse> viewOrder(@PathVariable(name = "orderNo") String orderNo,
                                          @RequestParam(name = "userType",required = false) String userType,
                                          @RequestParam(name = "id",required = false) String id);

    @PutMapping("/orders/status")
    ResponseEntity<ApiResponse> updateOrderStatus(@RequestParam("status") String status,
                                                  @RequestParam("orderNo") String orderNo,
                                                  @RequestParam(value = "userType", required = false) String userType,
                                                  @RequestParam(value = "id",required = false) String id,
                                                  @RequestBody(required = false) OrderNoteDTO orderNote);

    @PostMapping(value = "/orders/cancel/test", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    ResponseEntity<ApiResponse> testUpload(@RequestPart(name = "file") MultipartFile file,
                                           @RequestPart(name = "cancelOrder") String cancelOrder,
                                           @RequestParam("merchantId") String merchantId);

    @PostMapping("/orderNotes/post")
    ResponseEntity<ApiResponse> addOrderNote(@RequestBody OrderNoteDTO orderNoteDTO,
                                             @RequestParam(name = "userType") String userType,
                                             @RequestParam(name = "id") String id,
                                             @RequestParam(name = "userName") String userName);

    @GetMapping("/orderNotes/list/paged")
    ResponseEntity<ApiResponse> getAllOrderNote(
            @RequestParam(name = "orderNo",required = false) String orderNo,
            @RequestParam(name = "userType", required = false) String userType,
            @RequestParam(name = "id", required = false) String id);
}

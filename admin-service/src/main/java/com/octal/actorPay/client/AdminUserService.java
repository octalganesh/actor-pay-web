package com.octal.actorPay.client;

import com.octal.actorPay.constants.EndPointConstants;
import com.octal.actorPay.dto.*;
import com.octal.actorPay.dto.request.BankTransactionFilterRequest;
import com.octal.actorPay.dto.request.DisputeFilterRequest;
import com.octal.actorPay.dto.request.EkycUpdateRequest;
import com.octal.actorPay.dto.request.ProductCommissionFilterRequest;
import com.octal.actorPay.dto.request.UserFilterRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import static com.octal.actorPay.constants.EndPointConstants.USER_MICROSERVICE;
import static com.octal.actorPay.constants.EndPointConstants.UserServiceConstants.FETCH_USER_BASE_URL;

@FeignClient(name = USER_MICROSERVICE, url = FETCH_USER_BASE_URL)
@Service
public interface AdminUserService {

    // @param userDTO - object of userDto
    @RequestMapping(value = EndPointConstants.UserServiceConstants.ADD_USER_API, method = RequestMethod.POST)
    ApiResponse addUser(@RequestBody UserDTO userDTO);

    @RequestMapping(value = EndPointConstants.UserServiceConstants.UPDATE_USER_API, method = RequestMethod.PUT)
    ApiResponse updateUser(@RequestBody @Valid UserDTO userDTO);

    @RequestMapping(value = EndPointConstants.UserServiceConstants.GET_USER_DETAILS_URL, method = RequestMethod.GET)
    ApiResponse getUserById(@PathVariable("id") String id);

    @RequestMapping(value = EndPointConstants.UserServiceConstants.DELETE_USER_BY_IDS_API, method = RequestMethod.DELETE)
    void deleteUsersByIds(@RequestBody Map<String, List<String>> userIds);

    //@RequestMapping(value = EndPointConstants.UserServiceConstants.GET_ALL_USERS_URL, method = RequestMethod.POST)
    @PostMapping("/users/get/all/user/paged")
    ResponseEntity<ApiResponse> getAllUsersPaged(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                 @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                 @RequestParam(name = "asc", defaultValue = "false") boolean asc,
                                 //@RequestParam(defaultValue = "customer", required = false) String userType,
                                 @RequestBody UserFilterRequest userFilterRequest);

    @RequestMapping(value = EndPointConstants.UserServiceConstants.CHANGE_USER_STATUS, method = RequestMethod.PUT)
    ApiResponse changeUserStatus(@RequestParam(name = "id") String userId,
                                 @RequestParam(name = "status") Boolean status);

    @PostMapping("/productCommission/list/paged")
    ResponseEntity<ApiResponse> getProductAllCommissions(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                         @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                                         @RequestParam(name = "asc", defaultValue = "false") boolean asc,
                                                         @RequestBody ProductCommissionFilterRequest filterRequest);

    @GetMapping("/admin-total-commission/{orderStatus}")
    ResponseEntity<ApiResponse> getAdminTotalCommissionByOrderStatus(@PathVariable("orderStatus") String orderStatus);

    @PutMapping("/productCommission/update")
    ResponseEntity<ApiResponse> updateProductCommission(@RequestParam("status") String settlementStatus,@RequestParam("ids") List<String> ids);

    @PostMapping("/orderNotes/post")
    ResponseEntity<ApiResponse> addOrderNote(@RequestBody OrderNoteDTO orderNoteDTO,
                 @RequestParam(name = "userType") String userType,
                 @RequestParam(name = "id") String id,
                 @RequestParam(name = "userName") String userName);

    @GetMapping("/orderNotes/list/paged")
    ResponseEntity<ApiResponse> getAllOrderNote(
            @RequestParam(name = "orderNo",required = false) String orderNo,
            @RequestParam(name = "userType", required = false) String userType);

    @PostMapping("/dispute/send/message")
    public ResponseEntity<ApiResponse> sendMessage(@RequestBody DisputeMessageDTO disputeMessageDTO);

    @GetMapping("/users/getAllUsersCount")
    public ResponseEntity<ApiResponse> getAllUsersCount();

    @PutMapping("/dispute/update/{disputeId}")
    public ResponseEntity<ApiResponse> updateDispute(@PathVariable("disputeId") String disputeId,
                                                     @RequestParam(name = "status",required = false) String status,
                                                     @RequestParam(name = "userType",required = false) String userType,
                                                     @RequestParam(name = "penality",required = false) Double penality);

    @PostMapping("/dispute/list/paged")
    public ResponseEntity<ApiResponse> getAllDispute(@RequestParam(name = "pageNo",defaultValue = "0") Integer pageNo,
                                                     @RequestParam(name = "pageSize",defaultValue = "10") Integer pageSize,
                                                     @RequestParam(name = "createdAt",defaultValue = "createdAt") String sortBy,
                                                     @RequestParam(name = "asc",defaultValue = "false") boolean asc,
                                                     @RequestParam(name = "userType",required = false) String userType,
                                                     @RequestBody DisputeFilterRequest filterRequest);

    @GetMapping("/dispute/get/{disputeId}")
    public ResponseEntity<ApiResponse> getDisputeById(
            @PathVariable(name = "disputeId") String disputeId,
            @RequestParam(value = "id",required = false) String id,
            @RequestParam(value = "userType",required = false) String userType);

    @PutMapping("/users/ekyc/update")
    public ResponseEntity<ApiResponse>
    updateEkycStatus(@RequestBody EkycUpdateRequest updateRequest);

    @GetMapping("/users/ekyc/{userId}/{docType}")
    public ResponseEntity<ApiResponse> getUserKYCByDocType(@PathVariable(name = "docType") String docType,
                                                      @PathVariable(name = "userId") String userId);



    @PostMapping("users/ekyc/get/by/key")
    public ResponseEntity<ApiResponse> getEkycByKey( @RequestParam(defaultValue = "0") Integer pageNo,
                                                     @RequestParam(defaultValue = "10") Integer pageSize,
                                                     @RequestParam(defaultValue = "createdAt") String sortBy,
                                                     @RequestParam(defaultValue = "false") boolean asc,
                                                     @RequestBody EkycFilterRequest ekycFilterRequest) ;

    @GetMapping("/settlement/{status}")
    ResponseEntity<ApiResponse> findBySettlementStatus(@PathVariable(name = "status") String settlementStatus);

    @PostMapping("/v1/bank/list/transactions")
    public ResponseEntity<ApiResponse> searchBankTransaction(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                                             @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                             @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                                             @RequestParam(name = "asc", defaultValue = "false") boolean asc,
                                                             @RequestParam(name = "userType", required = false, defaultValue = "customer") String userType,
                                                             @RequestBody BankTransactionFilterRequest filterRequest);

    @PostMapping("/orders/applyReferral/{orderNo}")
    ApiResponse applyReferral( @PathVariable("orderNo") String orderNo,
                               @RequestBody ReferralSettingDTO referralSettingDTO);

    @GetMapping("/v1/referral/history/paged")
    public ResponseEntity<ApiResponse> getReferralHistoryByUserId(@RequestParam(name = "userId", required = false, defaultValue = "") String userId,
                                                                  @RequestParam(defaultValue = "0") Integer pageNo,
                                                                  @RequestParam(defaultValue = "10") Integer pageSize,
                                                                  @RequestParam(defaultValue = "createdAt") String sortBy,
                                                                  @RequestParam(defaultValue = "true") boolean asc);

    @PostMapping(value = "/v1/bank/list/transactions/report")
    public ResponseEntity<ApiResponse> getAllTransactionForReport(@RequestBody BankTransactionFilterRequest filterRequest);

}
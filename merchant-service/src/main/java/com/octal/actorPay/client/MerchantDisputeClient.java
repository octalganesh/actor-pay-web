package com.octal.actorPay.client;

import com.octal.actorPay.configs.FeignSupportConfig;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.DisputeMessageDTO;
import com.octal.actorPay.dto.request.DisputeFilterRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static com.octal.actorPay.constants.EndPointConstants.UserServiceConstants.FETCH_USER_BASE_URL;

@FeignClient(name = "user-service-dispute", url = FETCH_USER_BASE_URL,
        configuration = {FeignSupportConfig.class})
@Service
public interface MerchantDisputeClient {


    @PostMapping("/dispute/send/message")
    public ResponseEntity<ApiResponse> sendMessage(@RequestBody DisputeMessageDTO disputeMessageDTO);

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
}

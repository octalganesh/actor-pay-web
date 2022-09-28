package com.octal.actorPay.controller;

import com.octal.actorPay.dto.*;
import com.octal.actorPay.dto.request.MerchantFilterRequest;
import com.octal.actorPay.dto.request.SubmerchantFilterRequest;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.service.MerchantService;
import com.octal.actorPay.service.SubMerchantService;
import com.octal.actorPay.utils.CommonUtils;
import com.octal.actorPay.utils.ResponseUtils;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/submerchant")
public class SubMerchantController extends BaseController {

    @Autowired
    private SubMerchantService subMerchantService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private MerchantService merchantService;

//    @Secured("ROLE_SUBMERCHANT_CREATE")
    @PostMapping("/create")
    public ResponseEntity<?> createSubmerchant(@RequestBody SubMerchantDTO subMerchantDTO, final HttpServletRequest request) throws Exception {
        try {
            String currentUser = request.getHeader("userName");
            User registered = subMerchantService.create(subMerchantDTO, currentUser);
//            eventPublisher.publishEvent(new RegistrationCompleteEvent(registered, Locale.ENGLISH, getAppUrl(request)));
            return new ResponseEntity<>(new ApiResponse("Submerchant Account created successfully. Activation email sent to Submerchant Email.", registered.getId(), String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        } catch (FeignException fe) {
            fe.printStackTrace();
            throw new RuntimeException(fe.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), null, String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

//    @Secured("ROLE_SUBMERCHANT_UPDATE")
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody SubMerchantDTO merchantDTO, final HttpServletRequest request) {
        try {
            subMerchantService.update(merchantDTO, CommonUtils.getCurrentUser(request));
            return new ResponseEntity<>(ResponseUtils.ActorPayResponse("Submerchant account updated successfully", null, HttpStatus.OK), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(ResponseUtils.ActorPayResponse(e.getMessage(), null, HttpStatus.OK), HttpStatus.OK);
        }
    }

//    @Secured("ROLE_SUBMERCHANT_GET_BY_ID")
    @GetMapping(value = "/read/by/id/{id}")
    public ResponseEntity getUserById(@PathVariable("id") String id, final HttpServletRequest request) {
        MerchantDTO merchantDTO = merchantService.getMerchantDetails(id, CommonUtils.getCurrentUser(request));
        return new ResponseEntity<>(ResponseUtils.ActorPayResponse("Submerchant details", merchantDTO, HttpStatus.OK), HttpStatus.OK);
    }

 /*   @PostMapping(value = "/get/all/paged")
    public ResponseEntity getSubMerchantByPrimary(@Param("userId") String merchantId)  {
        MerchantDTO merchantDTO = merchantService.getMerchantDetailsByMerchantId(merchantId);
        return new ResponseEntity<>(ResponseUtils.ActorPayResponse("Sub-Merchant details ", null, HttpStatus.OK), HttpStatus.OK);
    }*/

    @PostMapping(value = "/get/all/paged")
    public ResponseEntity getSubMerchantByPrimary(@RequestParam(defaultValue = "0") Integer pageNo,
                                                  @RequestParam(defaultValue = "10") Integer pageSize,
                                                  @RequestParam(defaultValue = "createdAt") String sortBy,
                                                  @RequestParam(defaultValue = "false") boolean asc,
                                                  @RequestBody MerchantFilterRequest filterRequest)  {
        final PagedItemInfo pagedInfo = createPagedInfo(pageNo, pageSize, sortBy, asc, null);
        PageItem<MerchantDTO> merchantDTOList = merchantService.getAllMerchantsPaged(pagedInfo , filterRequest);
        return new ResponseEntity<>(ResponseUtils.ActorPayResponse("Sub-Merchant details ", merchantDTOList, HttpStatus.OK), HttpStatus.OK);
    }
    @PostMapping(value = "/get/all")
    public ResponseEntity getSubMerchantByMerchant(@RequestParam(defaultValue = "0") Integer pageNo,
                                                  @RequestParam(defaultValue = "10") Integer pageSize,
                                                  @RequestParam(defaultValue = "createdAt") String sortBy,
                                                  @RequestParam(defaultValue = "false") boolean asc,
                                                  @RequestBody MerchantFilterRequest filterRequest,final HttpServletRequest request)  {
        try {
            String userName = CommonUtils.getCurrentUser(request);
            final PagedItemInfo pagedInfo = createPagedInfo(pageNo, pageSize, sortBy, asc, null);
            PageItem<MerchantDTO> merchantDTOList = merchantService.getAllSubMerchantsByMerchant(pagedInfo , filterRequest,userName);
            return new ResponseEntity<>(ResponseUtils.ActorPayResponse("Sub-Merchant details ", merchantDTOList, HttpStatus.OK), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(ResponseUtils.ActorPayResponse(e.getMessage(), null, HttpStatus.NO_CONTENT), HttpStatus.OK);
        }
    }


//    @Secured("ROLE_SUBMERCHANT_DELETE_BY_ID")
    @DeleteMapping("/delete/by/ids")
    public ResponseEntity<ApiResponse> deleteUserByIds(@RequestBody Map<String, List<String>> userIds, final HttpServletRequest request) throws InterruptedException {
        Map data = subMerchantService.delete(userIds.get("ids"), CommonUtils.getCurrentUser(request));
        return new ResponseEntity<>(ResponseUtils.ActorPayResponse("Sub merchant deleted successfully.", data, HttpStatus.OK), HttpStatus.OK);
    }

    private String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    @Secured("ROLE_SUBMERCHANT_ASSOCIATE_TO_OUTLET")
    @PostMapping("/associate/merchant/{merchantId}/outlet/{outletId}")
    public ResponseEntity<ApiResponse> associateMerchantToOutlet(@PathVariable(name = "outletId") String outletId,
                                                                 @PathVariable(name = "merchantId") String merchantId) {
        MerchantOutletDTO merchantOutletDTO = subMerchantService.associateMerchantToOutlet(merchantId, outletId);
        return new ResponseEntity<>(ResponseUtils.ActorPayResponse("SubMerchant Associated from Outlet ", merchantOutletDTO, HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_SUBMERCHANT_DISASSOCIATE_TO_OUTLET")
    @DeleteMapping("/disassociate/merchant/{merchantId}/outlet/{outletId}")
    public ResponseEntity<ApiResponse> disassociateMerchantFromOutlet(@PathVariable(name = "outletId") String outletId, @PathVariable(name = "merchantId") String merchantId) {
        subMerchantService.disassociateMerchantFromOutlet(merchantId, outletId);
        return new ResponseEntity<>(ResponseUtils.ActorPayResponse("SubMerchant Disassociated from Outlet ", null, HttpStatus.OK), HttpStatus.OK);
    }
}




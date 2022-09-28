package com.octal.actorPay.controller;

import com.google.gson.Gson;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.ApplyPromoCodeResponse;
import com.octal.actorPay.dto.CartDTO;
import com.octal.actorPay.dto.CartItemDTO;
import com.octal.actorPay.dto.request.OfferFilterRequest;
import com.octal.actorPay.dto.request.PromoCodeFilter;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.entities.UserDeviceDetails;
import com.octal.actorPay.feign.clients.AdminClient;
import com.octal.actorPay.service.CartItemService;
import com.octal.actorPay.utils.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
@RequestMapping("/offers")
public class UserOfferController extends PagedItemsController {

    private AdminClient adminClient;

    private CommonService commonService;

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    public  UserOfferController(AdminClient adminClient, CommonService commonService) {
        this.adminClient = adminClient;
        this.commonService = commonService;
    }
    @PostMapping("/available")
    public ResponseEntity<ApiResponse> getAllAvailableOffers(@RequestParam(name = "pageNo",defaultValue = "0") Integer pageNo,
                                                             @RequestParam(name="pageSize", defaultValue = "10") Integer pageSize,
                                                             @RequestParam(name="sortBy",defaultValue = "createdAt") String sortBy,
                                                             @RequestParam(name="asc",defaultValue = "false") boolean asc,
                                                             @RequestBody OfferFilterRequest filterRequest) {
        try{
       filterRequest.setServiceName("userService");
        ResponseEntity<ApiResponse> apiResponse = adminClient.getAvailableOffers(pageNo,
                pageSize, sortBy, asc,filterRequest);
        return apiResponse;
        }catch (Exception e){
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), null,
                    String.valueOf(101), HttpStatus.OK), HttpStatus.OK);
        }
    }

    @GetMapping("/code/{promoCode}")
    public ResponseEntity<ApiResponse> getAllOfferByPromoCode(@PathVariable("promoCode") String promoCode, HttpServletRequest request) {
        User user = commonService.getLoggedInUser(request);
        return adminClient.getAllOfferByPromoCode(promoCode);
    }

    @GetMapping("/apply/{promoCode}")
    public ResponseEntity<ApiResponse> applyPromoCode(@PathVariable("promoCode") String promoCode, HttpServletRequest request) {
        User user = commonService.getLoggedInUser(request);
        PromoCodeFilter filterRequest = new PromoCodeFilter();
        filterRequest.setUserId(user.getId());
        CartDTO cartDTO = cartItemService.viewCart(user.getEmail());
        filterRequest.setAmount((float)cartDTO.getTotalPrice());
        filterRequest.setPromoCode(promoCode);
        try {
            ResponseEntity<ApiResponse> couponResponse = adminClient.applyPromoCode(filterRequest);
            if(couponResponse.getBody().getData() != null){
                Gson gson = new Gson();
                String couponResponseJson = gson.toJson(couponResponse.getBody().getData());
                ApplyPromoCodeResponse applyPromoCodeResponse = gson.fromJson(couponResponseJson,ApplyPromoCodeResponse.class);
                applyPromoCodeResponse.setUser(user);
                for(CartItemDTO cartItemDTO : cartDTO.getCartItemDTOList()){
                    applyPromoCodeResponse.setOrderItemId(cartItemDTO.getCartItemId());
//                    cartItemDTO.setPromoCodeResponse(applyPromoCodeResponse);
                    cartItemDTO.setPromoCode(filterRequest.getPromoCode());
                    cartItemService.updateCart(cartItemDTO,user.getEmail());
                }
                return couponResponse;
            }else{
                return new ResponseEntity<>(couponResponse.getBody(),HttpStatus.OK);
            }
        }catch (RuntimeException r){
            return new ResponseEntity<>(new ApiResponse(r.getMessage(), null,
                    String.valueOf(HttpStatus.EXPECTATION_FAILED.value()), HttpStatus.OK), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), null,
                    String.valueOf(HttpStatus.EXPECTATION_FAILED.value()), HttpStatus.OK), HttpStatus.OK);
        }
    }
    @GetMapping("/remove/{promoCode}")
    public ResponseEntity<ApiResponse> removePromoCode(@PathVariable("promoCode") String promoCode, HttpServletRequest request) {
        User user = commonService.getLoggedInUser(request);
        CartDTO cartDTO = cartItemService.viewCart(user.getEmail());
        for(CartItemDTO cartItemDTO : cartDTO.getCartItemDTOList()){
            if(cartItemDTO.getPromoCode() != null && cartItemDTO.getPromoCode().equals(promoCode)){
                cartItemDTO.setPromoCodeResponse(null);
                cartItemDTO.setPromoCode("removePromo");
                cartItemService.updateCart(cartItemDTO,user.getEmail());
            }
        }
        return new ResponseEntity<>(new ApiResponse("Promo Code Remove Successfully!!", null,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }
}

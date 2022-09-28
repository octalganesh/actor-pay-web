package com.octal.actorPay.controller;

import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.ShippingAddressDTO;
import com.octal.actorPay.service.ShippingAddressService;
import com.octal.actorPay.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
public class ShippingController extends PagedItemsController {

    @Autowired
    private ShippingAddressService shippingAddressService;


    @PostMapping(value = "/add/new/shipping/address")
    public ResponseEntity<ApiResponse> addShippingAddress(@RequestBody @Valid ShippingAddressDTO shippingAddressDTO, final HttpServletRequest request) {
        String loggedInUserEmail = request.getHeader("userName");
        return new ResponseEntity<>(ResponseUtils.ActorPayResponse("Shipping address added successfully",
                shippingAddressService.saveShipmentAddress(loggedInUserEmail, shippingAddressDTO), HttpStatus.OK),
                HttpStatus.OK);
    }


    @PutMapping(value = "/update/shipping/address")
    public ResponseEntity<ApiResponse> updateShippingAddress(@RequestBody @Valid ShippingAddressDTO shippingAddressDTO, final HttpServletRequest request) {
        String loggedInUserEmail = request.getHeader("userName");
        if(shippingAddressDTO.getPrimary() == null)
            shippingAddressDTO.setPrimary(false);
        shippingAddressService.updateShippingAddress(loggedInUserEmail, shippingAddressDTO);
        return new ResponseEntity<>(ResponseUtils.ActorPayResponse("Shipping address updated successfully",
                null, HttpStatus.OK),
                HttpStatus.OK);
    }

    @GetMapping(value = "/get/shipping/address/details")
    public ResponseEntity<ApiResponse> getAddressDetails(@RequestParam(name = "id") String shippingAddressId, final HttpServletRequest request) {
        String loggedInUserEmail = request.getHeader("userName");
        ShippingAddressDTO shippingAddressDetails = shippingAddressService.getShippingAddressDetails(loggedInUserEmail, shippingAddressId);
        return new ResponseEntity<>(ResponseUtils.ActorPayResponse("Shipping address details",
                shippingAddressDetails, HttpStatus.OK),
                HttpStatus.OK);
    }


    @GetMapping(value = "/get/all/user/shipping/address")
    public ResponseEntity getAllUsersPaged(@RequestParam(defaultValue = "0") Integer pageNo,
                                           @RequestParam(defaultValue = "10") Integer pageSize, @RequestParam(defaultValue = "createdAt") String sortBy,
                                           @RequestParam(defaultValue = "false") boolean asc, HttpServletRequest request) {
        final PagedItemInfo pagedInfo = createPagedInfo(pageNo, pageSize, sortBy, asc, null);
        PageItem<ShippingAddressDTO> addresses = shippingAddressService.getAllUserSavedShippingAddress(pagedInfo, request.getHeader("userName"));
        return new ResponseEntity<>(ResponseUtils.ActorPayResponse("All shipping address fetched Successfully",
                addresses, HttpStatus.OK),
                HttpStatus.OK);

    }

    @DeleteMapping("/delete/saved/shipping/address/{shippingId}")
    public ResponseEntity<?> deleteUserById(@PathVariable("shippingId") String shippingId, HttpServletRequest request) {
        try {
            shippingAddressService.deleteShippingAddress(shippingId, request.getHeader("userName"));
            return new ResponseEntity<>(new ApiResponse("Shipping address deleted successfully",null,String.valueOf(HttpStatus.OK.value()),HttpStatus.OK),HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(new ApiResponse(e.getMessage(),null,String.valueOf("101"),HttpStatus.OK),HttpStatus.OK);
        }
    }

}
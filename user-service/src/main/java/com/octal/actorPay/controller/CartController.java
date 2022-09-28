package com.octal.actorPay.controller;

import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.CartDTO;
import com.octal.actorPay.dto.CartItemDTO;
import com.octal.actorPay.exceptions.ObjectNotFoundException;
import com.octal.actorPay.service.CartItemService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cartitems")
public class CartController extends PagedItemsController {

    private CartItemService cartItemService;

    public CartController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@Valid @RequestBody CartItemDTO cartItemDTO, HttpServletRequest request) throws ObjectNotFoundException {

        List<String> errorList = valdate(cartItemDTO,"add");

        if(errorList.isEmpty()) {
            String userName = request.getHeader("userName");
            cartItemDTO.setEmail(userName);
            CartDTO cartDTO = cartItemService.addToCart(cartItemDTO);

            if (cartDTO == null) {
                return new ResponseEntity<>(new ApiResponse(String.format("Not able to add item to cart", ""), "",
                        String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(new ApiResponse("Cart Item List", cartDTO,
                        String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
            }
        }else{
            return new ResponseEntity<>(errorList, HttpStatus.BAD_REQUEST);
        }
    }

    private List<String> valdate(CartItemDTO cartItemDTO,String flow) {

        List<String> errorList = new ArrayList<>();
        if(cartItemDTO.getProductQty() <= 0) {
            errorList.add("Product Quantity is required");
        }
        if(flow.equalsIgnoreCase("add")) {
            if (StringUtils.isEmpty(cartItemDTO.getProductId())) {
                errorList.add("Product Id is required");
            }
            if (cartItemDTO.getProductPrice() <= 0) {
                errorList.add("Product Deal price is required");
            }
        }
        if(flow.equalsIgnoreCase("update")) {
            if(StringUtils.isEmpty(cartItemDTO.getCartItemId())) {
                errorList.add("Cart Item id is required");
            }
        }
        return errorList;
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateCartItem(@RequestBody CartItemDTO cartItemDTOs, HttpServletRequest request) throws ObjectNotFoundException {

        List<String> errorList = valdate(cartItemDTOs,"update");
        if(errorList.isEmpty()) {
            String userName = request.getHeader("userName");
            CartDTO cartDTO = cartItemService.updateCart(cartItemDTOs, userName);
            if (cartDTO == null) {
                return new ResponseEntity<>(new ApiResponse(String.format("Not able to update item to cart", ""), "",
                        String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(new ApiResponse("Cart Item List", cartDTO,
                        String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
            }
        }else{
            return new ResponseEntity<>(errorList, HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/view")
    public ResponseEntity<ApiResponse> viewCart(HttpServletRequest request) {
        String userName = request.getHeader("userName");
        CartDTO cartDTO = cartItemService.viewCart(userName);
        if (cartDTO == null) {
            return new ResponseEntity<>(new ApiResponse("Cart is empty", "",
                    String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse("Cart List", cartDTO,
                    String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        }
    }

    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<ApiResponse> removeCart(@PathVariable("cartItemId") String cartItemId, HttpServletRequest request) {
        String userName = request.getHeader("userName");
        CartDTO cartDTO = cartItemService.removeCart(cartItemId, userName);
        if (cartDTO.getCartItemDTOList() != null && cartDTO.getCartItemDTOList().size() > 0) {
            return new ResponseEntity<>(new ApiResponse("Cart List", cartDTO,
                    String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponse("Cart is Empty", cartDTO,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse> clearCart(HttpServletRequest request) {
        String userName = request.getHeader("userName");
        CartDTO cartDTO = cartItemService.clearCart(userName);
        return new ResponseEntity<>(new ApiResponse("Your Cart is Clear", cartDTO,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }
}

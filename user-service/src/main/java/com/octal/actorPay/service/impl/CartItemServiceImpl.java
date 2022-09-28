package com.octal.actorPay.service.impl;

import com.amazonaws.services.opsworks.model.App;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.octal.actorPay.dto.*;
import com.octal.actorPay.dto.request.PromoCodeFilter;
import com.octal.actorPay.entities.CartItem;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.exceptions.ActorPayException;
import com.octal.actorPay.exceptions.ObjectNotFoundException;
import com.octal.actorPay.feign.clients.AdminClient;
import com.octal.actorPay.feign.clients.MerchantClient;
import com.octal.actorPay.repositories.CartItemRepository;
import com.octal.actorPay.repositories.UserRepository;
import com.octal.actorPay.service.CartItemService;
import com.octal.actorPay.transformer.CartTransformer;
import com.octal.actorPay.utils.TaxCalculation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CartItemServiceImpl implements CartItemService {

    private CartItemRepository cartItemRepository;

    private MerchantClient merchantClient;

    private AdminClient adminClient;

    private TaxCalculation taxCalculation;

    private UserRepository userRepository;

    public CartItemServiceImpl(CartItemRepository cartItemRepository,
                               MerchantClient merchantClient,
                               AdminClient adminClient,
                               TaxCalculation taxCalculation, UserRepository userRepository) {
        this.cartItemRepository = cartItemRepository;
        this.merchantClient = merchantClient;
        this.adminClient = adminClient;
        this.taxCalculation = taxCalculation;
        this.userRepository = userRepository;
    }

    @Override
    public CartDTO addToCart(CartItemDTO cartItemDTO) throws ObjectNotFoundException {

        User user = userRepository.findByEmail(cartItemDTO.getEmail()).orElse(null);
        CartItem cart = cartItemRepository.findByProductIdAndIsActiveTrueAndUserAndDeletedFalse(cartItemDTO.getProductId(), user);
        if (cart != null) {
            throw new ActorPayException("The Product you have selected already exist in the cart ");
        }
        CartItem cartItem = CartTransformer.CART_DTO_TO_CART_ENTITY.apply(cartItemDTO);

        if (Objects.nonNull(cartItemDTO.getPromoCodeResponse()) && !cartItemDTO.getPromoCodeResponse().getPromoCode().isEmpty()) {
            cartItem.setPromoCodeResponse(cartItemDTO.getPromoCodeResponse());
        }

        ResponseEntity<ApiResponse> apiResponseResponseEntity = merchantClient.getProductById(cartItem.getProductId());
        if (apiResponseResponseEntity.getStatusCode().is2xxSuccessful()) {
            ApiResponse apiResponse = (ApiResponse) apiResponseResponseEntity.getBody();
            if (apiResponse.getData() == null) {
                throw new ObjectNotFoundException(String.format("Product data not found: - %s", cartItem.getProductId()));
            }
            processCartItems(apiResponse.getData(), cartItem, user);
        } else {
            throw new ObjectNotFoundException(String.format("Product not found: -  Product Id %s", cartItem.getProductId()));
        }
        CartDTO cartDTO = getActiveCartItemByUser(cartItem.getUser());
        return cartDTO;
    }

    @Override
    public CartDTO updateCart(CartItemDTO cartItemDTO, String userName) {
        User user = userRepository.findByEmail(userName).orElse(null);
//        for (CartItemDTO cartItemDTO : cartItemDTOsUpdateRequest) {
        CartItem cartItem = cartItemRepository.findByIdAndUserAndIsActiveTrueAndDeletedFalse(cartItemDTO.getCartItemId(), user);
        cartItem.setProductQty(cartItemDTO.getProductQty());
        cartItem.setPromoCodeResponse(cartItemDTO.getPromoCodeResponse());
        ResponseEntity<ApiResponse> apiResponseResponseEntity = merchantClient.getProductById(cartItem.getProductId());
        if (apiResponseResponseEntity.getStatusCode().is2xxSuccessful()) {
            ApiResponse apiResponse = (ApiResponse) apiResponseResponseEntity.getBody();
            if (apiResponse.getData() == null) {
                throw new ObjectNotFoundException(String.format("Product data not found: - Product Id %s", cartItem.getProductId()));
            }
            cartItem.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
            if (cartItemDTO.getPromoCode() != null && cartItemDTO.getPromoCode().length() > 0) {
                cartItem.setPromoCode(cartItemDTO.getPromoCode());
            }
            if(cartItemDTO.getPromoCode() != null && cartItemDTO.getPromoCode().equalsIgnoreCase("removePromo")){
                cartItem.setPromoCode(null);
                cartItem.setPromoCodeResponse(null);
            }
            processCartItems(apiResponse.getData(), cartItem, user);
        } else {
            throw new ObjectNotFoundException(String.format("Product not found: -  Product Id %s", cartItem.getProductId()));
        }
//        }
        CartDTO cartDTO = getActiveCartItemByUser(user);
        return cartDTO;
    }

    @Override
    public CartDTO getActiveCartItemByUser(User user) {
        List<CartItem> cartItems = cartItemRepository.findByUserAndIsActiveTrueAndDeletedFalse(user);
        if (cartItems != null && !cartItems.isEmpty()) {
            List<CartItemDTO> cartItemDTOs = new ArrayList<>();
            int totalQuantity = 0;
            double totalPrice = 0;
            double totalSgst = 0;
            double totalCgst = 0;
            double totalTaxableValue = 0;
            String merchantId = "";
            String merchantName = "";
            String promoCode = "";
            CartDTO cartDTO = new CartDTO();
            for (CartItem cartItem : cartItems) {
                CartItemDTO cartItemDTO = CartTransformer.CART_ENTITY_TO_CART_DTO.apply(cartItem);
                totalQuantity = totalQuantity + cartItemDTO.getProductQty();
                totalPrice = totalPrice + cartItemDTO.getTotalPrice();
                totalSgst = totalSgst + cartItemDTO.getProductSgst();
                totalCgst = totalCgst + cartItemDTO.getProductCgst();
                totalTaxableValue = totalTaxableValue + cartItemDTO.getTaxableValue();
                merchantId = cartItemDTO.getMerchantId();
                merchantName = merchantClient.getMerchantName(cartItem.getMerchantId()).getBody();
                cartItemDTO.setMerchantName(merchantName);
                String productName = merchantClient.getProductName(cartItem.getProductId()).getBody();
                cartItemDTO.setProductName(productName);
                if (Objects.nonNull(cartItem.getPromoCodeResponse())) {
                    cartItemDTO.setPromoCodeResponse(cartItem.getPromoCodeResponse());
                }
                if (cartItem.getPromoCode() != null && cartItem.getPromoCode().length() != 0) {
                    promoCode = cartItem.getPromoCode();
                }
                cartItemDTO.setPromoCode(cartItem.getPromoCode());
                cartItemDTOs.add(cartItemDTO);
            }
            cartDTO.setTotalCgst(doubleFormattingUpTo2Decimal(totalCgst));
            cartDTO.setTotalSgst(doubleFormattingUpTo2Decimal(totalSgst));
            cartDTO.setTotalPrice(totalPrice);
            cartDTO.setTotalTaxableValue(totalTaxableValue);
            cartDTO.setUserId(user.getId());
            cartDTO.setMerchantId(merchantId);
            cartDTO.setMerchantName(merchantName);
            cartDTO.setTotalQuantity(totalQuantity);
            //Get Promo Code Details;
            if (promoCode != null && promoCode.length() != 0) {
                Gson gson = new Gson();
                PromoCodeFilter filterRequest = new PromoCodeFilter();
                filterRequest.setAmount((float) cartDTO.getTotalPrice());
                filterRequest.setPromoCode(promoCode);
                filterRequest.setUserId(cartDTO.getUserId());
                ResponseEntity<ApiResponse> response = adminClient.applyPromoCode(filterRequest);
                String promoCodeResponseJson = gson.toJson(response.getBody().getData());
                ApplyPromoCodeResponse promoCodeResponse = gson.fromJson(promoCodeResponseJson, ApplyPromoCodeResponse.class);
                cartDTO.setPromoCodeResponse(promoCodeResponse);
            }

            cartDTO.setCartItemDTOList(cartItemDTOs);
            return cartDTO;
        } else {
            CartDTO cartDTO = new CartDTO();
            cartDTO.setCartItemDTOList(new ArrayList<>());
            return cartDTO;
        }
    }

    public double doubleFormattingUpTo2Decimal(double total) {
        Formatter formatter = new Formatter();
        formatter.format("%.2f", total);
        return total;
    }

    public CartDTO viewCart(String userName) {
        User user = userRepository.findByEmail(userName).orElse(null);
        CartDTO cartDTO = getActiveCartItemByUser(user);
        return cartDTO;
    }

    @Transactional
    @Override
    public CartDTO removeCart(String cartItemId, String userName) {
        User user = userRepository.findByEmail(userName).orElse(null);
        cartItemRepository.deleteByIdAndUserAndIsActiveTrueAndDeletedFalse(cartItemId, user);
        CartDTO cartDTO = getActiveCartItemByUser(user);
        return cartDTO;
    }

    @Transactional
    @Override
    public CartDTO clearCart(String userName) {
        User user = userRepository.findByEmail(userName).orElse(null);
        List<CartItem> cartItems = cartItemRepository.findByUserAndIsActiveTrueAndDeletedFalse(user);
        cartItems.forEach(cartItem ->
                {
                    cartItem.setActive(false);
                    cartItem.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
                    cartItem.setDeleted(true);
                }
        );
        CartDTO cartDTO = new CartDTO();
        cartDTO.setCartItemDTOList(new ArrayList<>());
        return cartDTO;
    }

    private void processCartItems(Object data, CartItem cartItem, User user) {
        ObjectMapper mapper = new ObjectMapper();
        ProductDTO productDTO = mapper.convertValue(data, ProductDTO.class);
        if (productDTO.getStockCount() <= 0) {
            throw new ObjectNotFoundException(
                    String.format("Product is not available to Shop - Product is Out of Stock: %s", productDTO.getName()));
        }
        if (productDTO.getStockCount() < cartItem.getProductQty()) {
            throw new ObjectNotFoundException(String.format("Insufficient Product Stock for the Product: %s  " +
                    "Stock availability is %d ", productDTO.getName(), productDTO.getStockCount()));
        }

        ResponseEntity<ApiResponse> apiTaxResponseResponseEntity = merchantClient.getTaxById(productDTO.getTaxId());
        if (apiTaxResponseResponseEntity.getStatusCode().is2xxSuccessful()) {
            ApiResponse taxApiResponse = (ApiResponse) apiTaxResponseResponseEntity.getBody();
            TaxDTO taxDTO = mapper.convertValue(taxApiResponse.getData(), TaxDTO.class);
            float taxPercentage = taxDTO.getTaxPercentage();
            double qty = cartItem.getProductQty();
            if (qty <= 0) {
                throw new ActorPayException("Invalid quantity - Quantity must be at least 1");
            }
            double sgst = productDTO.getSgst();
            double cgst = productDTO.getCgst();
            cartItem.setUser(user);
            cartItem.setTaxPercentage(taxPercentage);
            cartItem.setImage(productDTO.getImage());
            cartItem.setProductId(productDTO.getProductId());
            cartItem.setMerchantId(productDTO.getMerchantId());
            cartItem.setProductCgst(cgst > 0 ? cgst * qty : cgst);
            cartItem.setProductSgst(sgst > 0 ? cgst * qty : cgst);
            cartItem.setTaxableValue(productDTO.getDealPrice() * qty);
            cartItem.setProductPrice((productDTO.getDealPrice() + cgst + sgst));
            cartItem.setTotalPrice((productDTO.getDealPrice() + cgst + sgst) * qty);
            cartItem.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
            cartItem.setMerchantId(productDTO.getMerchantId());
            cartItem.setActive(true);
            validateCartMerchant(cartItem, user);
            cartItemRepository.save(cartItem);
        } else {
            throw new ObjectNotFoundException(String.format("Tax data not found: - Tax Id %s", productDTO.getTaxId()));
        }
    }

    private void validateCartMerchant(CartItem cartItem, User user) {
        List<CartItem> cartItems = cartItemRepository.findByUserAndIsActiveTrueAndDeletedFalse(user);
        List<String> merchantList = cartItems.stream()
                .map(CartItem::getMerchantId)
                .collect(Collectors.toList());
        String currentMerchants = cartItem.getMerchantId();

        if (merchantList.size() != 0 && !merchantList.contains(currentMerchants)) {
            throw new RuntimeException("Can't add Product from multiple Merchant");
        }
    }
}

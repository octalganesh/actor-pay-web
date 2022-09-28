package com.octal.actorPay.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.octal.actorPay.client.AdminFeignClient;
import com.octal.actorPay.constants.UserType;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.MerchantDTO;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.ProductDTO;
import com.octal.actorPay.dto.request.ProductFilterRequest;
import com.octal.actorPay.entities.MerchantDetails;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.service.*;
import feign.FeignException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
public class MerchantProductController extends BaseController {

    private ProductService productService;

    private UploadService uploadServiceUtil;

    private MerchantService merchantService;

    private AdminFeignClient adminFeignClient;

    private OutletService outletService;

    private SubMerchantService subMerchantService;

    @Autowired
    public MerchantProductController(ProductService productService,
                                     UploadService uploadServiceUtil,
                                     MerchantService merchantService,
                                     AdminFeignClient adminFeignClient,
                                     OutletService outletService, SubMerchantService subMerchantService) {
        this.productService = productService;
        this.uploadServiceUtil = uploadServiceUtil;
        this.merchantService = merchantService;
        this.adminFeignClient = adminFeignClient;
        this.outletService = outletService;
        this.subMerchantService = subMerchantService;
    }

    @Secured("ROLE_PRODUCT_ADD")
    @PostMapping
    public ResponseEntity<ApiResponse>
    addProduct(@RequestPart(value = "file", required = false) MultipartFile file,
               @Valid @RequestPart("product") ProductDTO productDTO, HttpServletRequest httpServletRequest) throws Exception {
        try {

            User user = getAuthorizedUser(httpServletRequest);
            productDTO.setUserName(user.getEmail());
            if (user != null) {
                if (user.getMerchantDetails() != null) {
                    MerchantDetails merchantDetails = user.getMerchantDetails();
                    if (!(merchantDetails != null && merchantDetails.getId().equalsIgnoreCase(productDTO.getMerchantId()))) {
                        return new ResponseEntity<>(new ApiResponse(String.format("Invalid Merchant Id %s", ""), "",
                                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
                    }
                } else {
                    validateProduct(productDTO);
                    ProductDTO newProductDTO = productService.save(file, productDTO);
                    if (newProductDTO != null) {
                        return new ResponseEntity<>(new ApiResponse("Product created successfully", productDTO.getProductId(),
                                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(new ApiResponse("Error while creating Product", "",
                                String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
                    }
                }
            }
            validateProduct(productDTO);
            ProductDTO newProductDTO = productService.save(file, productDTO);
            if (newProductDTO != null) {
                return new ResponseEntity<>(new ApiResponse("Product created successfully", productDTO.getProductId(),
                        String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ApiResponse("Error while creating Product", "",
                        String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
            }
        } catch (FeignException e) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
            String content = e.contentUTF8();
            Collection<ApiResponse> apiResponse = objectMapper.readValue(
                    content, new TypeReference<Collection<ApiResponse>>() {
                    });
            System.out.println(apiResponse.stream().findFirst());
            System.out.println(e.getMessage());
            return new ResponseEntity<>(apiResponse.stream().findFirst().get(), HttpStatus.BAD_REQUEST);
        }
    }

    @Secured("ROLE_PRODUCT_EDIT")
    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse> updateProduct(@RequestPart(value = "file", required = false) MultipartFile file,
                                                     @PathVariable("productId") String productId,
                                                     @Valid @RequestPart("product") ProductDTO productDTO,
                                                     HttpServletRequest httpServletRequest) throws Exception {
        User user = getAuthorizedUser(httpServletRequest);
        try {
            if (user != null) {
                MerchantDetails merchantDetails = user.getMerchantDetails();
                if (!(merchantDetails != null && merchantDetails.getId().equalsIgnoreCase(productDTO.getMerchantId()))) {
                    return new ResponseEntity<>(new ApiResponse(String.format("Invalid Merchant Id %s", ""), "",
                            String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
                }
            }
            productDTO.setUserName(user.getEmail());
            validateProduct(productDTO);
            ProductDTO updatedProductDTO = productService.update(file, productDTO);
            return new ResponseEntity<>(new ApiResponse("Product updated successfully", updatedProductDTO,
                    String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        } catch (FeignException e) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
            String content = e.contentUTF8();
            Collection<ApiResponse> apiResponse = objectMapper.readValue(
                    content, new TypeReference<Collection<ApiResponse>>() {
                    });
            System.out.println(apiResponse.stream().findFirst());
            System.out.println(e.getMessage());
            return new ResponseEntity<>(apiResponse.stream().findFirst().get(), HttpStatus.BAD_REQUEST);
        }
    }

    private void validateProduct(ProductDTO productDTO) {
        ResponseEntity<ApiResponse> catApiResponse = adminFeignClient.getCategoryById(productDTO.getCategoryId());
        ResponseEntity<ApiResponse> subCatApiResponse = adminFeignClient.getSubCategoryById(productDTO.getSubCategoryId());
        ResponseEntity<ApiResponse> taxApiResponse = adminFeignClient.getTaxById(productDTO.getTaxId());
        Long count = outletService.findCountById(productDTO.getOutletId());
        if (count == null || count == 0)
            throw new RuntimeException(String.format("The given Outlet Id is is not found - Outlet Id: %s", productDTO.getOutletId()));
        Double dealPrice = productDTO.getDealPrice();
        Double actualPrice = productDTO.getActualPrice();
        if (dealPrice > actualPrice) {
            throw new RuntimeException("Deal Price cannot be greater than Actual Price");
        }
    }

    @Secured("ROLE_PRODUCT_VIEW")
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable("productId") String productId,HttpServletRequest request) {

        User user = getAuthorizedUser(request);
        ProductDTO productDTO = productService.findByIdAndMerchantDetails(productId,user.getMerchantDetails());
        String message = (productDTO == null) ? "Product not found" : "";
        if (productDTO == null) {
            message = String.format("Product not found for the id %s -  ", productId);
            return new ResponseEntity<>(new ApiResponse(message, productDTO,
                    String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ApiResponse(message, productDTO,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_PRODUCT_LIST_VIEW")
    @PostMapping("/list/paged")
    public ResponseEntity<ApiResponse> getAllProduct(@RequestParam(defaultValue = "0") Integer pageNo,
                                                     @RequestParam(defaultValue = "10") Integer pageSize,
                                                     @RequestParam(defaultValue = "createdAt") String sortBy,
                                                     @RequestParam(defaultValue = "false") boolean asc,
                                                     @RequestBody ProductFilterRequest filterRequest, HttpServletRequest request) {
        User user = getAuthorizedUser(request);
        if (user != null && user.getUserType().equals(UserType.merchant.name())) {
            MerchantDTO merchantDTO = merchantService.getMerchantDetails(user.getId(), user.getEmail());
            filterRequest.setMerchantId(merchantDTO.getMerchantId());
        }
        final PagedItemInfo pagedInfo = createPagedInfo(pageNo, pageSize, sortBy, asc, null);

        return new ResponseEntity<>(new ApiResponse("Product list", productService.getAllProducts(pagedInfo,
                filterRequest),
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_PRODUCT_CHANGE_STATUS")
    @PutMapping("/change/status")
    public ResponseEntity<ApiResponse> changeProductStatus(@RequestParam(name = "id") String id,
                                                           @RequestParam(name = "status") Boolean status,HttpServletRequest request) throws InterruptedException {
        User user = getAuthorizedUser(request);
        productService.changeProductStatus(id, status,user.getMerchantDetails());
        return new ResponseEntity<>(new ApiResponse("Product status updated successfully", null,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_PRODUCT_DELETE")
    @DeleteMapping(value = "/remove")
    public ResponseEntity<ApiResponse> deleteProducts(@RequestParam("productId") List<String> productIds,HttpServletRequest request) {

        User user = getAuthorizedUser(request);
        Map<String, List<String>> listMap = productService.deleteProducts(productIds,user.getMerchantDetails());
        List<String> availableProducts = listMap.get("availableProducts");
        List<String> notAvailableProducts = listMap.get("notAvailableProducts");
        if (!availableProducts.isEmpty() && !notAvailableProducts.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse("Product(s) deleted successfully " + availableProducts
                    , "Following Product Id(s) are Invalid or already deleted " + notAvailableProducts,
                    String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        } else {
            if (!availableProducts.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse("Product(s) deleted successfully " + availableProducts
                        , "", String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
            }
            if (!notAvailableProducts.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse("Following Product Id(s) are Invalid or already deleted " + notAvailableProducts
                        , "", String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
            }
        }
        return null;
    }

    @GetMapping(value = "/getProductName/{productId}")
    public ResponseEntity<String> getProductName(@PathVariable("productId") String productId) {
        return new ResponseEntity<>(productService.findProductNameById(productId), HttpStatus.OK);
    }

    @GetMapping("/subcate/associated")
    ResponseEntity<ApiResponse> isSubcategoryAssociated(@RequestParam("subcategoryId") String subcategoryId) {
        Boolean isAssociated = productService.isSubcategoryAssociated(subcategoryId, PageRequest.of(0, 1));
        return new ResponseEntity<>(new ApiResponse(""
                , isAssociated, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping("/cate/associated")
    ResponseEntity<ApiResponse> isCategoryAssociated(@RequestParam("categoryId") String categoryId) {
        Boolean isAssociated = productService.isCategoryAssociated(categoryId, PageRequest.of(0, 1));
        return new ResponseEntity<>(new ApiResponse(""
                , isAssociated, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping("/byName/{productName}")
    public ResponseEntity<ApiResponse> findByName(@PathVariable("productName") String productName) {
        String productId = productService.findByNameAndDeleted(productName, false);
        if (StringUtils.isNotBlank(productId)) {
            return new ResponseEntity<>(new ApiResponse("Product Name ",
                    productId, HttpStatus.OK.name(), HttpStatus.OK), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse("Product not found ",
                    productId, HttpStatus.NOT_FOUND.name(), HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
        }
    }

    @Secured("ROLE_PRODUCT_CHANGE_STOCK")
    @PutMapping("/update/product/stock")
    public ResponseEntity<ApiResponse> updateProductStock(
            @RequestParam(name = "productId") String productId,
            @RequestParam(name = "stockCount") Integer stockCount,
            @RequestParam(name = "stockStatus") String stockStatus,HttpServletRequest request) {
        try {
            User user = getAuthorizedUser(request);
            productService.updateProductStock(productId, stockCount, stockStatus);
            return new ResponseEntity<>(new ApiResponse("Product Stock updated Successfully ",
                    productId, HttpStatus.OK.name(), HttpStatus.OK), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse("Product Stock Update Status ",
                    productId, HttpStatus.BAD_REQUEST.name(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all-products-counts")
    public ResponseEntity<ApiResponse> getAllProductProductCount() {
        return new ResponseEntity<>(new ApiResponse("All Product Counts.", productService.getAllProductProductCount(), String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }
}

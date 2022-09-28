package com.octal.actorPay.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.octal.actorPay.client.AdminFeignClient;
import com.octal.actorPay.constants.StockStatus;
import com.octal.actorPay.dto.*;
import com.octal.actorPay.dto.request.ProductFilterRequest;
import com.octal.actorPay.entities.*;
import com.octal.actorPay.exceptions.ObjectNotFoundException;
import com.octal.actorPay.exceptions.ActorPayException;
import com.octal.actorPay.repositories.MerchantDetailsRepository;
import com.octal.actorPay.repositories.OutletRepository;
import com.octal.actorPay.repositories.ProductRepository;
import com.octal.actorPay.repositories.UserRepository;
import com.octal.actorPay.service.MerchantService;
import com.octal.actorPay.service.ProductService;
import com.octal.actorPay.service.UploadService;
import com.octal.actorPay.specification.GenericSpecificationsBuilder;
import com.octal.actorPay.specification.SpecificationFactory;
import com.octal.actorPay.transformer.MerchantTransformer;
import com.octal.actorPay.transformer.PagedItemsTransformer;
import com.octal.actorPay.utils.TaxCalculation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private ProductRepository productRepository;

    private UploadService uploadServiceUtil;

    private MerchantService merchantService;

    private TaxCalculation taxCalculation;

    @Autowired
    private AdminFeignClient adminFeignClient;

    @Autowired
    private SpecificationFactory<Product> productSpecificationFactory;

    @Autowired
    private OutletRepository outletRepository;

    @Autowired
    public ProductServiceImpl(
            ProductRepository productRepository,
            UploadService uploadServiceUtil, MerchantService merchantService,
            TaxCalculation taxCalculation) {
        this.productRepository = productRepository;
        this.uploadServiceUtil = uploadServiceUtil;
        this.merchantService = merchantService;
        this.taxCalculation = taxCalculation;
    }


    @Override
    public ProductDTO save(MultipartFile file, ProductDTO productDTO) throws Exception {
        try {
            double sgst = 0;
            double cgst = 0;
            Product dupProdct = productRepository.findByName(productDTO.getName());
            if (dupProdct != null) {
                throw new ActorPayException(String.format("Product already created with name %s ", productDTO.getName()));
            }
            Product product = mapProductDTOToProduct(productDTO);
            if (file == null) {
                throw new ActorPayException("Product image is required");
            }
            String s3Path = uploadServiceUtil.uploadFileToS3(file, "Product/" + productDTO.getName());
            product.setImage(s3Path);
            product.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));

            MerchantDetails merchantDetails = merchantService.getMerchantIdByEmail(productDTO.getUserName());
            product.setMerchantDetails(merchantDetails);
            product.setCategoryId(productDTO.getCategoryId());
            product.setSubcategoryId(productDTO.getSubCategoryId());
            product.setStockCount(productDTO.getStockCount());
            product.setActive(Boolean.TRUE);
            product.setTaxId(productDTO.getTaxId());
            product.setOutletId(productDTO.getOutletId());
            String stockStatus = productDTO.getStockCount() > 0 ? StockStatus.IN_STOCK.name() : StockStatus.OUT_OF_STOCK.name();
            product.setStockStatus(stockStatus);
            ResponseEntity<ApiResponse> apiResponseResponseEntity
                    = adminFeignClient.getTaxById(productDTO.getTaxId());
            ApiResponse taxApiResponse = (ApiResponse) apiResponseResponseEntity.getBody();
            ObjectMapper mapper = new ObjectMapper();
            TaxDTO taxDTO = mapper.convertValue(taxApiResponse.getData(), TaxDTO.class);
            float taxPercentage = taxDTO.getTaxPercentage();
            if (taxPercentage > 0) {
                TaxCalculationInput calculationInput =
                        new TaxCalculationInput(product.getDealPrice(), taxPercentage, 1);
                TaxCalculationResponse taxCalculationResponse = taxCalculation.calculateTax(calculationInput);
                product.setDealPrice(taxCalculationResponse.getProductPriceExcludingTax());
                sgst = taxCalculationResponse.getsGst();
                cgst = taxCalculationResponse.getcGst();
            }
            ProductTax productTax = new ProductTax();
            productTax.setSgst(sgst);
            productTax.setCgst(cgst);
            productTax.setActive(true);
            productTax.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
            product.setProductTax(productTax);
            Product newProduct = productRepository.save(product);
            ProductDTO updatedProductDTO = mapProductToProductDTO(newProduct);
            return updatedProductDTO;
        } catch (IOException e) {
            throw new ActorPayException(e.getMessage());
        } catch (Exception e) {
            throw new ActorPayException(e.getMessage());
        }
    }

    @Override
    public ProductDTO update(MultipartFile file, ProductDTO productDTO) throws Exception {
        try {

            Product isExist = productRepository.findByName(productDTO.getName());
            if (isExist != null && !(isExist.getId().equalsIgnoreCase(productDTO.getProductId()))) {
                throw new ActorPayException(String.format("Category name is already found - %s ", productDTO.getName()));
            }
            if ((isExist != null && org.apache.commons.lang3.StringUtils.isEmpty(isExist.getImage())) && (file == null || file.isEmpty())) {
                throw new ActorPayException("Image is mandatory for Product");
            }

            Product product = productRepository.findById(productDTO.getProductId()).orElse(null);
            if (file != null) {
                String s3Path = uploadServiceUtil.uploadFileToS3(file, "Product/" + product.getName());
                productDTO.setImage(s3Path);
                product.setImage(s3Path);
            }
            ResponseEntity<ApiResponse> apiResponseResponseEntity
                    = adminFeignClient.getTaxById(productDTO.getTaxId());
            ApiResponse taxApiResponse = (ApiResponse) apiResponseResponseEntity.getBody();
            ObjectMapper mapper = new ObjectMapper();
            TaxDTO taxDTO = mapper.convertValue(taxApiResponse.getData(), TaxDTO.class);
            float taxPercentage = taxDTO.getTaxPercentage();
            // when updating the actual price is excluding the tax so adding gst value to get actual price
            double oldProductDealPrice = product.getDealPrice();
            double newProductDealPrice = productDTO.getDealPrice();
            if (oldProductDealPrice != newProductDealPrice) {
                TaxCalculationInput calculationInput =
                        new TaxCalculationInput(newProductDealPrice, taxPercentage, 1);
                TaxCalculationResponse taxCalculationResponse = taxCalculation.calculateTax(calculationInput);
                product.setDealPrice(taxCalculationResponse.getProductPriceExcludingTax());
                ProductTax productTax = product.getProductTax();
                productTax.setCgst(taxCalculationResponse.getcGst());
                productTax.setSgst(taxCalculationResponse.getsGst());
                product.setProductTax(productTax);
            }
            product.setDescription(productDTO.getDescription());
            product.setName(productDTO.getName());
            product.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
            product.setTaxId(productDTO.getTaxId());
            product.setCategoryId(productDTO.getCategoryId());
            product.setSubcategoryId(productDTO.getSubCategoryId());
            product.setStockCount(productDTO.getStockCount());
            String stockStatus = productDTO.getStockCount() > 0 ? StockStatus.IN_STOCK.name() : StockStatus.OUT_OF_STOCK.name();
            product.setStockStatus(stockStatus);
            product.setOutletId(productDTO.getOutletId());
            Product newProduct = productRepository.save(product);
            ProductDTO updatedProductDTO = mapProductToProductDTO(newProduct);
            return updatedProductDTO;
        } catch (IOException e) {
            throw new ActorPayException(e.getMessage());
        } catch (Exception e) {
            throw new ActorPayException(e.getMessage());
        }
    }
//

    @Override
    public ProductDTO getProductById(String productId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            ProductDTO productDTO = mapProductToProductDTO(product);
            String categoryId = product.getCategoryId();
            ResponseEntity<ApiResponse> apiResponseCategory = adminFeignClient.getCategoryById(categoryId);
            ApiResponse categoryBodyData = (ApiResponse) apiResponseCategory.getBody();
            if (categoryBodyData.getData() != null) {
                ObjectMapper mapper = new ObjectMapper();
                CategoriesDTO categoriesDTO = mapper.convertValue(categoryBodyData.getData(), CategoriesDTO.class);
                productDTO.setCategoryName(categoriesDTO.getName());
                String subcategoryId = product.getSubcategoryId();
                ResponseEntity<ApiResponse> apiResponseSubcategory = adminFeignClient.getSubCategoryById(subcategoryId);
                ApiResponse subcategoryBodyData = (ApiResponse) apiResponseSubcategory.getBody();
                if (subcategoryBodyData.getData() != null) {
                    SubCategoriesDTO subCategoriesDTO = mapper.convertValue(subcategoryBodyData.getData(), SubCategoriesDTO.class);
                    productDTO.setSubCategoryName(subCategoriesDTO.getName());
                }
            }
            return productDTO;
        }
        return null;
    }

    @Override
    public ProductDTO findByIdAndMerchantDetails(String productId, MerchantDetails merchantDetails) {
//        Product product = productRepository.findByIdAndMerchantDetails(productId,merchantDetails).orElseThrow(()-> new ActorPayException("Product not found"));
       System.out.println("#### Product Details ##### " + productId);
//        Product product = productRepository.findById(productId).orElseThrow(()-> new ActorPayException("Product not found"));
        Product product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            ProductDTO productDTO = mapProductToProductDTO(product);
            String categoryId = product.getCategoryId();
            ResponseEntity<ApiResponse> apiResponseCategory = adminFeignClient.getCategoryById(categoryId);
            ApiResponse categoryBodyData = (ApiResponse) apiResponseCategory.getBody();
            if (categoryBodyData.getData() != null) {
                ObjectMapper mapper = new ObjectMapper();
                CategoriesDTO categoriesDTO = mapper.convertValue(categoryBodyData.getData(), CategoriesDTO.class);
                productDTO.setCategoryName(categoriesDTO.getName());
                String subcategoryId = product.getSubcategoryId();
                ResponseEntity<ApiResponse> apiResponseSubcategory = adminFeignClient.getSubCategoryById(subcategoryId);
                ApiResponse subcategoryBodyData = (ApiResponse) apiResponseSubcategory.getBody();
                if (subcategoryBodyData.getData() != null) {
                    SubCategoriesDTO subCategoriesDTO = mapper.convertValue(subcategoryBodyData.getData(), SubCategoriesDTO.class);
                    productDTO.setSubCategoryName(subCategoriesDTO.getName());
                }
            }
            return productDTO;
        }
        return null;
    }

    private ProductDTO mapProductToProductDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductId(product.getId());
        productDTO.setDescription(product.getDescription());
        productDTO.setActualPrice(product.getActualPrice());
        productDTO.setDealPrice(product.getDealPrice());
        productDTO.setName(product.getName());
        productDTO.setImage(product.getImage());
        if (product.getMerchantDetails() != null) {
            productDTO.setMerchantId(product.getMerchantDetails().getId());
            productDTO.setMerchantName(product.getMerchantDetails().getBusinessName());
        }
        productDTO.setCreatedAt(product.getCreatedAt());
        productDTO.setUpdatedAt(product.getUpdatedAt());
        productDTO.setCategoryId(product.getCategoryId());
        productDTO.setSubCategoryId(product.getSubcategoryId());
        productDTO.setStockCount(product.getStockCount());
        productDTO.setStatus(product.isActive());
        productDTO.setTaxId(product.getTaxId());
        productDTO.setOutletId(product.getOutletId());
        productDTO.setStockStatus(product.getStockStatus());
        if (product.getProductTax() != null) {
            productDTO.setSgst(product.getProductTax().getSgst());
            productDTO.setCgst(product.getProductTax().getCgst());
            productDTO.setProductTaxId(product.getProductTax().getId());
        }
        System.out.println("======   End of  map method");
        return productDTO;
    }

    @Override
    public PageItem<ProductDTO> getAllProducts(PagedItemInfo pagedInfo, ProductFilterRequest filterRequest) {
        System.out.println("in get all product===============");
        GenericSpecificationsBuilder<Product> builder = new GenericSpecificationsBuilder<>();
        // prepare search query
        prepareProductSearchQuery(filterRequest, builder);

        final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(Product.class, pagedInfo);

        Page<Product> pagedResult = productRepository.findAll(builder.build(), pageRequest);
        List<String> outLetIds = pagedResult.getContent().stream().map(Product::getOutletId).collect(Collectors.toList());
        List<Outlet> outletList = outletRepository.findAllById(outLetIds);

        List<ProductDTO> productDTOs = new ArrayList<>();
        System.out.println("------- >>>>>   "+pagedResult.getContent());
        for (Product product : pagedResult.getContent()) {
            ProductDTO productDTO = mapProductToProductDTO(product);
            Outlet outlet = outletList.stream().filter(v->v.getId().equals(product.getOutletId())).findFirst().orElse(null);
            if (outlet != null) {
                if (Objects.nonNull(outlet.getAddress()) && Objects.nonNull(outlet.getAddress().getLatitude())
                && Objects.nonNull(outlet.getAddress().getLongitude())) {
                    double dist = org.apache.lucene.util.SloppyMath.haversinMeters(
                            Double.parseDouble(filterRequest.getLatitude()), Double.parseDouble(filterRequest.getLongitude()),
                            Double.parseDouble(outlet.getAddress().getLatitude()), Double.parseDouble(outlet.getAddress().getLongitude()));
                    productDTO.setDistance(dist);
                }
                productDTO.setEmail(outlet.getMerchantDetails().getUser().getEmail());
                productDTO.setContactNumber(outlet.getMerchantDetails().getUser().getContactNumber());
                productDTO.setShopAddress(outlet.getMerchantDetails().getShopAddress());
                productDTO.setFullAddress(outlet.getMerchantDetails().getFullAddress());
            }
///////////////////////////////////////////
            productDTOs.add(productDTO);
        }
        productDTOs.sort(Comparator.comparing(ProductDTO::getDistance));

        return new PageItem<>(pagedResult.getTotalPages(), pagedResult.getTotalElements(), productDTOs, pagedInfo.page,
                pagedInfo.items);
    }

    private void prepareProductSearchQuery(ProductFilterRequest filterRequest, GenericSpecificationsBuilder<Product> builder) {

        builder.with(productSpecificationFactory.isEqual("deleted", false));
        if (StringUtils.isNotBlank(filterRequest.getMerchantId())) {
            MerchantDetails merchantDetails = new MerchantDetails();
            merchantDetails.setId(filterRequest.getMerchantId());
            builder.with(productSpecificationFactory.isEqual("merchantDetails", merchantDetails));
        }
        if (StringUtils.isNotBlank(filterRequest.getCategoryName())) {
            if (StringUtils.isNotBlank(filterRequest.getSubcategoryName())) {
                ApiResponse subCategoryResponse = adminFeignClient.getSubCategoryByName(filterRequest.getSubcategoryName());
                if (subCategoryResponse.getHttpStatus().is2xxSuccessful()) {
                    if (subCategoryResponse.getData() != null) {
                        LinkedHashMap<String, Object> data = (LinkedHashMap<String, Object>) subCategoryResponse.getData();
                        builder.with(productSpecificationFactory.isEqual("subcategoryId", data.get("id")));
                    }
                } else {
                    throw new ObjectNotFoundException("Subcategory not found for given name: " + filterRequest.getSubcategoryName());
                }
            } else {
                ApiResponse categoryResponse = adminFeignClient.getCategoryByName(filterRequest.getCategoryName());
                if (categoryResponse.getHttpStatus().is2xxSuccessful()) {
                    if (categoryResponse.getData() != null) {
                        LinkedHashMap<String, Object> data = (LinkedHashMap<String, Object>) categoryResponse.getData();
                        builder.with(productSpecificationFactory.isEqual("categoryId", data.get("id")));
                    }
                } else {
                    throw new ObjectNotFoundException("Category not found for given name: " + filterRequest.getCategoryName());
                }
            }
        }


        if (StringUtils.isNotBlank(filterRequest.getName())) {
            builder.with(productSpecificationFactory.like("name", filterRequest.getName()));
        }
        if (filterRequest.getStatus() != null) {
            builder.with(productSpecificationFactory.isEqual("isActive", filterRequest.getStatus()));
        }
        if (filterRequest.getStartDate() != null) {
            builder.with(productSpecificationFactory.isGreaterThan("createdAt", filterRequest.getStartDate().atStartOfDay()));
        }
        if (filterRequest.getEndDate() != null) {
            builder.with(productSpecificationFactory.isLessThan("createdAt", filterRequest.getEndDate().plusDays(1).atStartOfDay()));
        }

    }

    @Override
    public PageItem<ProductDTO> getAllProductByStatus(PagedItemInfo pagedInfo, Boolean status) {

        final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(Product.class, pagedInfo);

        Page<Product> pagedResult = productRepository.findAllProducts(pageRequest);
        List<ProductDTO> productDTOs = new ArrayList<>();
        for (Product product : pagedResult.getContent()) {
            productDTOs.add(mapProductToProductDTO(product));
        }
        return new PageItem<>(pagedResult.getTotalPages(), pagedResult.getTotalElements(), productDTOs, pagedInfo.page,
                pagedInfo.items);
    }


    @Transactional
    @Override
    public Map<String, List<String>> deleteProducts(List<String> productIds,MerchantDetails merchantDetails) {
//        List<Product> productList = productRepository.fineByDeleteTrueAndIdIn(productIds);

        List<String> availableProducts = new ArrayList<>();
        List<String> notAvailableProducts = new ArrayList<>();
        Map<String, List<String>> listMap = new HashMap<>();

        for (String productId : productIds) {
            Product product = productRepository.findById(productId).orElse(null);
            if (product != null) {
                availableProducts.add(productId);
            } else {
                notAvailableProducts.add(productId);
            }
        }
        listMap.put("availableProducts", availableProducts);
        listMap.put("notAvailableProducts", notAvailableProducts);
        productRepository.deleteProducts(availableProducts);

        return listMap;
    }

    @Transactional
    @Override
    public Map<String, List<String>> updateProductStatus(List<String> productIds, boolean isActive) {
        List<String> products = new ArrayList<>();
        List<String> availableProducts = new ArrayList<>();
        List<String> notAvailableProducts = new ArrayList<>();
        Map<String, List<String>> listMap = new HashMap<>();
        for (String productId : productIds) {
            Product product = productRepository.findById(productId).orElse(null);
            if (product != null) {
                availableProducts.add(productId);
            } else {
                notAvailableProducts.add(productId);
            }
        }
        listMap.put("availableProducts", availableProducts);
        listMap.put("notAvailableProducts", notAvailableProducts);
        productRepository.updateProductStatus(productIds, isActive);
        return listMap;
    }

    @Transactional
    @Override
    public void changeProductStatus(String id, Boolean status,MerchantDetails merchantDetails) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            product.get().setActive(status);
            product.get().setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
            productRepository.save(product.get());
        } else {
            throw new ObjectNotFoundException("Product not found for the given id: " + id);
        }
    }

    @Override
    public boolean isSubcategoryAssociated(String subcategoryId, Pageable pageable) {
        Page<Product> product = productRepository.findBySubcategoryId(subcategoryId, PageRequest.of(0, 1));
        if (product.isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isCategoryAssociated(String categoryId, Pageable pageable) {
        Page<Product> product = productRepository.findByCategoryId(categoryId, PageRequest.of(0, 1));
        if (product.isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public ProductDTO findByName(String productName) {

        Product product = productRepository.findByName(productName);
        ProductDTO productDTO = mapProductToProductDTO(product);
        return productDTO;
    }

    @Override
    public String findProductNameById(String productId) {
        return productRepository.findProductNameById(productId);
    }

    @Override
    public String findByNameAndDeleted(String id, boolean isDeleted) {
        String productId = productRepository.findByProductName(id);
        return productId;
    }

    @Transactional
    @Override
    public void updateProductStock(String productId, Integer stockCount,String stockStatus,MerchantDetails merchantDetails) {
        productRepository.updateProductStatus(productId,stockCount,stockStatus,merchantDetails);
    }

    @Transactional
    @Override
    public void updateProductStock(String productId, Integer stockCount, String stockStatus) {
        productRepository.updateProductStatus(productId,stockCount,stockStatus);
    }

    @Override
    public long getAllProductProductCount() {
        return productRepository.count();
    }

    private Product mapProductDTOToProduct(ProductDTO productDTO) {
        Product product = new Product();
        product.setId(productDTO.getProductId());
        product.setDescription(productDTO.getDescription());
        product.setActualPrice(productDTO.getActualPrice());
        product.setDealPrice(productDTO.getDealPrice());
        product.setName(productDTO.getName());
        product.setImage(productDTO.getImage());
        product.setTaxId(productDTO.getTaxId());
        return product;
    }


}

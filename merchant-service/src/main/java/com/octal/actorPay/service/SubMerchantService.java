package com.octal.actorPay.service;

import com.octal.actorPay.dto.*;
import com.octal.actorPay.dto.request.SubmerchantFilterRequest;
import com.octal.actorPay.entities.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public interface SubMerchantService {

    /**
     * Get User details by user email id
     * @param email - registered user email id
     * @return - return user object
     */
    User getUserByEmailId(String email);

    User create(SubMerchantDTO subMerchantDTO, String currentUser);
    @Transactional
    void update(SubMerchantDTO subMerchantDTO, String currentUser) throws Exception;

    Map delete(List<String> ids, String currentUser) throws InterruptedException;

    SubMerchantDTO getSubMerchantDetails(String id, String currentUser);

    /**
     * get all the sub admin list
     * @param pagedInfo - contains pagination parameters
     * @return - returns pageItem Sub admin dto that contains list of sub admins
     */
    PageItem<SubMerchantDTO> getAllSubMerchantsPaged(PagedItemInfo pagedInfo, SubmerchantFilterRequest submerchantFilterRequest, String currentUser);

    void changeSubMerchantStatus(String id, Boolean status);

    MerchantOutletDTO associateMerchantToOutlet(String merchantId,String outletId);

    void disassociateMerchantFromOutlet(String merchantId,String outletId);

    MerchantSubMerchantAssoc findBySubmerchantId(String submerchantId);
 }

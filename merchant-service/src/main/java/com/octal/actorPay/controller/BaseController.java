package com.octal.actorPay.controller;

import com.octal.actorPay.constants.ResourceType;
import com.octal.actorPay.constants.UserType;
import com.octal.actorPay.dto.MerchantResponse;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.exceptions.ActorPayException;
import com.octal.actorPay.service.MerchantService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;

public class BaseController {

    @Autowired
    private MerchantService merchantService;

    protected PagedItemInfo createPagedInfo(int pageNo, int pageSize, String sortingField, boolean asc, String filterQuery) {
        // Requested pages started from 0 if you want to start from one then do pageNo - 1
        return new PagedItemInfo(pageNo, pageSize, sortingField, asc, filterQuery);
    }

    protected MerchantResponse getSecurityContext(HttpServletRequest request) {
        Object principal =
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = (String) principal;
        System.out.println("User has authorities: " + email);
        MerchantResponse merchantResponse = merchantService.findMerchantBasicData(email);
//        String userId = (String)request.getAttribute("userId");
//        String merchantId = (String) request.getAttribute("merchantId");
//        System.out.println("User Id #### " + userId);
//        System.out.println("Merchant Id #####  " + merchantId);
        return merchantResponse;
    }

    protected User findByPrimaryMerchantBySubMerchantId(String userName) {

        User user = merchantService.getUserByEmailId(userName);
        if (user.getUserType().equalsIgnoreCase(UserType.admin.name())) {
            return user;
        } else {
            if (user.getMerchantDetails().getResourceType().equals(ResourceType.SUB_MERCHANT)) {
                user = merchantService.findByPrimaryMerchantBySubMerchantId(user.getMerchantDetails().getId());
                return user;
            }
        }
        return user;
    }

    protected User getAuthorizedUser(HttpServletRequest request) {
        String userName = request.getHeader("userName");
        if (StringUtils.isNotBlank(userName)) {
            User user = findByPrimaryMerchantBySubMerchantId(userName);
            return user;
        } else {
            throw new ActorPayException(String.format("Invalid user %s ", userName));
        }
    }
}
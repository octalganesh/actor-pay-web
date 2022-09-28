package com.octal.actorPay.utils;

import com.octal.actorPay.entities.User;
import com.octal.actorPay.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class CommonService {

    @Autowired
    private MerchantService merchantService;

    public CommonService(MerchantService merchantService) {
        this.merchantService=merchantService;
    }
    public User getLoggedInUser(HttpServletRequest request) {
        String userName = request.getHeader("userName");
        User user = merchantService.getUserByEmailId(userName);
        return user;
    }
}

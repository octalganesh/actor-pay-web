package com.octal.actorPay.utils;

import com.netflix.discovery.converters.Auto;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.feign.clients.MerchantClient;
import com.octal.actorPay.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class CommonService {

    @Autowired
    private UserService userService;

    public CommonService(UserService userService,MerchantClient merchantClient) {
        this.userService=userService;
    }
    public User getLoggedInUser(HttpServletRequest request) {
        String userName = request.getHeader("userName");
        User user = userService.getUserByEmailId(userName);
        return user;
    }


}

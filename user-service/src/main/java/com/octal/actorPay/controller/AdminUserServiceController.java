package com.octal.actorPay.controller;

import com.octal.actorPay.feign.clients.AdminClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminUserServiceController {


    private AdminClient adminClient;
    public AdminUserServiceController(AdminClient adminClient) {
        this.adminClient=adminClient;
    }
    @GetMapping(value = "/auth/details/by/email/{email}")
    public ResponseEntity getUserByUserName(@PathVariable("email") String email) {
        return adminClient.getUserByEmailId(email);
    }
}

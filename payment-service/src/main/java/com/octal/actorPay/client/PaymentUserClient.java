package com.octal.actorPay.client;

import com.octal.actorPay.configs.FeignSupportConfig;
import com.octal.actorPay.constants.EndPointConstants;
import com.octal.actorPay.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = EndPointConstants.USER_MICROSERVICE,
        url = "http://localhost:8084/",
        configuration = FeignSupportConfig.class)
public interface PaymentUserClient {


    @GetMapping(value = "/get/user/{username}")
    public ResponseEntity getCustomer(@PathVariable(name = "username") String username);

    @GetMapping(value = "/users/by/id/{id}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable(name = "id") String id);

    @GetMapping(value = "/users/get/user/identity/{userIdentity}")
    ResponseEntity<ApiResponse> getUserIdentity(@PathVariable(name = "userIdentity") String userIdentity);
}

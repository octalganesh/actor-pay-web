package com.octal.actorPay.controller;


import com.octal.actorPay.client.GlobalServiceFeignClient;
import com.octal.actorPay.dto.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserGlobalSettingsController {

    private GlobalServiceFeignClient globalServiceFeignClient;

    public UserGlobalSettingsController(GlobalServiceFeignClient globalServiceFeignClient) {
        this.globalServiceFeignClient = globalServiceFeignClient;
    }

    @PostMapping("/users/getConfig")
    ApiResponse getConfig(@RequestBody String key) {
        return globalServiceFeignClient.getConfig(key);
    }
}

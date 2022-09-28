package com.octal.actorPay.client;

import com.octal.actorPay.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "global-settings", url = "http://localhost:8089/")
//@Service
public interface GlobalServiceFeignClient {

    @PostMapping("/v1/global/setting/getConfig")
    ApiResponse getConfig(@RequestBody String key);
}


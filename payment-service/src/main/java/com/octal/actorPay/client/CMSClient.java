package com.octal.actorPay.client;

import com.octal.actorPay.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "cms-service", url = "http://localhost:8088/")
@Service
public interface CMSClient {

    @RequestMapping(value = "/notification-content/{slug}",method = RequestMethod.GET)
    ApiResponse getNotificationContent(@PathVariable("slug") String slug);
}
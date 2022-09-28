package com.octal.actorPay.feign.clients;

import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.EmailDTO;
import com.octal.actorPay.dto.FcmUserNotificationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "cms-service", url = "http://localhost:8088/")
@Service
public interface CMSClient {

    /**
     * This method  internally calls CMS service to get Content
     * @param emailPropertyDTO - EmailDTO object
     */

    @RequestMapping(value = "/notification-content/{slug}",method = RequestMethod.GET)
    ApiResponse getNotificationContent(@PathVariable("slug") String slug);
}
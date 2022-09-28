package com.octal.actorPay.feign;

import com.octal.actorPay.constants.EndPointConstants;
import com.octal.actorPay.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = EndPointConstants.CMSServiceConstants.CMS_MICROSERVICE, url = EndPointConstants.CMSServiceConstants.CMS_BASE_URL)
@Service
public interface CmsServiceFeignClient {

    @RequestMapping(value = EndPointConstants.CMSServiceConstants.GET_EMAIL_TEM_BY_ID, method = RequestMethod.GET)
    ApiResponse getEmailTemplateById(@RequestParam(name = "id") String cmsId);


    @GetMapping(value = "/email-template/by/slug")
    ApiResponse getEmailTemplateBySlug(@RequestParam(name = "slug") String slug);

}
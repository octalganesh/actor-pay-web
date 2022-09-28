package com.octal.actorPay.client;

import com.octal.actorPay.constants.EndPointConstants;
import com.octal.actorPay.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.util.List;
import java.util.Map;

import static com.octal.actorPay.constants.EndPointConstants.MerchantServiceConstants.MERCHANT_BASE_URL;

@FeignClient(name = EndPointConstants.CMSServiceConstants.CMS_MICROSERVICE, url = EndPointConstants.CMSServiceConstants.CMS_BASE_URL)
@Service
public interface CMSFeignClient {

    @RequestMapping(value = "/faq/all/paged", method = RequestMethod.GET)
    ApiResponse getAllFAQData(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                              @RequestParam(name = "sortBy", defaultValue = "updatedAt") String sortBy,
                              @RequestParam(name = "asc", defaultValue = "false") boolean asc,
                              @RequestParam(name = "question",defaultValue = "") String question);

    @RequestMapping(value = EndPointConstants.CMSServiceConstants.GET_ALL_CMS_URL, method = RequestMethod.GET)
    ApiResponse getAllCMSData(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                              @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                              @RequestParam(name = "asc", defaultValue = "false") boolean asc,
                              @RequestParam(name = "title",defaultValue = "") String title);

    @RequestMapping(value = EndPointConstants.CMSServiceConstants.GET_LOYAL_REWARD_SETTING_DATA_URL, method = RequestMethod.GET)
    ApiResponse getLoyalPointsSettingData();

    @RequestMapping(value = EndPointConstants.CMSServiceConstants.CREATE_UPDATE_LOYAL_REWARD_SETTING_DATA_URL, method = RequestMethod.POST)
    ApiResponse createAndUpdateLoyalRewardPointsData(LRPointsDTO lrPointsDTO);

    @RequestMapping(value = EndPointConstants.CMSServiceConstants.READ_CMS_BY_ID_URL, method = RequestMethod.GET)
    ApiResponse readCmsById(@RequestParam(name = "id") String cmsId);

    @RequestMapping(value = EndPointConstants.CMSServiceConstants.GET_ALL_EMAIL_TEMPLATES_URL, method = RequestMethod.GET)
    ApiResponse getAllEmailTemplates(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                     @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                     @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                     @RequestParam(name = "asc", defaultValue = "asc") boolean asc,
                                     @RequestParam(name = "title", required = false) String title,
                                     @RequestParam(name = "emailSubject",required = false) String emailSubject,
                                     @RequestParam(name = "isActive",required = false) Boolean isActive);

    @RequestMapping(value = EndPointConstants.CMSServiceConstants.GET_FAQ_BY_ID_URL, method = RequestMethod.GET)
    ApiResponse getFAQDataByID(@RequestParam(name = "id") String cmsId);

    @RequestMapping(value = EndPointConstants.CMSServiceConstants.ADD_NEW_FAQ, method = RequestMethod.POST)
    ApiResponse addNewFAQ(@RequestBody FaqDTO faqDTO);

    @RequestMapping(value = EndPointConstants.CMSServiceConstants.UPDATE_FAQ_URL, method = RequestMethod.PUT)
    ApiResponse updateFAQ(@RequestBody FaqDTO faqDTO);

    @RequestMapping(value = EndPointConstants.CMSServiceConstants.CMS_UPDATE_URL, method = RequestMethod.PUT)
    ApiResponse updateCMS(@RequestBody CmsDTO cmsDTO);

    // We can use in future if needed
//    @PostMapping("/cms/add")
//    ResponseEntity<?> addNewCmsData(@RequestBody @Valid CmsDTO cmsDTO);

    @RequestMapping(value = EndPointConstants.CMSServiceConstants.EMAIL_TEMP_UPDATE_URL, method = RequestMethod.PUT)
    ApiResponse emailTempUpdate(@RequestBody EmailTemplateDTO emailTemplateDTO);

    @RequestMapping(value = EndPointConstants.CMSServiceConstants.DELETE_FAQ_BY_IDs, method = RequestMethod.DELETE)
    ApiResponse deleteFAQByIds(@RequestBody Map<String, List<String>> data);

    @DeleteMapping("/faq/delete/by/id")
    ApiResponse deleteFAQById(@RequestParam(name = "id") String id);

    @RequestMapping(value = EndPointConstants.CMSServiceConstants.GET_EMAIL_TEM_BY_ID, method = RequestMethod.GET)
    ApiResponse getEmailTemplateById(@RequestParam(name = "id") String cmsId);

    @PostMapping("/email-template/add")
    ApiResponse addEmailTemplates(@RequestBody @Valid EmailTemplateDTO emailTempDTO);

    @GetMapping(value = "/email-template/by/slug")
    ApiResponse getEmailTemplateBySlug(@RequestParam(name = "slug") String slug);

    @DeleteMapping("/email-template/delete/by/id")
    ApiResponse deleteEmailTemplate(@RequestParam(name = "id") String id);
}
package com.octal.actorPay.controller;

import com.octal.actorPay.dto.*;
import com.octal.actorPay.dto.request.EmailTemplateFilterRequest;
import com.octal.actorPay.entities.Cms;
import com.octal.actorPay.entities.EmailTemplate;
import com.octal.actorPay.entities.NotificationContent;
import com.octal.actorPay.service.CmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
public class CmsController extends PagedItemsController {

    @Autowired
    private CmsService cmsService;

    /***************************** CMS **************************/

    @GetMapping(value = "/cms/by/id")
    public ResponseEntity getCMSDataById(@RequestParam("id") String id) {
        return new ResponseEntity<>(new ApiResponse("CMS Data",
                cmsService.getCMSDataById(id), String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping(value = "/cms/all")
    public ResponseEntity getCMSDataPaged(@RequestParam(defaultValue = "0") Integer pageNo,
                                          @RequestParam(defaultValue = "10") Integer pageSize,
                                          @RequestParam(defaultValue = "updatedAt") String sortBy,
                                          @RequestParam(defaultValue = "false") boolean asc,
                                          @RequestParam(name = "title", defaultValue = "") String title,
                                          HttpServletRequest request) {

        final PagedItemInfo pagedInfo = createPagedInfo(pageNo, pageSize, sortBy, asc, null);

        return new ResponseEntity<>(new ApiResponse("All CMS data",
                cmsService.getCMSDataPaged(pagedInfo, request.getHeader("userName"), title),
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PutMapping("/cms/update")
    public ResponseEntity<?> updateCMSData(@RequestBody @Valid CmsDTO cmsDTO) {
        Cms cms = cmsService.updateCMSData(cmsDTO);
        if (cms != null) {
            return new ResponseEntity<>(new ApiResponse("CMS content updated successfully", null,
                    String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponse("Something went wrong, CMS content update failed", null,
                String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    // We can use in future if needed
//    @PostMapping("/cms/add")
//    public ResponseEntity<?> addNewCmsData(@RequestBody @Valid CmsDTO cmsDTO) {
//        Cms cms = cmsService.updateCMSData(cmsDTO);
//        if (cms != null) {
//            return new ResponseEntity<>(new ApiResponse("CMS content added successfully",null,
//                    String.valueOf(HttpStatus.OK.value()),HttpStatus.OK), HttpStatus.OK);
//        }
//        return new ResponseEntity<>("Something went wrong, CMS content update failed", HttpStatus.INTERNAL_SERVER_ERROR);
//    }

    /*********************EMAIL TEMPLATE*************************/

    @GetMapping(value = "/email-template/by/id")
    public ResponseEntity getEmailTemplateById(@RequestParam("id") String id) {
        return new ResponseEntity<>(new ApiResponse("CMS Data",
                cmsService.getEmailTemplateById(id), String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping(value = "/email-template/by/slug")
    public ResponseEntity getEmailTemplateBySlug(@RequestParam("slug") String slug) {

        return new ResponseEntity<>(new ApiResponse("CMS Data",
                cmsService.getEmailTemplateBySlug(slug), String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }
    @GetMapping(value = "/notification-content/{slug}")
    public ResponseEntity getNotificationContentBySlug(@PathVariable("slug") String slug) {
        NotificationContent response = cmsService.getNotificationContentBySlug(slug);
        if(response != null){
            return new ResponseEntity<>(new ApiResponse("Notification Content.",
                    cmsService.getNotificationContentBySlug(slug), String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(new ApiResponse("Notification Content.",
                    null, "101", HttpStatus.OK), HttpStatus.OK);
        }

    }

    @GetMapping(value = "/email-template/all")
    public ResponseEntity getEmailTemplatesPaged(@RequestParam(defaultValue = "0") Integer pageNo,
                                                 @RequestParam(defaultValue = "10") Integer pageSize,
                                                 @RequestParam(defaultValue = "updatedAt") String sortBy,
                                                 @RequestParam(defaultValue = "false") boolean asc,
                                                 @RequestParam(name = "title", required = false) String title,
                                                 @RequestParam(name = "emailSubject", required = false) String emailSubject,
                                                 @RequestParam(name = "isActive", required = false) Boolean isActive,
                                                 HttpServletRequest request) {
        final PagedItemInfo pagedInfo = createPagedInfo(pageNo, pageSize, sortBy, asc, null);
        EmailTemplateFilterRequest filterRequest = new EmailTemplateFilterRequest();
        filterRequest.setEmailSubject(emailSubject);
        filterRequest.setTitle(title);
        filterRequest.setStatus(isActive);
        PageItem<EmailTemplateDTO> emailTemplateDTOPageItem =
                cmsService.getEmailTemplatesPaged(pagedInfo, request.getHeader("userName"), title, filterRequest);
        if (emailTemplateDTOPageItem != null) {
            return new ResponseEntity<>(new ApiResponse("All Email Template Data",
                    cmsService.getEmailTemplatesPaged(pagedInfo, request.getHeader("userName"), title, filterRequest),
                    String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse("All Email Template Data is Empty", "",
                    String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        }

    }

    @PutMapping("/email-template/update")
    public ResponseEntity<?> updateEmailTemplates(@RequestBody @Valid EmailTemplateDTO emailTempDTO) {
        EmailTemplate emailTemp = cmsService.updateEmailTemplate(emailTempDTO);
        if (emailTemp != null) {
            return new ResponseEntity<>(new ApiResponse("Email Template updated successfully", null,
                    String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        }
//        return new ResponseEntity<>("Something went wrong, Email Template update failed", HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(new ApiResponse("Something went wrong, Email Template update failed", null,
                String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/email-template/delete/by/id")
    public ResponseEntity<?> deleteEmailTemplate(@RequestParam(name = "id") String id) {
        cmsService.deleteEmailTemplate(id);
        return new ResponseEntity<>(new ApiResponse("Email Template Deleted Successfully", null,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }
    @DeleteMapping("/faq/delete/by/id")
    public ResponseEntity<?> deleteFAQById(@RequestParam(name = "id") String id) {
        cmsService.deleteFAQById(id);
        return new ResponseEntity<>(new ApiResponse("FAQ Deleted Successfully", null,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping("/email-template/add")
    public ResponseEntity<?> addEmailTemplates(@RequestBody @Valid EmailTemplateDTO emailTempDTO) {
        EmailTemplate emailTemp = cmsService.addEmailTemplate(emailTempDTO);
        if (emailTemp != null) {
            return new ResponseEntity<>(new ApiResponse("Email Template added successfully", emailTemp,
                    String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        }
        return new ResponseEntity<>("Something went wrong, Email Template update failed", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /***********************FAQ************************/

    @GetMapping(value = "/faq/all/paged")
    public ResponseEntity getAllFaqDataPaged(@RequestParam(defaultValue = "0") Integer pageNo,
                                             @RequestParam(defaultValue = "10") Integer pageSize,
                                             @RequestParam(defaultValue = "updatedAt") String sortBy,
                                             @RequestParam(defaultValue = "false") boolean asc,
                                             @RequestParam(defaultValue = "") String question,
                                             HttpServletRequest request) {

        final PagedItemInfo pagedInfo = createPagedInfo(pageNo, pageSize, sortBy, asc, null);

        return new ResponseEntity<>(new ApiResponse("All FAQ data",
                cmsService.getFAQDataPaged(pagedInfo, request.getHeader("userName"), question),
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping(value = "/faq/by/id")
    public ResponseEntity getFaqById(@RequestParam("id") String id) {
        return new ResponseEntity<>(new ApiResponse("FAQ Data",
                cmsService.getFAQDataById(id), String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping("/faq/add/new")
    public ResponseEntity<ApiResponse> createFaq(@RequestBody @Valid FaqDTO faqDTO) {
        cmsService.createFaq(faqDTO);
        return new ResponseEntity<>(new ApiResponse("FAQ created successfully", null,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PutMapping("/faq/update")
    public ResponseEntity<ApiResponse> updateFaq(@RequestBody @Valid FaqDTO faqDTO) {
        cmsService.updateFaq(faqDTO);
        return new ResponseEntity<>(new ApiResponse("FAQ updated successfully", null,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @DeleteMapping("faq/delete/by/ids")
    public ResponseEntity<ApiResponse> deleteFaq(@RequestBody Map<String, List<String>> data,
                                                 HttpServletRequest request) throws InterruptedException {
        cmsService.deleteFaq(data);
        return new ResponseEntity<>(new ApiResponse("FAQ deleted successfully", null,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }


    @GetMapping(value = "/faq/all")
    public ResponseEntity getAllFaqData(HttpServletRequest request) {

        return new ResponseEntity<>(new ApiResponse("All FAQ data",
                cmsService.getListOfFAQ(),
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }


    /*************************** LRP ***************************/

    @GetMapping(value = "/get/loyal/reward/points/setting/data")
    public ResponseEntity getLrp() {
        return new ResponseEntity<>(new ApiResponse("Loyalty Rewards Points Data",
                cmsService.getLrpData(), String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping("/loyal/reward/points/create/or/update")
    public ResponseEntity<ApiResponse> createOrUpdateLrp(@RequestBody @Valid LRPointsDTO lrpDTO) {
        cmsService.createOrUpdateLrp(lrpDTO);
        return new ResponseEntity<>(new ApiResponse("LRP updated successfully", null,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

      /*
    This code can be used later to implement delete or create CMS operation.

    @PutMapping("/create/contents")
    public ResponseEntity<Void> createAboutUs(@Valid @RequestBody CmsDTO cmsDTO, HttpServletRequest request) {
        cmsService.createContents(cmsDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @DeleteMapping("/delete/about-us")
    public ResponseEntity<ApiResponse> deleteAboutUs() throws InterruptedException {
        cmsService.deleteAboutUs();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/delete/policy")
    public ResponseEntity<ApiResponse> deletePolicy() throws InterruptedException {
        cmsService.deletePolicy();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/delete/terms")
    public ResponseEntity<ApiResponse> deleteTerms() throws InterruptedException {
        cmsService.deleteTerms();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/email-templates")
    public ResponseEntity getEmailTemplates() {
        return new ResponseEntity<>(new ApiResponse("All CMS data",cmsService.getEmailTemplates(),
                String.valueOf(HttpStatus.OK.value()),HttpStatus.OK), HttpStatus.OK);
    }*/

    @GetMapping(value = "/get/static/data/by/cms")
    public ResponseEntity getCMSByCMSType(@RequestParam("type") String cmsType) {
        return new ResponseEntity<>(new ApiResponse("Static content",
                cmsService.getCMSDataByCMSType(cmsType), String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

}
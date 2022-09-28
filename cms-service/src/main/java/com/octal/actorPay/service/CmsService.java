package com.octal.actorPay.service;

import com.octal.actorPay.dto.*;
import com.octal.actorPay.dto.request.EmailTemplateFilterRequest;
import com.octal.actorPay.entities.Cms;
import com.octal.actorPay.entities.EmailTemplate;
import com.octal.actorPay.entities.NotificationContent;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface CmsService {


    PageItem<CmsDTO> getCMSDataPaged(PagedItemInfo pagedInfo, String currentUser, String searchByTitle);
    CmsDTO getCMSDataById(String id);
    Cms updateCMSData(CmsDTO cmsDTO);
//    PageItem<CmsDTO> getCMSDataPaged(PagedItemInfo pagedInfo, String currentUser,
//                                            String searchByTitle, EmailTemplateFilterRequest filterRequest);
    PageItem<EmailTemplateDTO> getEmailTemplatesPaged(PagedItemInfo pagedInfo, String userName, String searchByTitle,EmailTemplateFilterRequest filterRequest);
    EmailTemplateDTO getEmailTemplateById(String id);
    EmailTemplate updateEmailTemplate(EmailTemplateDTO emailTempDTO);
    EmailTemplate addEmailTemplate(EmailTemplateDTO emailTempDTO);
    EmailTemplate getEmailTemplateBySlug(String slug);
    NotificationContent getNotificationContentBySlug(String slug);
    void deleteEmailTemplate(String id);
    void deleteFAQById(String id);

    PageItem<FaqDTO> getFAQDataPaged(PagedItemInfo pagedInfo, String userName, String searchByQuestion);
    FaqDTO getFAQDataById(String id);
    void createFaq(FaqDTO faqDTO);
    void updateFaq(FaqDTO faqDTO);
    void deleteFaq(Map<String, List<String>> data);

    LRPointsDTO getLrpData();
    void createOrUpdateLrp(LRPointsDTO lrpDTO);
    CmsDTO getCMSDataByCMSType(String cmsType);

    List<FaqDTO> getListOfFAQ();


//    This code can be used later for delete and create operation on CMS
//    Cms createContents(CmsDTO cmsDTO);
//    void deleteAboutUs() throws InterruptedException;
//    void deletePolicy() throws InterruptedException;
//    void deleteTerms() throws InterruptedException;
}
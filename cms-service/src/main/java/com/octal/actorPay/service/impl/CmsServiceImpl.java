package com.octal.actorPay.service.impl;

import com.octal.actorPay.dto.*;
import com.octal.actorPay.dto.request.EmailTemplateFilterRequest;
import com.octal.actorPay.dto.request.ProductFilterRequest;
import com.octal.actorPay.entities.*;
import com.octal.actorPay.exceptions.ExceptionUtils;
import com.octal.actorPay.exceptions.ObjectNotFoundException;
import com.octal.actorPay.repositories.*;
import com.octal.actorPay.service.CmsService;
import com.octal.actorPay.specification.GenericSpecificationsBuilder;
import com.octal.actorPay.specification.SpecificationFactory;
import com.octal.actorPay.transformer.*;
import com.octal.actorPay.utils.CommonUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CmsServiceImpl implements CmsService {

    public static final Logger LOGGER = LogManager.getLogger(CmsServiceImpl.class);

    private CmsRepository cmsRepository;

    private FAQRepository faqRepository;

    private LRPRepository lrpRepository;

    private EmailTemplateRepository emailTempRepo;

    private NotificationContentRepository notificationContentRepository;

    private SpecificationFactory<EmailTemplate> emailTemplateSpecificationFactory;

    /*************CMS***************/

    public CmsServiceImpl(CmsRepository cmsRepository, FAQRepository faqRepository,
                          LRPRepository lrpRepository, EmailTemplateRepository emailTempRepo,
                          NotificationContentRepository notificationContentRepository,
                          SpecificationFactory<EmailTemplate> emailTemplateSpecificationFactory) {
        this.cmsRepository = cmsRepository;
        this.faqRepository = faqRepository;
        this.lrpRepository = lrpRepository;
        this.emailTempRepo = emailTempRepo;
        this.notificationContentRepository = notificationContentRepository;
        this.emailTemplateSpecificationFactory = emailTemplateSpecificationFactory;
    }

    @Override
    public CmsDTO getCMSDataById(String id) {
        Optional<Cms> cms = cmsRepository.findById(id);
        if (cms.isPresent()) {
            return cms.map(content -> CmsTransformer.CMS_TO_DTO.apply(content)).orElse(null);
        }
        throw new ObjectNotFoundException("CMS data not found for the given id: " + id);

    }

    @Override
    public PageItem<CmsDTO> getCMSDataPaged(PagedItemInfo pagedInfo, String currentUser,
                                            String searchByTitle) {
        final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(Cms.class, pagedInfo);

        Page<Cms> pagedResult;
        if (!searchByTitle.trim().isEmpty()) {
            pagedResult = cmsRepository.findByTitleContainingIgnoreCase(searchByTitle, pageRequest);
        } else {
            pagedResult = cmsRepository.findAll(pageRequest);
        }
        List<CmsDTO> content = pagedResult.getContent().stream().map(CmsTransformer.CMS_TO_DTO)
                .collect(Collectors.toList());

        return new PageItem<>(pagedResult.getTotalPages(), pagedResult.getTotalElements(), content, pagedInfo.page,
                pagedInfo.items);
    }

    @Override
    public void deleteFAQById(String id) {
        try {
            Optional<FAQ> faq = faqRepository.findById(id);
            if (faq.isPresent()) {
                faqRepository.deleteById(id);
            } else {
                throw new ObjectNotFoundException("FAQ is not found for the given id: " + id);
            }
        } catch (Exception e) {
            if (e instanceof DataIntegrityViolationException) {
                DataIntegrityViolationException dataIntegrityViolationException = (DataIntegrityViolationException) e;
                String errorMessage = ExceptionUtils.duplicateCheckHandleException(dataIntegrityViolationException);
                throw new RuntimeException(errorMessage);
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public void deleteEmailTemplate(String id) {
        try {
            Optional<EmailTemplate> cms = emailTempRepo.findById(id);
            if (cms.isPresent()) {
                emailTempRepo.deleteById(id);
            } else {
                throw new ObjectNotFoundException("Email Template is not found for the given id: " + id);
            }
        } catch (Exception e) {
            if (e instanceof DataIntegrityViolationException) {
                DataIntegrityViolationException dataIntegrityViolationException = (DataIntegrityViolationException) e;
                String errorMessage = ExceptionUtils.duplicateCheckHandleException(dataIntegrityViolationException);
                throw new RuntimeException(errorMessage);
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
//    @Transactional(propagation = Propagation.REQUIRED)
    public Cms updateCMSData(CmsDTO cmsDTO) {
        try {
            Optional<Cms> cms = cmsRepository.findById(cmsDTO.getId());
            if (cms.isPresent()) {
                cms.get().setTitle(cmsDTO.getTitle());
                cms.get().setContents(cmsDTO.getContents());
                cms.get().setMetaTitle(cmsDTO.getMetaTitle());
                cms.get().setMetaKeyword(cmsDTO.getMetaKeyword());
                cms.get().setMetaData(cmsDTO.getMetaData());
                cms.get().setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
                cmsRepository.save(cms.get());
                return cms.get();
            } else {
                throw new ObjectNotFoundException("CMS data not found for the given id: " + cmsDTO.getId());
            }
        } catch (Exception e) {
            if (e instanceof DataIntegrityViolationException) {
                DataIntegrityViolationException dataIntegrityViolationException = (DataIntegrityViolationException) e;
                String errorMessage = ExceptionUtils.duplicateCheckHandleException(dataIntegrityViolationException);
                throw new RuntimeException(errorMessage);
            }
        }
        return null;
    }
    // We can use in future if needed
//    @Override
//    @Transactional(propagation = Propagation.REQUIRED)
//    public Cms updateCMSData(CmsDTO cmsDTO) {
//        if(StringUtils.isNotBlank(cmsDTO.getId())) {
//            Optional<Cms> cms = cmsRepository.findById(cmsDTO.getId());
//            if (cms.isPresent()) {
//                cms.get().setTitle(cmsDTO.getTitle());
//                cms.get().setContents(cmsDTO.getContents());
//                cms.get().setMetaTitle(cmsDTO.getMetaTitle());
//                cms.get().setMetaKeyword(cmsDTO.getMetaKeyword());
//                cms.get().setMetaData(cmsDTO.getMetaData());
//                cms.get().setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
//                Cms newCMS = cmsRepository.save(cms.get());
//                return newCMS;
//            }else{
//                throw new ObjectNotFoundException("CMS data not found for the given id: " + cmsDTO.getId());
//            }
//        } else {
//            Cms cmsToUpdate = CmsTransformer.CMS_DTO_TO_CMS.apply(cmsDTO);
//            cmsToUpdate = cmsRepository.save(cmsToUpdate);
//            return cmsToUpdate;
//        }
//    }
    // EMAIL TEMPLATE

    @Override
    public EmailTemplateDTO getEmailTemplateById(String id) {
        Optional<EmailTemplate> emailTemplate = emailTempRepo.findById(id);
        if (emailTemplate.isPresent()) {
            return emailTemplate.map(emailTemp -> EmailTemplateTransformer.EMAIL_TEMPLATE_TO_DTO.apply(emailTemp)).orElse(null);
        } else {
            throw new ObjectNotFoundException("Email Template not found for the given id: " + id);
        }
    }

    @Override
    public EmailTemplate getEmailTemplateBySlug(String slug) {
        return emailTempRepo.findBySlug(slug).orElse(null);
    }

    @Override
    public NotificationContent getNotificationContentBySlug(String slug) {
        Optional<NotificationContent> record = notificationContentRepository.findBySlug(slug);
        if (!record.isPresent()){
            return null;
        }
        return record.get();
    }

    @Override
    public PageItem<EmailTemplateDTO> getEmailTemplatesPaged(PagedItemInfo pagedInfo, String userName,
                                                             String searchByTitle, EmailTemplateFilterRequest filterRequest) {
        final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(EmailTemplate.class, pagedInfo);
        GenericSpecificationsBuilder<EmailTemplate> builder = new GenericSpecificationsBuilder<>();
        prepareEmailTemplateSearchQuery(filterRequest, builder);
        Page<EmailTemplate> pagedResult = emailTempRepo.findAll(builder.build(), pageRequest);
//        if (!searchByTitle.trim().isEmpty()) {
//            pagedResult = emailTempRepo.findByTitleContainingIgnoreCase(searchByTitle, pageRequest);
//        } else {
//            pagedResult = emailTempRepo.findAll(pageRequest);
//        }
        List<EmailTemplate> content = pagedResult.getContent();
        if (content != null && !content.isEmpty()) {
            List<EmailTemplateDTO> emailTemplateDTOS = content.stream().map(EmailTemplateTransformer.EMAIL_TEMPLATE_TO_DTO)
                    .collect(Collectors.toList());
            return new PageItem<>(pagedResult.getTotalPages(), pagedResult.getTotalElements(), emailTemplateDTOS, pagedInfo.page,
                    pagedInfo.items);
        }
        return null;
    }

    @Override
//    @Transactional(propagation = Propagation.REQUIRED)
    public EmailTemplate updateEmailTemplate(EmailTemplateDTO emailTempDTO) {
        try {
            Optional<EmailTemplate> emailTemp = emailTempRepo.findById(emailTempDTO.getId());
            if (emailTemp.isPresent()) {
                emailTemp.get().setTitle(emailTempDTO.getTitle());
                emailTemp.get().setEmailSubject(emailTempDTO.getEmailSubject());
                emailTemp.get().setContents(emailTempDTO.getContents());
                emailTemp.get().setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
                emailTemp.get().setSlug(emailTempDTO.getSlug());
                emailTempRepo.save(emailTemp.get());
                return emailTemp.get();
            } else {
                throw new RuntimeException("Email Template data not found for the given id: " + emailTempDTO.getId());
            }
        } catch (Exception e) {
            if (e instanceof DataIntegrityViolationException) {
                DataIntegrityViolationException dataIntegrityViolationException = (DataIntegrityViolationException) e;
                String errorMessage = ExceptionUtils.duplicateCheckHandleException(dataIntegrityViolationException);
                throw new RuntimeException(errorMessage);
            }
        }
        return null;
    }

    @Override
//    @Transactional(propagation = Propagation.REQUIRED)
    public EmailTemplate addEmailTemplate(EmailTemplateDTO emailTempDTO) {
        try {
            EmailTemplate emailTemp = new EmailTemplate();
            emailTemp.setTitle(emailTempDTO.getTitle());
            emailTemp.setEmailSubject(emailTempDTO.getEmailSubject());
            emailTemp.setContents(emailTempDTO.getContents());
            emailTemp.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
            emailTemp.setSlug(emailTempDTO.getSlug());
            EmailTemplate newEmailTemplate = emailTempRepo.save(emailTemp);
            return newEmailTemplate;
        } catch (Exception e) {
            if (e instanceof DataIntegrityViolationException) {
                DataIntegrityViolationException dataIntegrityViolationException = (DataIntegrityViolationException) e;
                String errorMessage = ExceptionUtils.duplicateCheckHandleException(dataIntegrityViolationException);
                throw new RuntimeException(errorMessage);
            }
        }
        return null;
    }

    /***************** FAQ *******************/

    @Override
    public PageItem<FaqDTO> getFAQDataPaged(PagedItemInfo pagedInfo, String userName, String searchByQuestion) {
        final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(FAQ.class, pagedInfo);

        Page<FAQ> pagedResult;
        if (!searchByQuestion.trim().isEmpty()) {
            pagedResult = faqRepository.findByQuestionContainingIgnoreCase(searchByQuestion, pageRequest);
        } else {
            pagedResult = faqRepository.findAll(pageRequest);
        }

        List<FaqDTO> faq = pagedResult.getContent().stream().map(FAQTransformer.FAQ_TO_DTO)
                .collect(Collectors.toList());

        return new PageItem<>(pagedResult.getTotalPages(), pagedResult.getTotalElements(), faq, pagedInfo.page,
                pagedInfo.items);
    }

    @Override
    public FaqDTO getFAQDataById(String id) {
        Optional<FAQ> faq = faqRepository.findById(id);
        if (faq.isPresent()) {
            return faq.map(faqObj -> FAQTransformer.FAQ_TO_DTO.apply(faqObj)).orElse(null);
        } else {
            throw new ObjectNotFoundException("FAQ data not found for the given id: " + id);
        }
    }

    @Override
//    @Transactional(propagation = Propagation.REQUIRED)
    public void createFaq(FaqDTO faqDTO) {
        // TODO add admin validation
        try {
            FAQ faq = new FAQ();
            faq.setQuestion(faqDTO.getQuestion());
            faq.setAnswer(faqDTO.getAnswer());
            faq.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
            faqRepository.save(faq);
        } catch (Exception e) {
            if (e instanceof DataIntegrityViolationException) {
                DataIntegrityViolationException dataIntegrityViolationException = (DataIntegrityViolationException) e;
                String errorMessage = ExceptionUtils.duplicateCheckHandleException(dataIntegrityViolationException);
                throw new RuntimeException(errorMessage);
            }
        }
    }

    @Override
//    @Transactional(propagation = Propagation.REQUIRED)
    public void updateFaq(FaqDTO faqDTO) {
        // TODO add admin validation
        try {
            Optional<FAQ> faq = faqRepository.findById(faqDTO.getId());
            if (faq.isPresent()) {
                faq.get().setQuestion(faqDTO.getQuestion());
                faq.get().setAnswer(faqDTO.getAnswer());
                faq.get().setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));

                faqRepository.save(faq.get());
            } else {
                throw new ObjectNotFoundException("FAQ not found for the given id: " + faqDTO.getId());
            }
        } catch (Exception e) {
            if (e instanceof DataIntegrityViolationException) {
                DataIntegrityViolationException dataIntegrityViolationException = (DataIntegrityViolationException) e;
                String errorMessage = ExceptionUtils.duplicateCheckHandleException(dataIntegrityViolationException);
                throw new RuntimeException(errorMessage);
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteFaq(Map<String, List<String>> data) {
        faqRepository.deleteInBatch(faqRepository.findAllById(data.get("faqIds")));
    }

    /***************** LRP **************/

    @Override
    public LRPointsDTO getLrpData() {
        Optional<LRP> lrp = Optional.ofNullable(lrpRepository.findAll().stream().findFirst().orElseThrow(() -> new ObjectNotFoundException("LRP data not found..")));
        return lrp.map(lrpObj -> LrpTransformer.LRP_TO_DTO.apply(lrpObj)).orElse(null);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void createOrUpdateLrp(LRPointsDTO lrpDTO) {
        // TODO add admin validation

        List<LRP> lrpList = lrpRepository.findAll();
        if (lrpList.isEmpty()) {
            LRP lrp = new LRP();
            lrp.setId(lrpDTO.getId());
            lrp.setAddMoney(lrpDTO.getAddMoney());
            lrp.setPayTransferToWallet(lrpDTO.getPayTransferToWallet());
            lrp.setPayNFC(lrpDTO.getPayNFC());
            lrp.setBuyDeals(lrpDTO.getBuyDeals());
            lrp.setThresholdValueToRedeemPoints(lrpDTO.getThresholdValueToRedeemPoints());
            lrp.setConversionRateOfLoyaltyPoints(lrpDTO.getConversionRateOfLoyaltyPoints());
            lrp.setConversionRateOfLoyaltyCurrency(lrpDTO.getConversionRateOfLoyaltyCurrency());

            lrp.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
            lrpRepository.save(lrp);
        } else {
            Optional<LRP> lrp = lrpRepository.findById(lrpDTO.getId());
            if (lrp.isPresent()) {
                lrp.get().setAddMoney(lrpDTO.getAddMoney());
                lrp.get().setPayTransferToWallet(lrpDTO.getPayTransferToWallet());
                lrp.get().setPayNFC(lrpDTO.getPayNFC());
                lrp.get().setBuyDeals(lrpDTO.getBuyDeals());
                lrp.get().setThresholdValueToRedeemPoints(lrpDTO.getThresholdValueToRedeemPoints());
                lrp.get().setConversionRateOfLoyaltyPoints(lrpDTO.getConversionRateOfLoyaltyPoints());
                lrp.get().setConversionRateOfLoyaltyCurrency(lrpDTO.getConversionRateOfLoyaltyCurrency());
                lrp.get().setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));

                lrpRepository.save(lrp.get());
            } else {
                throw new ObjectNotFoundException("LRP not found for the given id: " + lrpDTO.getId());
            }
        }
    }

    @Override
    public CmsDTO getCMSDataByCMSType(String cmsType) {
        Optional<Cms> cms = cmsRepository.findByCmsType(Integer.parseInt(cmsType));
        if (cms.isPresent()) {
            return cms.map(content -> CmsTransformer.CMS_TO_DTO.apply(content)).orElse(null);
        }
        throw new ObjectNotFoundException("CMS data not found for the given type: " + cmsType);
    }

    @Override
    public List<FaqDTO> getListOfFAQ() {
        return faqRepository.findAll().stream().map(FAQTransformer.FAQ_TO_DTO).collect(Collectors.toList());
    }

    private void prepareEmailTemplateSearchQuery(EmailTemplateFilterRequest filterRequest, GenericSpecificationsBuilder<EmailTemplate> builder) {

        builder.with(emailTemplateSpecificationFactory.isEqual("deleted", false));
        if (StringUtils.isNotBlank(filterRequest.getEmailSubject())) {

            builder.with(emailTemplateSpecificationFactory.like("emailSubject", filterRequest.getEmailSubject()));
        }

        if (StringUtils.isNotBlank(filterRequest.getTitle())) {
            builder.with(emailTemplateSpecificationFactory.like("title", filterRequest.getTitle()));
        }
        if (filterRequest.getStatus() != null) {
            builder.with(emailTemplateSpecificationFactory.isEqual("isActive", filterRequest.getStatus()));
        }
        if (filterRequest.getStartDate() != null) {
            builder.with(emailTemplateSpecificationFactory.isGreaterThan("createdAt", filterRequest.getStartDate().atStartOfDay()));
        }
        if (filterRequest.getEndDate() != null) {
            builder.with(emailTemplateSpecificationFactory.isLessThan("createdAt", filterRequest.getEndDate().plusDays(1).atStartOfDay()));
        }

    }

    /*
    This code can be used later for delete and create operation on CMS

    @Override
    public void deleteAboutUs() throws InterruptedException{
        Optional<Cms> cms = cmsRepository.findByCmsType(EndPointConstants.ABOUT_US);
        if(cms.isPresent()){
            cmsRepository.delete(cms.get());
        }
    }

    @Override
    public void deletePolicy() throws InterruptedException{
        Optional<Cms> cms = cmsRepository.findByCmsType(EndPointConstants.POLICY);
        if(cms.isPresent()){
            cmsRepository.delete(cms.get());
        }
    }

    @Override
    public void deleteTerms() throws InterruptedException {
        Optional<Cms> cms = cmsRepository.findByCmsType(EndPointConstants.TERMS);
        if(cms.isPresent()){
            cmsRepository.delete(cms.get());
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Cms createContents(CmsDTO cmsDTO) {
            Cms cms = new Cms();
            cms.setCmsType(cmsDTO.getCmsType());
            cms.setTitle(cmsDTO.getTitle());
            cms.setContents(cmsDTO.getContents());
            cms.setMetaTitle(cmsDTO.getMetaTitle());
            cms.setMetaKeyword(cmsDTO.getMetaKeyword());
            cms.setMetaData(cmsDTO.getMetaData());
            cms.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
            return cmsRepository.save(cms);
    }*/
}
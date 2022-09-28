package com.octal.actorPay.transformer;

import com.octal.actorPay.dto.CmsDTO;
import com.octal.actorPay.dto.FaqDTO;
import com.octal.actorPay.entities.Cms;
import com.octal.actorPay.entities.FAQ;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.function.Function;

public class FAQTransformer {

    public static Function<FAQ, FaqDTO> FAQ_TO_DTO = (faq) -> {

        FaqDTO faqDTO = new FaqDTO();

        faqDTO.setId(faq.getId());
        faqDTO.setQuestion(faq.getQuestion());
        faqDTO.setAnswer(faq.getAnswer());
        faqDTO.setUpdatedAt(faq.getUpdatedAt());
        faqDTO.setCreatedAt(faq.getCreatedAt());

        return faqDTO;
    };

    /*
    public static Function<User, AuthUserDTO> USER_TO_AUTH_DTO = (user) -> {

        AuthUserDTO authUserDTO = new AuthUserDTO();
        authUserDTO.setId(user.getId());
        authUserDTO.setContactNumber(user.getContactNumber());
        authUserDTO.setEmail(user.getEmail());
        authUserDTO.setExtensionNumber(user.getExtensionNumber());
        authUserDTO.setPassword(user.getPassword());

        return authUserDTO;
    };
     */
}
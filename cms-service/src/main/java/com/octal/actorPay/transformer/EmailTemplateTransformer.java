package com.octal.actorPay.transformer;

import com.octal.actorPay.dto.EmailTemplateDTO;
import com.octal.actorPay.entities.EmailTemplate;

import java.util.function.Function;

public class EmailTemplateTransformer {

    public static Function<EmailTemplate, EmailTemplateDTO> EMAIL_TEMPLATE_TO_DTO = (email) -> {

        EmailTemplateDTO emailTempDTO = new EmailTemplateDTO();

        emailTempDTO.setId(email.getId());
        emailTempDTO.setTitle(email.getTitle());
        emailTempDTO.setContents(email.getContents());
        emailTempDTO.setEmailSubject(email.getEmailSubject());
        emailTempDTO.setUpdatedAt(email.getUpdatedAt());

        return emailTempDTO;
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
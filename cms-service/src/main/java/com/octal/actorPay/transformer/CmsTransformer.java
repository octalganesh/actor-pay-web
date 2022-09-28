package com.octal.actorPay.transformer;

import com.octal.actorPay.dto.CmsDTO;
import com.octal.actorPay.entities.Cms;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.function.Function;

public class CmsTransformer {

    public static Function<Cms, CmsDTO> CMS_TO_DTO = (cms) -> {

        CmsDTO cmsDTO = new CmsDTO();

        cmsDTO.setId(cms.getId());
        cmsDTO.setCmsType(cms.getCmsType());
        cmsDTO.setTitle(cms.getTitle());
        cmsDTO.setContents(cms.getContents());
        cmsDTO.setMetaTitle(cms.getMetaTitle());
        cmsDTO.setMetaKeyword(cms.getMetaKeyword());
        cmsDTO.setMetaData(cms.getMetaData());
        cmsDTO.setUpdatedAt(cms.getUpdatedAt());
        return cmsDTO;
    };
    public static Function<CmsDTO, Cms> CMS_DTO_TO_CMS = (cmsDto) -> {

        Cms cms = new Cms();
        cms.setCmsType(cmsDto.getCmsType());
        cms.setTitle(cmsDto.getTitle());
        cms.setContents(cmsDto.getContents());
        cms.setMetaTitle(cmsDto.getMetaTitle());
        cms.setMetaKeyword(cmsDto.getMetaKeyword());
        cms.setMetaData(cmsDto.getMetaData());
        cms.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
        return cms;
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
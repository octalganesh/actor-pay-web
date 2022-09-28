package com.octal.actorPay.transformer;

import com.octal.actorPay.dto.AdminDTO;
import com.octal.actorPay.dto.AuthUserDTO;
import com.octal.actorPay.dto.ScreenDTO;
import com.octal.actorPay.dto.request.ContactUsDTO;
import com.octal.actorPay.entities.ContactUs;
import com.octal.actorPay.entities.Modules;
import com.octal.actorPay.entities.Screens;
import com.octal.actorPay.entities.User;

import java.util.function.Function;
public class AdminTransformer {


    public static final Function<ContactUs, ContactUsDTO> CONTACT_US_TO_DTO = (contact) ->{
        ContactUsDTO contactDto = new ContactUsDTO();
        contactDto.setMail(contact.getMail());
        contactDto.setName(contact.getName());
        contactDto.setType(contact.getType());
        contactDto.setText(contact.getText());

        return contactDto;
    };
    public static Function<User, AdminDTO> ADMIN_TO_DTO = (user) -> {

        AdminDTO userDTO = new AdminDTO();
        userDTO.setId(user.getId());
        userDTO.setContactNumber(user.getContactNumber());
        userDTO.setEmail(user.getEmail());
        userDTO.setExtensionNumber(user.getExtensionNumber());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setDateOfBirth(user.getDateOfBirth()!=null?user.getDateOfBirth().toString():null);
        userDTO.setCreatedAt(user.getCreatedAt());
        userDTO.setUserType(user.getUserType());

        return userDTO;
    };

    public static final Function<Screens,ScreenDTO> SCREENS_TO_DTO = (modules)->{
        ScreenDTO screenDTO = new ScreenDTO();
        screenDTO.setId(modules.getId());
        screenDTO.setName(modules.getScreenName());
        screenDTO.setScreenOrder(modules.getScreenOrder());
        return screenDTO;
    } ;



    public static Function<User, AuthUserDTO> USER_TO_AUTH_DTO = (user) -> {

        AuthUserDTO authUserDTO = new AuthUserDTO();
        authUserDTO.setId(user.getId());
        authUserDTO.setContactNumber(user.getContactNumber());
        authUserDTO.setEmail(user.getEmail());
//        authUserDTO.setUsername(user.getUsername());
        authUserDTO.setExtensionNumber(user.getExtensionNumber());
        authUserDTO.setPassword(user.getPassword());

        return authUserDTO;
    };
}
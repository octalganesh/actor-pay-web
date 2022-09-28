package com.octal.actorPay.transformer;

import com.octal.actorPay.dto.AuthUserDTO;
import com.octal.actorPay.dto.EkycFilterRequest;
import com.octal.actorPay.dto.UserDTO;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.entities.UserDocument;
import com.octal.actorPay.entities.UserSetting;
import com.octal.actorPay.repositories.SettingRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author - Naveen Kumawat
 * UserTransformer is used transform data in new object as per the requirement
 */
public class UserTransformer {

    public static Function<User, UserDTO> USER_TO_DTO = (user) -> {

        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setGender(user.getGender());
        userDTO.setDateOfBirth(user.getDateOfBirth() != null ? user.getDateOfBirth().toString() : null);
        userDTO.setEmail(user.getEmail());
        // TODO Set profile picture here with S3
        userDTO.setExtensionNumber(user.getExtensionNumber());
        userDTO.setContactNumber(user.getContactNumber());
        userDTO.setUserType(user.getUserType());
        // TODO set username here
        userDTO.setLastLoginDate(user.getLastLoginDate());
//        userDTO.setInvalidLoginAttempts(user.getInvalidLoginAttempts());
        userDTO.setActive(user.isActive());
        userDTO.setEkycStatus(user.getEkycStatus());
        userDTO.setAadhar(user.getAadharNumber());
        userDTO.setPanNumber(user.getPanNumber());
        // userDTO.setUserType(user.getUserType());


//        userDTO.setPassword(user.getPassword());
        // TODO userDTO.setRoles(user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toList()));

        userDTO.setCreatedAt(user.getCreatedAt());
        userDTO.setUpdatedAt(user.getUpdatedAt());
        userDTO.setReferralCode(user.getReferralCode());

        if(user.getProfilePicture() != null){
            userDTO.setProfilePicture(user.getProfilePicture());
        }

        return userDTO;
    };


    public static Function<UserDocument, EkycFilterRequest> USER_EKYC_TO_DTO = (userDocument) -> {

        EkycFilterRequest ekycFilterRequest = new EkycFilterRequest();
        ekycFilterRequest.setEkycStatus(userDocument.getEkycStatus());
        ekycFilterRequest.setDocumentData(userDocument.getDocumentData());
        ekycFilterRequest.setDocType(userDocument.getDocType());
        ekycFilterRequest.setStartDate(userDocument.getCreatedAt());
        ekycFilterRequest.setEndDate(userDocument.getUpdatedAt());
        ekycFilterRequest.setUser(userDocument.getUser());

        return ekycFilterRequest;
    };

    public static Function<User, AuthUserDTO> USER_TO_AUTH_DTO = (user) -> {

        AuthUserDTO authUserDTO = new AuthUserDTO();
        authUserDTO.setId(user.getId());
        authUserDTO.setContactNumber(user.getContactNumber());
        authUserDTO.setEmail(user.getEmail());
        authUserDTO.setUsername(user.getFirstName() + " " + user.getLastName());
        authUserDTO.setExtensionNumber(user.getExtensionNumber());
        authUserDTO.setPassword(user.getPassword());
        authUserDTO.setRole(user.getRole());

        return authUserDTO;
    };
}
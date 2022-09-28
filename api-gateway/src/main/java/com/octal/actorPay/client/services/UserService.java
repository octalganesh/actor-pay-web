package com.octal.actorPay.client.services;

import com.octal.actorPay.constants.EndPointConstants;
import com.octal.actorPay.dto.AuthUserDTO;
import com.octal.actorPay.dto.UserResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

import static com.octal.actorPay.constants.EndPointConstants.USER_MICROSERVICE;
import static com.octal.actorPay.constants.EndPointConstants.UserServiceConstants.FETCH_USER_BASE_URL;

/**
 * @author naveen.kumawat
 * UserService is a feignClient service which contains all user-service related apis
 */
@FeignClient(name = USER_MICROSERVICE, url = FETCH_USER_BASE_URL)
@Service
public interface UserService {

    /**
     * This api is used to fetch user details of the given username
     */
    @RequestMapping(value = EndPointConstants.UserServiceConstants.FETCH_USER_BY_EMAIL)
    Optional<AuthUserDTO> getUserByEmail(@PathVariable("email") String email);
}
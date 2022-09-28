package com.octal.actorPay.client.services;

import com.octal.actorPay.constants.EndPointConstants;
import com.octal.actorPay.dto.AuthUserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

import static com.octal.actorPay.constants.EndPointConstants.ADMIN_MICROSERVICE;
import static com.octal.actorPay.constants.EndPointConstants.AdminServiceConstants.FETCH_ADMIN_BASE_URL;
import static com.octal.actorPay.constants.EndPointConstants.MERCHANT_MICROSERVICE;
import static com.octal.actorPay.constants.EndPointConstants.MerchantServiceConstants.FETCH_MERCHANT_BASE_URL;

/**
 * @author naveen.kumawat
 * UserService is a feignClient service which contains all user-service related apis
 */
@FeignClient(name = MERCHANT_MICROSERVICE, url = FETCH_MERCHANT_BASE_URL)
@Service
public interface MerchantService {

    /**
     * This api is used to fetch user details of the given username
     */
    @RequestMapping(value = EndPointConstants.MerchantServiceConstants.FETCH_MERCHANT_BY_EMAIL)
    Optional<AuthUserDTO> getUserByEmail(@PathVariable("email") String email);
}
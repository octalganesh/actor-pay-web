package com.octal.actorPay.config;

import com.octal.actorPay.constants.EndPointConstants;
import com.octal.actorPay.dto.AuthUserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

import static com.octal.actorPay.constants.EndPointConstants.MERCHANT_MICROSERVICE;
import static com.octal.actorPay.constants.EndPointConstants.MerchantServiceConstants.FETCH_MERCHANT_BASE_URL;

/**
 * @author Nishant Saraswat
 * merchatService is a feignClient service which contains all merchat-service related apis
 */
@FeignClient(name = MERCHANT_MICROSERVICE, url = FETCH_MERCHANT_BASE_URL)
@Service
public interface MerchantService {

    @RequestMapping(value = EndPointConstants.MerchantServiceConstants.FETCH_MERCHANT_BY_EMAIL)
    Optional<AuthUserDTO> getUserByEmail(@PathVariable("email") String email);
}
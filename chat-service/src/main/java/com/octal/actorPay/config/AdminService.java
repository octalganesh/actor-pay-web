package com.octal.actorPay.config;

import com.octal.actorPay.constants.EndPointConstants;
import com.octal.actorPay.dto.AuthUserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Optional;

import static com.octal.actorPay.constants.EndPointConstants.ADMIN_MICROSERVICE;
import static com.octal.actorPay.constants.EndPointConstants.AdminServiceConstants.FETCH_ADMIN_BASE_URL;

/**
 * @author Nishant Saraswat
 * adminService is a feignClient service which contains all admin-service related apis
 */
@FeignClient(name = ADMIN_MICROSERVICE, url = FETCH_ADMIN_BASE_URL)
@Service
public interface AdminService {

    @RequestMapping(value = EndPointConstants.AdminServiceConstants.FETCH_ADMIN_BY_EMAIL, method = RequestMethod.GET)
    Optional<AuthUserDTO> getUserByEmail(@PathVariable("email") String email);
}
package com.octal.actorPay.controller;

import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.AuthenticationResponse;
import com.octal.actorPay.dto.LoginRequest;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.entities.UserOtpVerification;
import com.octal.actorPay.exceptions.AccessDeniedException;
import com.octal.actorPay.exceptions.InvalidPasswordException;
import com.octal.actorPay.exceptions.ActorPayException;
import com.octal.actorPay.exceptions.UnauthorisedException;
import com.octal.actorPay.jwt.JwtTokenProvider;
import com.octal.actorPay.service.AdminService;
import com.octal.actorPay.utils.ResponseUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

/**
 * @author -Naveen Kumawat
 * this class contains all the public accessable apis
 */

@Controller
public class AdminAuthenticationController {

    private static final Logger LOGGER = LogManager.getLogger(JwtTokenProvider.class);


    @Autowired
    private AdminService adminService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping(value = "/auth/login")
    public ResponseEntity<ApiResponse> userLogin(@Valid @RequestBody LoginRequest request) {
        if(adminService.getUserVerificationStatus(request.getEmail()).equals(UserOtpVerification.UserVerificationStatus.STATUS_PENDING)) {
            throw new AccessDeniedException("User account is not verified");
        }


        // check user account is active or not
        if (adminService.isUserAccountActive(request.getEmail())) {
            // TODO check that user is active or not, and other pre-conditions
            // validate user credential
        authenticate(request.getEmail(), request.getPassword());

        User user = adminService.getUserByEmailId(request.getEmail());
        if (user == null) {
            throw new UnauthorisedException("Invalid credential, please try with valid email or password");
        }

        AuthenticationResponse authenticationResponse = jwtTokenProvider.generateToken(user);
        return new ResponseEntity<>(ResponseUtils.ActorPayResponse("User login successfully",authenticationResponse,HttpStatus.OK), HttpStatus.OK);
        } else {
            throw new ActorPayException("User account is not active, please contact admin for account activation");
        }
    }

    /**
     * this API is used to get new access token when access token got expire, in which user pass refresh  token in request param and
     * then validate the refresh token and generate new access token
     *
     * @param token - refresh token
     * @return - if refresh token is valid then it generate new access token and
     * returns authenticated response with new token and user info
     * @throws Exception - if refresh token is not valid then throw error
     */
    @PostMapping(value = "/auth/token/refresh")
    public ResponseEntity<AuthenticationResponse> issueNewAccessTokenByRefreshToken(@RequestParam("token") String token) {

        // validate refresh token
        boolean isTokenValid = jwtTokenProvider.validateRefreshToken(token);
        LOGGER.debug("refresh token is valid: {} ", isTokenValid);

        // check refresh token is valid
        if (isTokenValid) {
            String username = jwtTokenProvider.getUsernameFromToken(token);

            //  final UserDetails userDetails = jwtDetailsService.loadUserByUsername(username);
            User user = adminService.getUserByEmailId(username);

            // generate access token from refresh token
            AuthenticationResponse response = jwtTokenProvider.generateTokenFromRefreshToken(user, token);
            return ResponseEntity.ok(response);
        } else {

            throw new ActorPayException("Invalid Refresh token");
        }
    }

    /**
     * authenticate user from given username and password
     *
     * @param username - registered username
     * @param password - user password
     */
    private void authenticate(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            LOGGER.info("User authenticated");

        } catch (DisabledException e) {
            System.out.println("Invalid User");
            throw new InvalidPasswordException("Invalid User");
        } catch (BadCredentialsException e) {
            // update user invalid password count
            // int invalidPasswordCount = userService.updateUserInfoOnInvalidLoginAttempts(username, false) to show count 1 instead 0 add maxInvalidPasswordCount by 1

            System.out.println("Invalid user credentials");
            // for user perspective, for now , we have added one to not to show 0 attempts
            // throw new InvalidPasswordException(messageHelper.getMessage("invalid.password.attempts", new Object[]{maxInvalidPasswordCount - invalidPasswordCount}));
            throw new InvalidPasswordException("Invalid user credentials");

        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * verify user account
     *
     * @param token - unique token which is mapped with registered user
     * @return - returns account status
     */
    @PostMapping(value = "/user/activate")
    public ResponseEntity<ApiResponse> userVerification(@RequestParam("token") String token) {

//        boolean isUserVerified = adminService.activeNewUserByToken(token);
//        if (isUserVerified) {
//            return new ResponseEntity<>(new ApiResponse("User verification successfully",UserOtpVerification.UserVerificationStatus.STATUS_VERIFIED,"",HttpStatus.OK), HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(new ApiResponse("User verification is not successfully",UserOtpVerification.UserVerificationStatus.STATUS_PENDING,"",HttpStatus.UNAUTHORIZED), HttpStatus.UNAUTHORIZED);
//        }
        return null;
    }
}
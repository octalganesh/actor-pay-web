package com.octal.actorPay.filters;

import com.octal.actorPay.dto.MerchantResponse;
import com.octal.actorPay.jwt.CustomMerchantDetailsService;
import com.octal.actorPay.jwt.JwtTokenProvider;
import com.octal.actorPay.service.MerchantService;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class MerchantAuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    private CustomMerchantDetailsService  customMerchantDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("ServletRequest userNameHeader: {}" + request.getHeader("username"));
        System.out.println("request URL: {}" + request.getRequestURI());
        String username = request.getHeader("username");
        if (StringUtils.isNotEmpty(username)) {
            UserDetails userDetails = customMerchantDetailsService.loadUserByUsername(username);
            System.out.println("userDetails.getAuthorities() : {}" + userDetails.getAuthorities());
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
//             Global injection role permission information and login user basic information
            SecurityContextHolder.getContext().setAuthentication(authentication);
//            authentication.setDetails();
        }

        filterChain.doFilter(request, response);
    }

    // TODO if need need to use following code
    /*
     HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.isNotBlank(bearerToken)) {
            String token = bearerToken.substring("Bearer".length(),bearerToken.length());
            System.out.println("Token " + token);
            Claims claims = jwtTokenProvider.getClaims(token);
            String subject = claims.getSubject();
            String id = (String) claims.get("id");
            System.out.println("Subject ##### " + subject);
            System.out.println("Id ###### " + id);
            MerchantResponse merchantResponse = merchantService.findMerchantBasicData(id);
            System.out.println("MerchantId " + merchantResponse.getMerchantId());
            System.out.println("User Id " + merchantResponse.getUserId());
            request.setAttribute("merchantId",merchantResponse.getMerchantId());
            request.setAttribute("userId",merchantResponse.getUserId());
        }
     */
}

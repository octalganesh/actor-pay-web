package com.octal.actorPay.configuration.jwt;

import com.netflix.zuul.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * @author Naveen
 * JwtTokenFilter is used to validate JWT token and forword the request to next spring filter
 */
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public JwtTokenFilter(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // extract token from header
        String token = jwtTokenUtil.resolveToken(request);

        // validated token
        if (!Objects.isNull(token) && jwtTokenUtil.validateToken(token)) {
            Authentication auth = null;
            RequestContext ctx = RequestContext.getCurrentContext();
            String requestURL = String.valueOf(request.getRequestURL());
            if (requestURL.contains("admin-service")) {
                auth = jwtTokenUtil.getAuthentication(token, "ADMIN");
                ctx.addZuulRequestHeader("isAdmin", auth.getName());
            } else if (requestURL.contains("merchant-service")) {
                auth = jwtTokenUtil.getAuthentication(token, "MERCHANT");
            } else {
                auth = jwtTokenUtil.getAuthentication(token, "");
            }
            if (!Objects.isNull(auth))
                SecurityContextHolder.getContext().setAuthentication(auth);
            // TODO will replace with userId or email id

            // add current user's username and add into zuul header
            ctx.addZuulRequestHeader("userName", auth.getName());
        }
        filterChain.doFilter(request, response);
    }
}
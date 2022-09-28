package com.octal.actorPay.configuration.jwt;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * @author Naveen
 * This class is used to integrate spring security and it validate each request
 */
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtTokenFilter jwtRequestFilter;

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * Configure spring security for every request and ignore spring security for all the given urls
     *
     * @param httpSecurity - httpSecurity
     * @throws Exception - this method throws exception if any error occurred
     */
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        // CSRF =  Cross-Site Request Forgery
        // CORS= Cross-Origin Resource Sharing
        httpSecurity
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests().antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/api/user-service/users/signup",
                        "/api/user-service/users/social/signup",
                        "/api/user-service/users/login",
                        "/api/user-service/users/resend/activation/link/request",
                        "/api/user-service/auth/token/refresh",
                        "/api/user-service/users/forget/password",
                        "/api/user-service/users/reset/password",
                        "/api/user-service/oauth2/authorize/normal/twitter",
                        "/api/user-service/oauth2/callback/twitter",
                        "/api/merchant-service/merchant/forget/password",
                        "/api/merchant-service/merchant/reset/password",
                        "/api/admin-service/forget/password",
                        "/api/admin-service/reset/password",
                        "/api/admin-service/auth/login",
                        "/api/admin-service/auth/token/refresh",
                        "/api/merchant-service/merchant/signup",
                        "/api/cms-service/get/static/data/by/cms",
                        "/api/cms-service/faq/all",
                        "/api/merchant-service/auth/login",
                        "/api/merchant-service/user/activate",
                        "/api/user-service/user/activate",
                        "/api/global-service/v1/country/get/all",
                        "/api/global-service/v1/country/get/by/{id}",
                        "/api/global-service/v1/global/setting/getConfig",
                        "/api/merchant-service/role/get/all/screens",
                        "/api/user-service/v1/webhook/qr/credit",
                        "/api/user-service/users/social/check/{token}"

                )
                .permitAll()
                .anyRequest().authenticated()
                .and()
                .apply(new JwtConfigurer(jwtTokenUtil));
    }
}
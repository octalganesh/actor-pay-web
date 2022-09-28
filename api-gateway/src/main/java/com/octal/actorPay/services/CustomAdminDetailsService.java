package com.octal.actorPay.services;

import com.octal.actorPay.client.services.AdminService;
import com.octal.actorPay.client.services.UserService;
import com.octal.actorPay.dto.AuthUserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * CustomUserDetailsService implements UserDetailsService and overrides its method
 * which is used to retrieve the user's authentication and authorization information
 */
@Component
public class CustomAdminDetailsService implements UserDetailsService {

    @Autowired
    private AdminService adminService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // call user-service api and get user details
        AuthUserDTO userResponseDTO = adminService.getUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email id:" + email + " not found"));

        List<GrantedAuthority> grantedAuthorities = userResponseDTO.getRoles()
                .stream().map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new User(userResponseDTO.getEmail(),
                        userResponseDTO.getPassword(), true, true, true,
                true, grantedAuthorities);

        /* return new User(String.join("-", username, userResponseDTO.getEmail()), userResponseDTO.getPassword(), true, true, true, true, grantedAuthorities);*/
    }
}
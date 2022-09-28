package com.octal.actorPay.jwt;

import com.octal.actorPay.entities.Role;
import com.octal.actorPay.service.UserService;
import com.octal.actorPay.validator.RuleValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CustomUserDetailsService implements UserDetailsService and overrides its method
 * which is used to retrieve the user's authentication and authorization information
 */
@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Autowired
    private RuleValidator ruleValidator;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // call user-service api and get user details
        com.octal.actorPay.entities.User user = ruleValidator.checkPresence(userService.getUserByEmailId(username),"User is not found for given username: "+username);
        Role role = user.getRole();
//        List<GrantedAuthority> grantedAuthorities = role.getName());
//                .collect(Collectors.toList());
        // dummy Role population
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(role.getName().name()));
        return new User(user.getEmail(),
                user.getPassword(), true, true, true,
                true, grantedAuthorities);
        /* return new User(String.join("-", username, userResponseDTO.getEmail()),
                userResponseDTO.getPassword(), true, true, true,
                true, grantedAuthorities);*/
    }
}
package com.octal.actorPay.jwt;

import com.octal.actorPay.entities.Permission;
import com.octal.actorPay.entities.Role;
import com.octal.actorPay.entities.RoleAPIMapping;
import com.octal.actorPay.repositories.PermissionRepository;
import com.octal.actorPay.repositories.RoleAPIMappingRepository;
import com.octal.actorPay.service.AdminService;
import com.octal.actorPay.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CustomUserDetailsService implements UserDetailsService and overrides its method
 * which is used to retrieve the user's authentication and authorization information
 */
@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AdminService adminService;

    @Autowired
    private RoleService roleApiMappingService;

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // call user-service api and get user details
        com.octal.actorPay.entities.User user = adminService.getUserByEmailId(username);
        Role role = user.getRole();
        if(role == null) {
            throw new RuntimeException("User Doesn't have any Role to Access the API");
        }

        org.springframework.security.core.userdetails.User springUser =
                new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
                        user.getActive(), true, true, true, getAuthorities(role.getId()));

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
                user.getActive(), true, true, true, getAuthorities(role.getId()));
        /* return new User(String.join("-", username, userResponseDTO.getEmail()),
                userResponseDTO.getPassword(), true, true, true,
                true, grantedAuthorities);*/
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String roleId) {
        List<RoleAPIMapping> roleApiMappings = roleApiMappingService.getRoleApiAssocByMapping(roleId);
        return getGrantedAuthorities(roleApiMappings);
    }

    private List<GrantedAuthority> getGrantedAuthorities(List<RoleAPIMapping> roleApiMappings) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        List<String> permissionList = roleApiMappings.stream()
                .map(RoleAPIMapping::getPermissionId)
                .collect(Collectors.toList());
        List<Permission> permissions = permissionRepository.findByIdIn(permissionList);
        for (Permission permission: permissions) {
            authorities.add(new SimpleGrantedAuthority(permission.getName()));
        }
        return authorities;
    }
}
package com.octal.actorPay.jwt;

import com.octal.actorPay.entities.Permission;
import com.octal.actorPay.entities.Role;
import com.octal.actorPay.entities.RoleApiMapping;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.repositories.PermissionRepository;
import com.octal.actorPay.service.MerchantService;
import com.octal.actorPay.service.RoleApiMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CustomMerchantDetailsService implements UserDetailsService and overrides its method
 * which is used to retrieve the user's authentication and authorization information
 */
@Component
public class CustomMerchantDetailsService implements UserDetailsService {

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private RoleApiMappingService roleApiMappingService;

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = merchantService.getUserByEmailId(username);
        Role role = user.getRole();
        if(role == null) {
            throw new RuntimeException("User Doesn't have any Role to Access the API");
        }
        org.springframework.security.core.userdetails.User springUser =
                new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
                        user.isActive(), true, true, true, getAuthorities(role.getId()));

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
                user.isActive(), true, true, true, getAuthorities(role.getId()));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String roleId) {
        List<RoleApiMapping> roleApiMappings = roleApiMappingService.getRoleApiAssocByMapping(roleId);
        return getGrantedAuthorities(roleApiMappings);
    }

    private List<GrantedAuthority> getGrantedAuthorities(List<RoleApiMapping> roleApiMappings) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        List<String> permissionList = roleApiMappings.stream()
                .map(RoleApiMapping::getPermissionId)
                .collect(Collectors.toList());
        List<Permission> permissions = permissionRepository.findByIdIn(permissionList);
        for (Permission permission: permissions) {
            authorities.add(new SimpleGrantedAuthority(permission.getName()));
        }
        return authorities;
    }
}
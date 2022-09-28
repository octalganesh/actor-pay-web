package com.octal.actorPay.service.impl;

import com.octal.actorPay.dto.RoleApiMappingDTO;
import com.octal.actorPay.entities.Permission;
import com.octal.actorPay.entities.RoleApiMapping;
import com.octal.actorPay.repositories.PermissionRepository;
import com.octal.actorPay.repositories.RoleApiMappingRepository;
import com.octal.actorPay.repositories.UserRepository;
import com.octal.actorPay.service.RoleApiMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RoleApiMappingServiceImpl implements RoleApiMappingService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleApiMappingRepository roleApiMappingRepository;

    @Autowired
    private PermissionRepository permissionRepository;


    @Override
    public void createRoleApiMapping(RoleApiMappingDTO roleApiMappingDTO, String actor) {
//        Optional<User> user = ruleValidator.checkPresence(userRepository.findByEmail(actor), "user not found");
//        if (user.isPresent()) {
//            if(!isRoleMerchant(user)) {
//                throw new ActorPayException("Only Primary-Merchant can give permission !! ");
//            } else {
//                RoleApiMapping roleApiMapping = new RoleApiMapping();
//                List<RoleApiMappingDTO> roleApiMappingDTOS = new ArrayList<>();
//                roleApiMappingDTOS.add(roleApiMappingDTO);
//                for (RoleApiMappingDTO permission : roleApiMappingDTOS ){
//                    roleApiMapping.setRoleId(permission.getRoleId());
//                    roleApiMapping.setPermissionId(String.valueOf(permission.getPermissionId()));
//                    roleApiMapping.setActive(Boolean.FALSE);
//                    roleApiMapping.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
//                    roleApiMapping.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
//                    roleApiMapping.setDeleted(Boolean.FALSE);
//                }
//                System.out.println("->  "+ roleApiMappingDTO.getPermissionId() +"   ------   "+roleApiMappingDTO.getRoleId());
//                roleApiMappingRepository.saveAndFlush(roleApiMapping);
//            }
//        }
    }

    @Override
    public void updateRoleApiMapping(RoleApiMappingDTO roleApiMappingDTO, String actor) {


    }

    @Override
    public List<Permission> getAllPermission(String currentUser) {
        List<Permission> allPermission = permissionRepository.findAll();
        System.out.println("Data list  >>>   "+allPermission);
        return allPermission;
    }

//    private boolean isRoleMerchant(Optional<User> user) {
//        return user.get().getRoles().stream().anyMatch(a -> a.getName().equals(Role.RoleName.MERCHANT.name()));
//    }


    @Override
    public ArrayList<RoleApiMapping> getRoleApiAssocByMapping(String roleId) {
        return roleApiMappingRepository.findByRoleId(roleId);
    }
}

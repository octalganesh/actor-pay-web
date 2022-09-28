package com.octal.actorPay.service.impl;

import com.google.common.base.Strings;
import com.octal.actorPay.config.LoadDefaultPermission;
import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.constants.ResourceType;
import com.octal.actorPay.dto.DefaultPermissions;
import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.RoleDTO;
import com.octal.actorPay.dto.RolePermissionUpdate;
import com.octal.actorPay.dto.UserRoleResponse;
import com.octal.actorPay.dto.request.RoleFilterRequest;
import com.octal.actorPay.entities.Permission;
import com.octal.actorPay.entities.Role;
import com.octal.actorPay.entities.RoleAPIMapping;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.exceptions.ActorPayException;
import com.octal.actorPay.exceptions.InvalidRoleDataException;
import com.octal.actorPay.exceptions.InvalidRoleIdentifierException;
import com.octal.actorPay.exceptions.ObjectNotFoundException;
import com.octal.actorPay.exceptions.RoleNotFoundException;
import com.octal.actorPay.helper.MessageHelper;
import com.octal.actorPay.repositories.PermissionRepository;
import com.octal.actorPay.repositories.RoleAPIMappingRepository;
import com.octal.actorPay.repositories.RoleRepository;
import com.octal.actorPay.repositories.ScreensRepository;
import com.octal.actorPay.repositories.UserRepository;
import com.octal.actorPay.service.RoleService;
import com.octal.actorPay.specification.GenericSpecificationsBuilder;
import com.octal.actorPay.specification.SpecificationFactory;
import com.octal.actorPay.transformer.PagedItemsTransformer;
import com.octal.actorPay.transformer.RoleTransformer;
import com.octal.actorPay.validator.RuleValidator;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.octal.actorPay.transformer.RoleTransformer.ROLE_TO_DTO;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MessageHelper messageHelper;


    @Autowired
    private SpecificationFactory<Role> roleSpecificationFactory;

    @Autowired
    private RuleValidator ruleValidator;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private ScreensRepository screensRepository;

    @Autowired
    private RoleAPIMappingRepository roleAPIMappingRepository;

    @Autowired
    private LoadDefaultPermission loadDefaultPermission;

    @Override
    public PageItem<RoleDTO> getRoleList(PagedItemInfo pagedInfo, String currentUser) {
        final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(Role.class, pagedInfo);
        Page<Role> pagedResult = roleRepository.findAll(pageRequest);
        List<RoleDTO> content = pagedResult.getContent().stream().map(ROLE_TO_DTO)
                .collect(Collectors.toList());;

        for (RoleDTO roleDTO : content) {
            List<RoleAPIMapping> roleApiMappings = roleAPIMappingRepository.findByRoleId(roleDTO.getId());
            List<String> permissionIds = roleApiMappings.stream().map(RoleAPIMapping::getPermissionId).collect(Collectors.toList());
            List<Permission> permissions = permissionRepository.findByIdInAndIsActiveTrue(permissionIds);
            List<String> activePermissionIds = permissions.stream().map(Permission::getId).collect(Collectors.toList());

            Optional.ofNullable(roleApiMappings).ifPresent(mappings -> {
                mappings.forEach(roleApiMapping -> {
                    roleApiMapping.setPermissionName(permissionRepository.findById(roleApiMapping.getPermissionId()).get().getName());
                });
            });

            roleApiMappings = roleApiMappings.stream().filter(v -> activePermissionIds.contains(v.getPermissionId())).collect(Collectors.toList());
            roleDTO.setRoleApiMappings(roleApiMappings);
        }

        return new PageItem<>(pagedResult.getTotalPages(),
                pagedResult.getTotalElements(),
                content, pagedInfo.page,
                pagedInfo.items);
    }

    @Override
    public PageItem<RoleDTO> getRoleList(PagedItemInfo pagedInfo, String currentUser, RoleFilterRequest roleFilterRequest) {

        Optional<User> actor = userRepository.findByEmail(currentUser);

        if(actor.isPresent() && ruleValidator.isAdmin(actor.get().getId())) {
            final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(Role.class, pagedInfo);
            GenericSpecificationsBuilder<Role> builder = new GenericSpecificationsBuilder<>();
            prepareRoleSearchFilter(roleFilterRequest, builder);
            Page<Role> pagedResult = roleRepository.findAll(builder.build(), pageRequest);
            List<RoleDTO> content = pagedResult.getContent().stream().map(ROLE_TO_DTO)
                    .collect(Collectors.toList());
            for (RoleDTO roleDTO : content) {
                List<RoleAPIMapping> roleApiMappings = roleAPIMappingRepository.findByRoleId(roleDTO.getId());
                List<String> permissionIds = roleApiMappings.stream().map(RoleAPIMapping::getPermissionId).collect(Collectors.toList());
                List<Permission> permissions = permissionRepository.findByIdInAndIsActiveTrue(permissionIds);
                List<String> activePermissionIds = permissions.stream().map(Permission::getId).collect(Collectors.toList());

//                Optional.ofNullable(roleApiMappings).ifPresent(mappings -> {
//                    mappings.forEach(roleApiMapping -> {
//                        roleApiMapping.setPermissionName(permissionRepository.findById(roleApiMapping.getPermissionId()).get().getName());
//                    });
//                });
//
//                roleApiMappings = roleApiMappings.stream().filter(v->activePermissionIds.contains(v.getPermissionId())).collect(Collectors.toList());
//                roleDTO.setRoleApiMappings(roleApiMappings);
            }

            return new PageItem<>(pagedResult.getTotalPages(), pagedResult.getTotalElements(), content, pagedInfo.page,
                    pagedInfo.items);
        } else {
            throw new ActorPayException(messageHelper.getMessage("admin.can.access.this.resource"));
        }
    }

    @Override
    public RoleDTO getRoleById(String id, String currentUser) {
        if (id == null)
            throw new InvalidRoleIdentifierException("Id cannot be null");

        Optional<User> actor = userRepository.findByEmail(currentUser);
        if (actor.isPresent() && ruleValidator.isAdmin(actor.get().getId())) {
            Optional<Role> roleObj = roleRepository.findById(id);
            if (roleObj.isPresent()) {
                RoleDTO roleDTO = ROLE_TO_DTO.apply(roleObj.get());
                List<RoleAPIMapping> roleApiMappings = roleAPIMappingRepository.findByRoleId(roleDTO.getId());
                Optional.ofNullable(roleApiMappings).ifPresent(mappings -> {
                    mappings.forEach(roleApiMapping -> {
                        roleApiMapping.setPermissionName(permissionRepository.findById(roleApiMapping.getPermissionId()).get().getName());
                    });
                });
                roleDTO.setRoleApiMappings(roleApiMappings);
                return roleDTO;
            } else {
                throw new RoleNotFoundException("Role not found for given ID : " + id);
            }
        } else {
            throw new ActorPayException(messageHelper.getMessage("admin.can.access.this.resource"));
        }
    }

    /**
     * Role creation has following mapping...
     * Role has one to many mapping with module
     * Each module can have one permission so having onee to one mapping
     * <p>
     * Nested object structure:
     * Role has all the Modules that have permissions on each.
     *
     * @param roleDto
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void createRole(RoleDTO roleDto, String currentUser) {
        if (roleDto.getName().equalsIgnoreCase(Role.RoleName.ADMIN.name())) {
            throw new ActorPayException("Primary Merchant Role Name can't be used to create");
        }
        Optional<User> byEmail = userRepository.findByEmail(currentUser);
        String logInUserId = byEmail.get().getId();

        Role role = new Role();
        role.setUserId(logInUserId);
        role.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
        role.setName(roleDto.getName().toUpperCase());
        role.setDescription(roleDto.getDescription());
        role.setDeleted(Boolean.FALSE);
        role.setActive(Boolean.TRUE);
        role.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        Role newRole = roleRepository.saveAndFlush(role);


        List<String> permissionIds = roleDto.getPermissions();

        List<Permission> permissions = permissionRepository.findByIdInAndIsActiveTrue(permissionIds);
        if (permissions != null && permissions.size() == 0) {
            throw new ActorPayException("Permission can't be empty");
        }
        long count = roleAPIMappingRepository.countByPermissionIdInAndRoleId(permissionIds, newRole.getId());
        if (count > 0) {
            throw new ActorPayException("Some Permission are already assigned - Remove those Permission(s) and Try again");
        }

        List<RoleAPIMapping> newRoleApiMappings = addPermission(permissions, newRole);
       // UserRoleResponse userRoleResponse = new UserRoleResponse();
      //  userRoleResponse.setRoleDTO(ROLE_TO_DTO.apply(newRole));
        Optional.ofNullable(newRoleApiMappings).ifPresent(mappings -> {
            mappings.forEach(roleApiMapping -> {
                roleApiMapping.setPermissionName(permissionRepository.findById(roleApiMapping.getPermissionId()).get().getName());
            });
        });
   //     userRoleResponse.getRoleDTO().setRoleApiMappings(newRoleApiMappings);
    //    return userRoleResponse;

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateRole(RolePermissionUpdate roleUpdateRequest, String userName) {
        Role roleObject = roleRepository.findByIdAndIsActiveTrue(roleUpdateRequest.getId()).orElseThrow(() ->
                new ActorPayException("Role is not found"));
        if (roleObject.getName().equalsIgnoreCase(Role.RoleName.ADMIN.name())) {
            throw new ActorPayException("ADMIN Role can't be Updated");
        }
        roleObject.setName(roleUpdateRequest.getName());
        roleObject.setDescription(roleUpdateRequest.getDescription());
        roleObject.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
        roleObject.setDescription(roleUpdateRequest.getDescription());
        roleObject.setActive(roleUpdateRequest.getActive());
        roleObject.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
        roleObject.setActive(Boolean.TRUE);
        roleRepository.save(roleObject);
        List<String> addPermissionIds = roleUpdateRequest.getAddPermissionIds();
        Optional.ofNullable(addPermissionIds).ifPresent(ids -> {
            ids.forEach(id -> {
                permissionRepository.findById(id).orElseThrow(() -> new
                        ActorPayException(String.format("Permission id is not found to add %s", id)));
            });
        });
        List<String> removePermissionIds = roleUpdateRequest.getRemovePermissionIds();
        Optional.ofNullable(addPermissionIds).ifPresent(ids -> {
            ids.forEach(id -> {
                permissionRepository.findById(id).orElseThrow(() -> new
                        ActorPayException(String.format("Permission id is not found to Remove[ %s", id)));
            });
        });
        Optional.ofNullable(addPermissionIds).ifPresent(ids -> {
            List<Permission> permissions = permissionRepository.findByIdIn(addPermissionIds);
            addPermission(permissions, roleObject);
        });
        // Remove Permission
        roleAPIMappingRepository.deleteByRoleIdAndPermissionId(roleObject.getId(), removePermissionIds);
        List<RoleAPIMapping> roleApiMappings = roleAPIMappingRepository.findByRoleId(roleObject.getId());
       // UserRoleResponse userRoleResponse = new UserRoleResponse();
    //    userRoleResponse.setRoleDTO(ROLE_TO_DTO.apply(roleObject));
        Optional.ofNullable(roleApiMappings).ifPresent(mappings -> {
            mappings.forEach(roleApiMapping -> {
                roleApiMapping.setPermissionName(permissionRepository.findById(roleApiMapping.getPermissionId()).get().getName());
            });
        });
  //      userRoleResponse.getRoleDTO().setRoleApiMappings(roleApiMappings);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteRoles(@RequestBody Map<String, List<String>> roleIds) {
        // check if the role is in use here
        List<Role> roles = roleRepository.findAllById(roleIds.get("roleIds"));
        //TODO change delete logic
        roles.forEach(r -> {
            int userCount=0;
        	userCount=roleRepository.findUsersIdByRoleId(r.getId());
        	if(userCount==0) {
            roleRepository.deleteById(r.getId());
        	}
        	else
        		throw new InvalidRoleDataException("User/Users are associated with this role so role can not be deleted");	
        });
    }

    @Override
    public void changeRoleStatus(String id, boolean status) {
        Role role = roleRepository.findRoleById(id);
        if (role!=null) {
            if(status)
                roleRepository.updateStatusById(id,1);
            else
                roleRepository.updateStatusById(id,0);
        } else {
            throw new ObjectNotFoundException("Role not found for the given id: " + id);
        }
    }



    @Override
    public List<RoleDTO> getAllActiveRoles() {

        return roleRepository.findAllByIsActiveIsTrue(Sort.by(Sort.Direction.ASC, "name"))
                .stream()
                .map(RoleTransformer.ROLE_TO_DTO)
                .collect(Collectors.toList());
    }

    // Utility method
    public static void validateRoleName(String roleName) {
        if (Strings.isNullOrEmpty(roleName)) {
            throw new InvalidRoleDataException("Invalid role name: " + roleName);
        }
    }

    @Override
    public List<Permission> getAllPermission(String currentUser) {
        List<Permission> allPermission = permissionRepository.findByIsActive(true);
        return allPermission;
    }

    @Override
    public UserRoleResponse addRoleToUser(String roleId, String userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ActorPayException(String.format("User is is not found %s", userId)));
        if (user.getAdmin()) {
            throw new ActorPayException("Admin is having sufficient permission - can't add any Permission ");
        }
        Role role = roleRepository.findByIdAndIsActiveTrue(roleId).orElseThrow(() ->
                new ActorPayException(String.format("Role is not found %s", roleId)));


        user.setRole(role);
        User updatedUser = userRepository.save(user);
        // Adding Default Role
        DefaultPermissions defaultPermissions = loadDefaultPermission.getDefaultPermissions();
        List<RoleAPIMapping> defaultRoleMappings = defaultPermissions.getSubAdminDefaultPermissions()
                .get(CommonConstant.DEFAULT_SUB_ADMIN_PERMISSION);
        List<RoleAPIMapping> roleApiMappings = roleAPIMappingRepository.findByRoleId(updatedUser.getRole().getId());
//        roleApiMappings.addAll(defaultRoleMappings);
        UserRoleResponse userRoleResponse = new UserRoleResponse();
        userRoleResponse.setRoleDTO(ROLE_TO_DTO.apply(role));
        Optional.ofNullable(roleApiMappings).ifPresent(mappings -> {
            mappings.forEach(roleApiMapping -> {
                roleApiMapping.setPermissionName(permissionRepository.findById(roleApiMapping.getPermissionId()).get().getName());
            });
        });
        userRoleResponse.getRoleDTO().setUserId(userId);
        return userRoleResponse;
    }

    @Override
    public UserRoleResponse removeRoleFromUser(String roleId, String userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ActorPayException(String.format("User is is not found %s", userId)));
        Role role = roleRepository.findByIdAndIsActiveTrue(roleId).orElseThrow(() ->
                new ActorPayException(String.format("Role is not found %s", roleId)));
        if (role.getName().equals(Role.RoleName.ADMIN.name())) {
            throw new ActorPayException("ADMIN Role can't be Deleted");
        }
        Role defaultRole = roleRepository.findByNameAndIsActiveTrue(Role.RoleName.SUB_ADMIN.name()).get();
        user.setRole(defaultRole);
        User updatedUser = userRepository.save(user);
        List<RoleAPIMapping> roleApiMappings = roleAPIMappingRepository.findByRoleId(updatedUser.getRole().getId());
        UserRoleResponse userRoleResponse = new UserRoleResponse();
        userRoleResponse.setRoleDTO(ROLE_TO_DTO.apply(role));
        Optional.ofNullable(roleApiMappings).ifPresent(mappings -> {
            mappings.forEach(roleApiMapping -> {
                roleApiMapping.setPermissionName(permissionRepository.findById(roleApiMapping.getPermissionId()).get().getName());
            });
        });
        userRoleResponse.getRoleDTO().setRoleApiMappings(roleApiMappings);
        userRoleResponse.getRoleDTO().setUserId(userId);
        return userRoleResponse;
    }

    public List<RoleAPIMapping> addDefaultRole() {
        DefaultPermissions defaultPermissions = loadDefaultPermission.getDefaultPermissions();
        List<RoleAPIMapping> roleApiMappings = defaultPermissions.getSubAdminDefaultPermissions()
                .get(CommonConstant.DEFAULT_SUB_ADMIN_PERMISSION);
        return roleApiMappings;
    }

    private void prepareRoleSearchFilter(RoleFilterRequest filterRequest, GenericSpecificationsBuilder<Role> builder) {

        builder.with(roleSpecificationFactory.isEqual("deleted", false));
        if (org.apache.commons.lang.StringUtils.isNotBlank(filterRequest.getUserId())) {
            builder.with(roleSpecificationFactory.isEqual("userId", filterRequest.getUserId()));
        }

        if (org.apache.commons.lang.StringUtils.isNotBlank(filterRequest.getName())) {
            builder.with(roleSpecificationFactory.like("name", filterRequest.getName()));
        }

        if (StringUtils.isNotBlank(filterRequest.getDescription())) {
            builder.with(roleSpecificationFactory.like("description", filterRequest.getDescription()));
        }

        if (filterRequest.getStatus() != null) {
            builder.with(roleSpecificationFactory.isEqual("isActive", filterRequest.getStatus()));
        }
    }

    private List<RoleAPIMapping> addPermission(List<Permission> permissions, Role role) {

        if (role.getName().equalsIgnoreCase(Role.RoleName.ADMIN.name())) {
            throw new ActorPayException("Don't have permission to add/remove permission for Primary Merchant Role");
        }
        List<RoleAPIMapping> roleApiMappings = new ArrayList<>();
        for (Permission permission : permissions) {
            RoleAPIMapping isExist = roleAPIMappingRepository.findByRoleIdAndPermissionId(role.getId(), permission.getId()).orElse(null);
            if (roleAPIMappingRepository.findByRoleIdAndPermissionId(role.getId(), permission.getId()).isPresent()) {
                throw new ActorPayException(String.format("The given Role is already assigned with the Permission %s", permission.getName()));
            }
            RoleAPIMapping roleApiMapping = new RoleAPIMapping();
            roleApiMapping.setRoleId(role.getId());
            roleApiMapping.setPermissionId(permission.getId());
            roleApiMapping.setActive(Boolean.TRUE);
            roleApiMapping.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
            roleApiMappings.add(roleApiMapping);
        }
        List<RoleAPIMapping> newRoleApiMappings = roleAPIMappingRepository.saveAll(roleApiMappings);
        return newRoleApiMappings;
    }

    @Override
    public ArrayList<RoleAPIMapping> getRoleApiAssocByMapping(String roleId) {
        return roleAPIMappingRepository.findByRoleId(roleId);
    }
}
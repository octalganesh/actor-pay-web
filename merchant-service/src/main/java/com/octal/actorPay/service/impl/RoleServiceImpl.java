package com.octal.actorPay.service.impl;

import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.constants.ResourceType;
import com.octal.actorPay.dto.*;
import com.octal.actorPay.dto.request.RoleFilterRequest;
import com.octal.actorPay.entities.Permission;
import com.octal.actorPay.entities.Role;
import com.octal.actorPay.entities.RoleApiMapping;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.exceptions.ActorPayException;
import com.octal.actorPay.exceptions.InvalidRoleIdentifierException;
import com.octal.actorPay.exceptions.RoleNotFoundException;
import com.octal.actorPay.repositories.PermissionRepository;
import com.octal.actorPay.repositories.RoleApiMappingRepository;
import com.octal.actorPay.repositories.RoleRepository;
import com.octal.actorPay.repositories.UserRepository;
import com.octal.actorPay.service.RoleService;
import com.octal.actorPay.specification.GenericSpecificationsBuilder;
import com.octal.actorPay.specification.SpecificationFactory;
import com.octal.actorPay.transformer.MerchantTransformer;
import com.octal.actorPay.transformer.PagedItemsTransformer;
import com.octal.actorPay.utils.LoadDefaultPermission;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
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
    private SpecificationFactory<Role> roleSpecificationFactory;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RoleApiMappingRepository roleApiMappingRepository;

    @Autowired
    private LoadDefaultPermission loadDefaultPermission;

    @Override
    public PageItem<RoleDTO> getAllRolesPaged(PagedItemInfo pagedInfo, RoleFilterRequest roleFilterRequest, String currentUser) {
        GenericSpecificationsBuilder<Role> builder = new GenericSpecificationsBuilder<>();
        User user = userRepository.findByEmail(currentUser).orElseThrow(() -> new ActorPayException("User not found - Invalid user"));
        boolean isMerchant = isRoleMerchant(user);
        if (!isMerchant) {
            throw new ActorPayException("Only merchant can perform this operation");
        }
        prepareRoleSearchQuery(roleFilterRequest, user.getId(), builder);
        final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(Role.class, pagedInfo);
        Page<Role> pagedResult = roleRepository.findAll(builder.build(), pageRequest);
        List<RoleDTO> content = pagedResult.getContent().stream().map(ROLE_TO_DTO)
                .collect(Collectors.toList());
        for (RoleDTO roleDTO : content) {
            List<RoleApiMapping> roleApiMappings = roleApiMappingRepository.findByRoleId(roleDTO.getId());
            List<String> permissionIds = roleApiMappings.stream().map(RoleApiMapping::getPermissionId).collect(Collectors.toList());
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
        return new PageItem<>(pagedResult.getTotalPages(), pagedResult.getTotalElements(), content, pagedInfo.page,
                pagedInfo.items);
    }

    @Override
    public List<KeyValuePair> getRoleKeyValuePair(String currentUser) {
        GenericSpecificationsBuilder<Role> builder = new GenericSpecificationsBuilder<>();
        User user = userRepository.findByEmail(currentUser).orElseThrow(() -> new ActorPayException("User not found - Invalid user"));
        boolean isMerchant = isRoleMerchant(user);
        if (!isMerchant) {
            throw new ActorPayException("Only merchant can perform this operation");
        }

        //      final PageRequest pageRequest = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("recordId").descending());
        //      List<Role> roleList = roleRepository.findAll(builder.build());

        List<Role> roleList = roleRepository.findByUserId(user.getId());

//        List<RoleDTO> list = pagedResult.getContent().stream().map(ROLE_TO_DTO)
//                .collect(Collectors.toList());
        List<KeyValuePair> responseList = new ArrayList<>();

        for (Role role : roleList) {

            responseList.add(new KeyValuePair(role.getId(), role.getName()));

        }
        return responseList;

    }

    @Override
    public RoleDTO getRoleById(String id, String actor) {
        if (StringUtils.isBlank(id))
            throw new InvalidRoleIdentifierException("Id cannot be null");
        User user = userRepository.findByEmail(actor).orElseThrow(() -> new ActorPayException("User not found - Invalid user"));
        boolean isMerchant = isRoleMerchant(user);
        if (!isMerchant) {
            throw new ActorPayException("Only merchant can perform this operation");
        }
        Optional<Role> roleObj = roleRepository.findById(id);
        if (roleObj.isPresent()) {
            RoleDTO roleDTO = ROLE_TO_DTO.apply(roleObj.get());
            List<RoleApiMapping> roleApiMappings = roleApiMappingRepository.findByRoleId(roleDTO.getId());
            Optional.ofNullable(roleApiMappings).ifPresent(mappings -> {
                mappings.forEach(roleApiMapping -> {
                    roleApiMapping.setPermissionName(permissionRepository.findById(roleApiMapping.getPermissionId()).get().getName());
                });
            });
            roleDTO.setRoleApiMappings(roleApiMappings);
            return roleDTO;
        } else
            throw new RoleNotFoundException("Role not found for given ID : " + id);
    }

    @Override
    public UserRoleResponse getRoleByUserid(String id, String actor) {
        if (StringUtils.isBlank(id))
            throw new InvalidRoleIdentifierException("Id cannot be null");
        User merchantUser = userRepository.findByEmail(actor).orElseThrow(() -> new ActorPayException("User not found - Invalid user"));
        boolean isMerchant = isRoleMerchant(merchantUser);
        /*if (!isMerchant) {
            throw new ActorPayException("Only merchant can perform this operation");
        }*/
        User user = userRepository.findUserByEmailOrContactNumber(id).orElseThrow(() -> new ActorPayException("User not found"));
        UserDTO userDTO = MerchantTransformer.USER_TO_USER_DTO.apply(user);
        Role role = roleRepository.findById(user.getRole().getId()).orElseThrow(() -> new ActorPayException("Role not found for the user"));
        RoleDTO roleDTO = ROLE_TO_DTO.apply(role);
        List<RoleApiMapping> roleApiMappings = roleApiMappingRepository.findByRoleId(roleDTO.getId());
        Optional.ofNullable(roleApiMappings).ifPresent(mappings -> {
            mappings.forEach(roleApiMapping -> {
                roleApiMapping.setPermissionName(permissionRepository.findById(roleApiMapping.getPermissionId()).get().getName());
            });
        });
        roleDTO.setRoleApiMappings(roleApiMappings);
        UserRoleResponse userRoleResponse = new UserRoleResponse();
        userRoleResponse.setUserDTO(userDTO);
        userRoleResponse.setRoleDTO(roleDTO);
        return userRoleResponse;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public UserRoleResponse createRole(RoleCreateRequest roleCreateRequest, String currentUser) {
        // TODO admin validation need to add
        if (roleCreateRequest.getName().equalsIgnoreCase(Role.RoleName.PRIMARY_MERCHANT.name())) {
            throw new ActorPayException("Primary Merchant Role Name can't be used to create");
        }
        Optional<User> byEmail = userRepository.findByEmail(currentUser);
        String logInUserId = byEmail.get().getId();

        System.out.println("== Role Type ->  " + roleCreateRequest.getName());
        //Optional<Role> byName = roleRepository.findByName(roleCreateRequest.getName());

       /* if (byName.get().getName().equals(roleCreateRequest.getName())) {
            throw new ActorPayException("Role name already available, please try with another Role Name");
        } else {*/

        Role role = new Role();
        role.setUserId(logInUserId);
        role.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
        role.setName(roleCreateRequest.getName().toUpperCase());
        role.setDescription(roleCreateRequest.getDescription());
        role.setDeleted(Boolean.FALSE);
        role.setActive(Boolean.TRUE);
        role.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        Role newRole = roleRepository.saveAndFlush(role);


        List<String> permissionIds = roleCreateRequest.getPermissions();
//        Map<String, List<RoleApiMapping>> defaultPermissions = loadDefaultPermission.getDefaultPermissions().getSubMerchantDefaultPermissions();
//        List<RoleApiMapping> defaultRoleApiMappings = defaultPermissions.get(CommonConstant.DEFAULT_SUB_MERCHANT_PERMISSION);
//       List<String> defaultPermissionIds = defaultRoleApiMappings.stream().map(permissionId ->
//               permissionId.getPermissionId()).collect(Collectors.toList());
//       permissionIds.addAll(defaultPermissionIds);

//        String userId = roleCreateRequest.getUserId();
//        User user = userRepository.findById(userId).orElseThrow(() -> new ActorPayException("User not found"));
//        if (user.getMerchantDetails().getResourceType().equals(ResourceType.PRIMARY_MERCHANT)) {
//            throw new ActorPayException("User type is Primary Merchant. " +
//                    "He is having sufficient Access - can't assign further permission");
//        }
        List<Permission> permissions = permissionRepository.findByIdInAndIsActiveTrue(permissionIds);
        if (permissions != null && permissions.size() == 0) {
            throw new ActorPayException("Permission can't be empty");
        }
        long count = roleApiMappingRepository.countByPermissionIdInAndRoleId(permissionIds, newRole.getId());
        if (count > 0) {
            throw new ActorPayException("Some Permission are already assigned - Remove those Permission(s) and Try again");
        }
//        roleApiMappingRepository.deleteByRoleIdAndPermissionId(user.getRole().getId(),defaultPermissionIds);
//
//        user.setRole(newRole);
//        userRepository.save(user);
        List<RoleApiMapping> newRoleApiMappings = addPermission(permissions, newRole);
        UserRoleResponse userRoleResponse = new UserRoleResponse();
        userRoleResponse.setRoleDTO(ROLE_TO_DTO.apply(newRole));
        Optional.ofNullable(newRoleApiMappings).ifPresent(mappings -> {
            mappings.forEach(roleApiMapping -> {
                roleApiMapping.setPermissionName(permissionRepository.findById(roleApiMapping.getPermissionId()).get().getName());
            });
        });
        userRoleResponse.getRoleDTO().setRoleApiMappings(newRoleApiMappings);
        return userRoleResponse;
        // }

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public RoleApiMappingResponse assignRoleToUser(RoleCreateRequest roleCreateRequest) {

        return new RoleApiMappingResponse();
    }

    private List<RoleApiMapping> addPermission(List<Permission> permissions, Role role) {

        if (role.getName().equalsIgnoreCase(Role.RoleName.PRIMARY_MERCHANT.name())) {
            throw new ActorPayException("Don't have permission to add/remove permission for Primary Merchant Role");
        }
        List<RoleApiMapping> roleApiMappings = new ArrayList<>();
        for (Permission permission : permissions) {
            RoleApiMapping isExist = roleApiMappingRepository.findByRoleIdAndPermissionId(role.getId(), permission.getId()).orElse(null);
            if (roleApiMappingRepository.findByRoleIdAndPermissionId(role.getId(), permission.getId()).isPresent()) {
                throw new ActorPayException(String.format("The given Role is already assigned with the Permission %s", permission.getName()));
            }
            RoleApiMapping roleApiMapping = new RoleApiMapping();
            roleApiMapping.setRoleId(role.getId());
            roleApiMapping.setPermissionId(permission.getId());
            roleApiMapping.setActive(Boolean.TRUE);
            roleApiMapping.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
            roleApiMappings.add(roleApiMapping);
        }
        List<RoleApiMapping> newRoleApiMappings = roleApiMappingRepository.saveAll(roleApiMappings);
        return newRoleApiMappings;
    }

    private User beforeCreateAndUpdateRole(String actor) {
        User user = userRepository.findByEmail(actor).orElseThrow(() -> new ActorPayException("User not found"));
        if (user != null) {
            if (!isRoleMerchant(user)) {
                throw new ActorPayException("Only merchant can create roles");
            }
        }
        return user;
    }

    private boolean isRoleMerchant(User user) {
        return user.getRole().getName().equals(Role.RoleName.PRIMARY_MERCHANT.name());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateRole(RolePermissionUpdateRequest roleUpdateRequest, String userName) {
        Role roleObject = roleRepository.findByIdAndIsActiveTrue(roleUpdateRequest.getId()).orElseThrow(() ->
                new ActorPayException("Role is not found"));
        if (roleObject.getName().equalsIgnoreCase(Role.RoleName.PRIMARY_MERCHANT.name())) {
            throw new ActorPayException("Primary Merchant Role can't be Updated");
        }
        if (roleObject != null) {
            roleObject.setName(roleUpdateRequest.getName());
            roleObject.setDescription(roleUpdateRequest.getDescription());
            roleObject.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
            roleObject.setDescription(roleUpdateRequest.getDescription());
            roleObject.setActive(roleUpdateRequest.getActive());
            roleObject.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
            roleObject.setActive(Boolean.TRUE);
            roleRepository.save(roleObject);
        }
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
        roleApiMappingRepository.deleteByRoleIdAndPermissionId(roleObject.getId(), removePermissionIds);
        List<RoleApiMapping> roleApiMappings = roleApiMappingRepository.findByRoleId(roleObject.getId());
        UserRoleResponse userRoleResponse = new UserRoleResponse();
        userRoleResponse.setRoleDTO(ROLE_TO_DTO.apply(roleObject));
        Optional.ofNullable(roleApiMappings).ifPresent(mappings -> {
            mappings.forEach(roleApiMapping -> {
                roleApiMapping.setPermissionName(permissionRepository.findById(roleApiMapping.getPermissionId()).get().getName());
            });
        });
        userRoleResponse.getRoleDTO().setRoleApiMappings(roleApiMappings);
    }

    @Override
    public void deleteRoles(String roleId) {

        Long count = roleRepository.findMinimumAssociatedRole(roleId);
        if (count != null && count > 0) {
            throw new ActorPayException("Role is already Associated with User");
        }
        roleRepository.deleteRole(roleId);
    }

    @Override
    public void changeRoleStatus(String id, boolean status) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new ActorPayException("Role not found"));
        if (role.getName().equalsIgnoreCase(Role.RoleName.PRIMARY_MERCHANT.name())) {
            throw new ActorPayException("Primary Merchant Role status can't be changed");
        }
        if (status) {
            roleRepository.updateStatusById(id, 1);
        } else {
            roleRepository.updateStatusById(id, 0);
        }

    }

    @Override
    public List<RoleDTO> getAllRoles(String currentUser) {
        User user = userRepository.findByEmail(currentUser).orElseThrow(() -> new ActorPayException("User not found"));
        boolean isMerchant = isRoleMerchant(user);
        if (!isMerchant) {
            throw new ActorPayException("Only merchant can perform this operation");
        }
      /*  //List<Role> allByUserIdAndOrderByNameAsc = roleRepository.findAllByUserIdOrderByName(user.getId());
        List<Role> allByUserIdAndOrderByNameAsc = new ArrayList<>();
        return allByUserIdAndOrderByNameAsc.stream().map(ROLE_TO_DTO)
                .collect(Collectors.toList());*/
        List<Role> allRole = roleRepository.findAll();
        System.out.println("all role -----   " + allRole);
        return allRole.stream().map(ROLE_TO_DTO)
                .collect(Collectors.toList());
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
        if (user.getMerchantDetails().getResourceType().equals(ResourceType.PRIMARY_MERCHANT)) {
            throw new ActorPayException("Primary Merchant is having sufficient permission - can't add any Permission ");
        }
        Role role = roleRepository.findByIdAndIsActiveTrue(roleId).orElseThrow(() ->
                new ActorPayException(String.format("Role is not found %s", roleId)));


        user.setRole(role);
        User updatedUser = userRepository.save(user);
        // Adding Default Role
        DefaultPermissions defaultPermissions = loadDefaultPermission.getDefaultPermissions();
        List<RoleApiMapping> defaultRoleMappings = defaultPermissions.getSubMerchantDefaultPermissions()
                .get(CommonConstant.DEFAULT_SUB_MERCHANT_PERMISSION);
        List<RoleApiMapping> roleApiMappings = roleApiMappingRepository.findByRoleId(updatedUser.getRole().getId());
//        roleApiMappings.addAll(defaultRoleMappings);
        UserRoleResponse userRoleResponse = new UserRoleResponse();
        userRoleResponse.setRoleDTO(ROLE_TO_DTO.apply(role));
        Optional.ofNullable(roleApiMappings).ifPresent(mappings -> {
            mappings.forEach(roleApiMapping -> {
                roleApiMapping.setPermissionName(permissionRepository.findById(roleApiMapping.getPermissionId()).get().getName());
            });
        });
        return userRoleResponse;
    }

    @Override
    public UserRoleResponse removeRoleFromUser(String roleId, String userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ActorPayException(String.format("User is is not found %s", userId)));
        Role role = roleRepository.findByIdAndIsActiveTrue(roleId).orElseThrow(() ->
                new ActorPayException(String.format("Role is not found %s", roleId)));
        if (role.getName().equals(Role.RoleName.PRIMARY_MERCHANT.name())) {
            throw new ActorPayException("Primary Merchant Role can't be Deleted");
        }
        Role defaultRole = roleRepository.findByNameAndIsActiveTrue(ResourceType.SUB_MERCHANT.name()).get();
        user.setRole(defaultRole);
        User updatedUser = userRepository.save(user);
        List<RoleApiMapping> roleApiMappings = roleApiMappingRepository.findByRoleId(updatedUser.getRole().getId());
        UserRoleResponse userRoleResponse = new UserRoleResponse();
        userRoleResponse.setRoleDTO(ROLE_TO_DTO.apply(role));
        Optional.ofNullable(roleApiMappings).ifPresent(mappings -> {
            mappings.forEach(roleApiMapping -> {
                roleApiMapping.setPermissionName(permissionRepository.findById(roleApiMapping.getPermissionId()).get().getName());
            });
        });
        userRoleResponse.getRoleDTO().setRoleApiMappings(roleApiMappings);
        return userRoleResponse;
    }

    public List<RoleApiMapping> addDefaultRole() {
        DefaultPermissions defaultPermissions = loadDefaultPermission.getDefaultPermissions();
        List<RoleApiMapping> roleApiMappings = defaultPermissions.getSubMerchantDefaultPermissions()
                .get(CommonConstant.DEFAULT_SUB_MERCHANT_PERMISSION);
        return roleApiMappings;
    }

    private void prepareRoleSearchQuery(RoleFilterRequest filterRequest, String merchantId, GenericSpecificationsBuilder<Role> builder) {

        builder.with(roleSpecificationFactory.isEqual("deleted", false));


        if (StringUtils.isNotBlank(filterRequest.getUserId())) {
            builder.with(roleSpecificationFactory.isEqual("userId", filterRequest.getUserId()));
        }

        if (StringUtils.isNotBlank(filterRequest.getName())) {
            builder.with(roleSpecificationFactory.like("name", filterRequest.getName()));
        }

        if (StringUtils.isNotBlank(filterRequest.getDescription())) {
            builder.with(roleSpecificationFactory.like("description", filterRequest.getDescription()));
        }
        if (filterRequest.getStatus() != null) {
            builder.with(roleSpecificationFactory.isEqual("isActive", filterRequest.getStatus()));
        }
    }

}
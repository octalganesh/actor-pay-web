package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.RoleApiMapping;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface RoleApiMappingRepository extends JpaRepository<RoleApiMapping,String> {

    ArrayList<RoleApiMapping> findByRoleId(String roleId);
    long countByPermissionIdInAndRoleId(List<String> permissionIds,String roleId);

    ArrayList<RoleApiMapping> findByRoleIdAndIsActiveTrue(String roleId);

    @Modifying
    @Query("delete RoleApiMapping r where roleId = :roleId and r.permissionId in (:permissionIds)")
    void deleteByRoleIdAndPermissionId(@Param("roleId") String roleId,@Param("permissionIds") List<String> permissionIds);

    Optional<RoleApiMapping> findByRoleIdAndPermissionId(String roleId, String permissionId);


}

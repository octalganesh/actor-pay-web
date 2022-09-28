package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.RoleAPIMapping;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface RoleAPIMappingRepository extends JpaRepository<RoleAPIMapping, String> {

    ArrayList<RoleAPIMapping> findByRoleId(String roleId);
    long countByPermissionIdInAndRoleId(List<String> permissionIds, String roleId);

    ArrayList<RoleAPIMapping> findByRoleIdAndIsActiveTrue(String roleId);

    @Modifying
    @Query("delete RoleAPIMapping r where roleId = :roleId and r.permissionId in (:permissionIds)")
    void deleteByRoleIdAndPermissionId(@Param("roleId") String roleId, @Param("permissionIds") List<String> permissionIds);

    Optional<RoleAPIMapping> findByRoleIdAndPermissionId(String roleId, String permissionId);
}

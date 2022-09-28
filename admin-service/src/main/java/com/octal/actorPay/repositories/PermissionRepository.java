package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {

    List<Permission> findByIdIn(List<String> permissionIds);
    List<Permission> findByIdInAndIsActiveTrue(List<String> permissionIds);

    List<Permission> findByIsActive(Boolean isActive);

}
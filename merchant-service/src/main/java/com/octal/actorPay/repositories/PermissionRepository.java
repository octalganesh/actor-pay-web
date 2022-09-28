package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission,String> {

//    @Query(name = "select permission from Permission permission where permission.id in :permissionId")
    List<Permission> findByIdIn(List<String> permissionIds);
    List<Permission> findByIdInAndIsActiveTrue(List<String> permissionIds);

    List<Permission> findByIsActive(Boolean isActive);

   /* //SELECT DISTINCT p.name FROM `merchant_db`.`role_api_mapping` r JOIN  `merchant_db`.`permission` p ON r.permission_id  = p.id WHERE r.role_id = "33a15655-2921-43fd-ac4d-cc1976f0d890";
    @Query(value = "SELECT DISTINCT p.id, p.name FROM permission p JOIN  role_api_mapping r ON r.permission_id  = p.id WHERE r.role_id = :role_id", nativeQuery = true)
    List<String>  findSubMerchantPermission(@Param("role_id") String role_id);
    */
}

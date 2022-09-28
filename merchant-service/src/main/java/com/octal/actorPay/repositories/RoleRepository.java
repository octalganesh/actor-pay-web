package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String>, JpaSpecificationExecutor<Role> {

    List<Role> findByUserId(String userId);

    Optional<Role> findByNameAndIsActiveTrue(String roleName);
    @Query(value = "update role r set is_active= :status where r.id = :roleId ", nativeQuery = true)
    Role updateStatusById(@Param("roleId") String roleId, @Param("status") int status);

    Optional<Role> findByIdAndIsActiveTrue(String roleId);

    @Query(nativeQuery = true, value = "select count(role_id) from users where role_id=:roleId limit 0,2")
    Long findMinimumAssociatedRole(@Param("roleId") String roleId);
/*
    @Query(nativeQuery = true, value = "select count(role_id) from users_role where role_id=:roleId")
    Long getCountOfAssociatedRoleWithUser(@Param("roleId") String roleId);

    @Query(nativeQuery = true, value = "select count(role_id) from role_to_screen_mapping where role_id=:roleId")
    Long getCountOfAssociatedRoleWithScreen(@Param("roleId") String roleId);*/

    @Query(value = "delete from role where id =:roleId", nativeQuery = true)
    void deleteRole(@Param("roleId") String roleId);

    @Query(value = "SELECT id FROM role WHERE name= :type", nativeQuery = true)
    String findRoleIdByUserType(@Param("type") String type);

    @Query(value = "SELECT * FROM role r where r.name LIKE '%SUB_MERCHANT%'",nativeQuery = true)
    Optional<Role> findTypeName();

    @Query(value = "SELECT * FROM merchant_db.role WHERE NAME NOT IN ('PRIMARY_MERCHANT', 'SUB_MERCHANT') AND id= :roleId", nativeQuery = true)
    Optional<Role> findResourceType(String roleId);


    Optional<Role> findByName(@Param("name") String name);
}
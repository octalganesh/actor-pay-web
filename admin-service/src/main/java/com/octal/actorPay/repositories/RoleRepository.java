package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.Role;


import com.octal.actorPay.entities.RoleToScreenMapping;
import com.octal.actorPay.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
@Repository
public interface RoleRepository extends JpaRepository<Role, String>, JpaSpecificationExecutor<Role> {

//    @Query(value = "select count(*) from users_roles where role_id = ?1", nativeQuery = true)
//    Long countRoleUsage(String roleId);

    List<Role> findAllByIsActiveIsTrue(Sort sort);


    @Query(value = "select * from role r where r.id = :roleId ", nativeQuery = true)
    Role findRoleById(@Param("roleId") String roleId);

    @Query(value = "update role r set is_active= :status where r.id = :roleId ", nativeQuery = true)
    Role updateStatusById(@Param("roleId") String roleId,@Param("status") int status);


    @Query(value = "select count(*) from users_role ur where ur.role_id = :roleId ", nativeQuery = true)
    int findUsersIdByRoleId(@Param("roleId")  String roleId);

    Optional<Role> findByIdAndIsActiveTrue(String roleId);

    Optional<Role> findByNameAndIsActiveTrue(String roleName);

}
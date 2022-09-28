package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.Outlet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OutletRepository extends JpaRepository<Outlet, String>, JpaSpecificationExecutor<Outlet> {

//    Page<Outlet> findOutletByUserId(String userId, Pageable pageRequest, Specification specification);

    Optional<Outlet> findByIdAndDeletedFalse(String id);

    //    @Query(name = "select count(o.id) from Outlet o where o.id=:outletId")
//    Long findCountById(@Param("outletId") String outletId);
    long countById(String id);

}
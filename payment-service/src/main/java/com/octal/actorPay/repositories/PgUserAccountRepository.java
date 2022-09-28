package com.octal.actorPay.repositories;

import com.octal.actorPay.model.entities.PgUserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PgUserAccountRepository extends JpaRepository<PgUserAccount,String> {

//    List<PgUserAccount> findByUserIdAndUserType(String userId);
    List<PgUserAccount> findByUserId(String userId);
    PgUserAccount findByUserIdAndPgFundId(String userId,String fundId);
    PgUserAccount findByAccountNumberAndIfscCodeAndUserId(String accountNumber,String ifscCode, String userId);
    PgUserAccount findByUserIdAndIsPrimaryAccountTrue(String userId);
    List<PgUserAccount> findByUserIdAndIsSelfTrue(String userId);
    List<PgUserAccount> findByUserIdAndIsSelfFalse(String userId);

}

package com.octal.actorPay.repositories;

import com.octal.actorPay.model.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, String>, JpaSpecificationExecutor<Wallet> {

    /*  @Query(value = "update wallet w set w.amount= :amount where w.userId = :userId ", nativeQuery = true)
      Wallet findWalletByUserId(@Param("userId") String userId, @Param("amount") double amount);
  */
    @Query(value = "select w from Wallet w  where w.userId = :userId ")
    Wallet findWalletByUserId(@Param("userId") String userId);

    @Query(value = "select w from Wallet w  where w.userId = :userId and w.userType=:userType")
    Wallet findWalletByUserIdAndUserType(@Param("userId") String userId,@Param("userType") String userType);

    Optional<Wallet> findByUserIdAndDeletedFalse(String userId);
//
    @Query(value = "SELECT SUM(b.transaction_amount) from wallet_transaction b where b.created_at BETWEEN :from AND :to AND user_type='customer' AND transaction_types='CREDIT'",
            nativeQuery = true)
    List<Double> findAmountByDay(@Param("from") String from, @Param("to") String to);

}
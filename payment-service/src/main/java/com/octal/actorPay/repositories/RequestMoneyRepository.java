package com.octal.actorPay.repositories;

import com.octal.actorPay.constants.RequestMoneyStatus;
import com.octal.actorPay.model.entities.RequestMoney;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface RequestMoneyRepository extends JpaRepository<RequestMoney, String>, JpaSpecificationExecutor<RequestMoney> {

    List<RequestMoney> findRequestMoneyByToUserId(String toUserId);
    List<RequestMoney> findRequestMoneyByUserId(String userId);
    List<RequestMoney> findRequestMoneyByToUserIdAndRequestMoneyStatus(String toUserId,RequestMoneyStatus requestMoneyStatus);
    List<RequestMoney> findRequestMoneyByUserIdAndRequestMoneyStatus(String userId,RequestMoneyStatus requestMoneyStatus);

    RequestMoney findRequestMoneyByUserIdAndToUserId(String userId,String toUserId);

    Optional<RequestMoney> findByIdAndRequestMoneyStatusAndToUserId(String id, RequestMoneyStatus requestMoneyStatus,String toUserId);

    RequestMoney findByRequestCode(String requestCode);
}

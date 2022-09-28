package com.octal.actorPay.spec;

import com.octal.actorPay.model.entities.RequestMoney;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;

public class RequestMoneySpec {

    private static Specification<RequestMoney> getByUserId(String userId) {
        return (root, query, criteriaBuilder) -> {
            Predicate requestMoneySpec = criteriaBuilder.equal(root.get("userId"), userId);
            return requestMoneySpec;
        };
    }

    private static Specification<RequestMoney> getByToUserId(String toUserId) {
        return (root, query, criteriaBuilder) -> {
            Predicate requestMoneySpec = criteriaBuilder.equal(root.get("toUserId"), toUserId);
            return requestMoneySpec;
        };
    }
    public static Specification<RequestMoney> amountBetween(Double fromAmount,Double toAmount) {
        return (root, query, criteriaBuilder) -> {
            Predicate requestMoneySpec = criteriaBuilder.between(root.get("amount"), fromAmount,toAmount);
            return requestMoneySpec;
        };
    }
    public static Specification<RequestMoney> dateBetween(String attribute,LocalDateTime startDate, LocalDateTime endDate) {
        return (root, query, criteriaBuilder) -> {
            Predicate requestMoneySpec = criteriaBuilder.between(root.get(attribute), startDate,endDate);
            return requestMoneySpec;
        };
    }
    public static Specification<RequestMoney> getUserIdOrToUserId(String userId, String toUserId) {
        return Specification.where(getByUserId(userId))
                .or(getByToUserId(toUserId));
    }
    public static Specification<RequestMoney> combineSpec(String attribute, Object value) {
        return  (root, query, criteriaBuilder) -> {
            Predicate requestMoneySpec = criteriaBuilder.equal(root.get(attribute), value);
            return requestMoneySpec;
        };

    }


}

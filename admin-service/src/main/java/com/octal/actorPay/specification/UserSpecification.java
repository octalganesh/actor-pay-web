package com.octal.actorPay.specification;

import com.octal.actorPay.entities.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserSpecification {

        public static Specification<User> getFirstName(String firstname) {
            return (root, criteriaQuery, criteriaBuilder) -> {
                return criteriaBuilder.like(root.get("firstName"),"%" +firstname+ "%");
            };
        }
        public static Specification<User> getLastName(String lastname){
            return ((root, query, criteriaBuilder) -> {
                return criteriaBuilder.like(root.get("lastName"), "%" + lastname + "%");
            });
        }
        public static Specification<User> getEmail(String email){
            return ((root, query, criteriaBuilder) -> {
                return criteriaBuilder.equal(root.get("email"), email);
            });
        }
        public static Specification<User> getContactNumber(String contactNumber){
            return ((root, query, criteriaBuilder) -> {
                return criteriaBuilder.equal(root.get("contactNumber"), contactNumber);
            });
        }
        public static Specification<User> getIsActive(boolean isActive){
            return ((root, query, criteriaBuilder) -> {
                return criteriaBuilder.equal(root.get("isActive"), isActive);
            });
        }
        public static Specification<User> getCreatedAt(LocalDateTime createdAt) {
            return ((root, query, criteriaBuilder) -> {
                return criteriaBuilder.equal(root.get("createdAt"), createdAt);
            });
        }

/*
    public  Specification<User> getUsers(UserDTO useReq) {
            return (root, query, criteriaBuilder) -> {

        List<Predicate> predicates = new ArrayList<>();

        if (useReq.getEmail() != null && !useReq.getEmail().isEmpty()) {
            predicates.add((Predicate) criteriaBuilder.like(criteriaBuilder.lower(root.get("email")),
                    "%" + useReq.getEmail().toLowerCase() + "%"));
        }
        if (useReq.getFirstName() != null && !useReq.getFirstName().isEmpty()) {
            predicates.add((Predicate) criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")),
                    "%" + useReq.getFirstName().toLowerCase() + "%"));
        }
        if (useReq.getLastName() != null && !useReq.getLastName().isEmpty()) {
            predicates.add((Predicate) criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")),
                    "%" + useReq.getLastName().toLowerCase() + "%"));
        }
        if (useReq.getContactNumber() != null && !useReq.getContactNumber().isEmpty()) {
             predicates.add((Predicate) criteriaBuilder.equal(root.get("contactNumber"), useReq.getContactNumber()));
                }
        if (useReq.isActive() != false) {
            predicates.add((Predicate) criteriaBuilder.equal(criteriaBuilder.lower(root.get("isActive")), useReq.isActive()));
        }
        return criteriaBuilder.and(predicates.toArray(new javax.persistence.criteria.Predicate[0]));
      };
    }*/

}


package com.octal.actorPay.validator;

import com.octal.actorPay.entities.User;
import org.springframework.stereotype.Service;

import java.util.Optional;


/**
 * @author naveen.kumawat
 * RuleValidator - contains methods which are used to perform some precondications before executing actual logic
 */
@Service
public interface RuleValidator {

    /**
     * check object is present or not, if object is not present then throw exception
     *
     * @param <T> - type
     * @param reference - object
     * @return - return the object
     */
    <T> T checkPresence(T reference);

    /**
     * check object is present or not, if object is not present then throw exception with
     * custom provided message
     *
     * @param <T>
     * @param reference
     * @param message   - error message
     * @return - return object if present
     */
    <T> Optional<T> checkPresence(Optional<T> reference, String message);

    <T> T checkPresence(T reference, String message);

    /**
     * this method checks that information is being modify by owner or admin
     * @param user - that will be update in database
     * @param actor - current logged in user
     * @return - true if user is account ower or admin 
     */
//    boolean canUpdateUser(User user, User actor);

//    boolean isAdmin(String userId);




}
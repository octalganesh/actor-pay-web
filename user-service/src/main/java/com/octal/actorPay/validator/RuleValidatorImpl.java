package com.octal.actorPay.validator;

import com.google.common.base.Predicate;
import com.octal.actorPay.exceptions.ObjectNotFoundException;
import com.octal.actorPay.entities.Role;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.repositories.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

@Service
public class RuleValidatorImpl implements RuleValidator {

    public static final Logger LOGGER = LogManager.getLogger(RuleValidatorImpl.class);

    @Inject
    private UserRepository userRepository;



    @Override
    public <T> T checkPresence(T reference) {
        if (reference == null) {
            LOGGER.error("object not found ");
            throw new ObjectNotFoundException();
        }
        return reference;
    }

    @Override
    public <T> Optional<T> checkPresence(Optional<T> reference, String message) {
        if (!reference.isPresent()) {
            LOGGER.error("object not found ");
            throw new ObjectNotFoundException(message);
        }
        return reference;
    }

    @Override
    public <T> T checkPresence(T reference, String message) {
        if (reference == null) {
            LOGGER.error("object not found ");
            throw new ObjectNotFoundException(message);
        }
        return reference;
    }

//    @Override
//    public boolean canUpdateUser(User user, User actor) {
//        return isAdmin(actor.getId()) | user.getId().equals(actor.getId());
//    }
//
//
//    private Predicate<User> isAdmin(final User user) {
//
//        return usr -> isAdmin(user.getId());
//    }

    /**
     * this method fetch user information from database and checks that user is admin or not
     * @param actor - current logged in user
     * @return - true if user is admin else false
     */
//    @Override
//    public boolean isAdmin(String actor) {
//        Optional<User> user = checkNotNull(userRepository.findById(actor), "User not found. Id=" + actor);
//        Optional<Role> superAdminRole = user.get().getRoles().stream().filter(r -> r.getName().equals(Role.RoleName.ADMIN)).findFirst();
//        return superAdminRole.filter(role -> user.get().getRoles().contains(role)).isPresent();
//    }


}
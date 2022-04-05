package cs5031.groupc.practical3.utils;

import cs5031.groupc.practical3.database.DataAccessObject;
import cs5031.groupc.practical3.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import cs5031.groupc.practical3.vo.UserRole;

/**
 * Utils class for input validation.
 * */
@Component
public class InputValidationUtils {

    final DataAccessObject dao;

    @Autowired
    public InputValidationUtils(DataAccessObject dao) {
        this.dao = dao;
    }

    public void inSameGroup(User user1, User user2) throws ResponseStatusException {
        if (!user1.getGroup().getGroupId().equals(user2.getGroup().getGroupId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Users must be in same group.");
        }
    }

    public void userInGroup(User user, String groupName) throws ResponseStatusException {
        if (!user.getGroup().getName().equals(groupName)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not in group.");
        }
    }

    public void userHasGroup(User user, boolean shouldHaveGroup) throws ResponseStatusException {
        boolean hasGroup = user.getGroup() != null;
        if (hasGroup != shouldHaveGroup) {
            if (shouldHaveGroup) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User has no group.");
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already has a group.");
            }
        }
    }

    public void userHasGroup(User user) throws ResponseStatusException {
        if (user.getGroup() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User has no group.");
        }
    }

    public void userIsNotAdmin(User user) throws ResponseStatusException {
        if (user.getRole().getRole().equals(UserRole.ADMIN.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This would leave the group without admin.");
        }
    }

    public void valueInRange(double value, double lo, double hi) throws ResponseStatusException {
        if (value < lo || value > hi) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Value out of range.");
        }
    }

    public void userEnabled(User user) throws ResponseStatusException {
        if (!user.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User must be enabled.");
        }
    }

}

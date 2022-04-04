package cs5031.groupc.practical3;

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

    public void userHasNoGroup(User user) throws ResponseStatusException {
        if (user.getGroup() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already in a group.");
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

}

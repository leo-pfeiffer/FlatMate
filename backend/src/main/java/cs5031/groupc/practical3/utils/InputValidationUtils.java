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
 */
@Component
public class InputValidationUtils {

    /**
     * The DataAccessObject; grants access to the database.
     */
    private final DataAccessObject dao;

    /**
     * The constructor.
     *
     * @param dao The DAO.
     */
    @Autowired
    public InputValidationUtils(final DataAccessObject dao) {
        this.dao = dao;
    }

    /**
     * Tests if two user are in the same group.
     *
     * @param user1 The first user.
     * @param user2 The second user.
     * @throws ResponseStatusException Throws exception if not in same group.
     */
    public void inSameGroup(final User user1, final User user2) throws ResponseStatusException {
        if (!user1.getGroup().getGroupId().equals(user2.getGroup().getGroupId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Users must be in same group.");
        }
    }

    /**
     * Tests if user is in group.
     *
     * @param user      The user to be tested.
     * @param groupName The group to be tested.
     * @throws ResponseStatusException Throws exception if not in the group.
     */
    public void userInGroup(final User user, final String groupName) throws ResponseStatusException {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User is null.");
        }

        if (!user.getGroup().getName().equals(groupName)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not in group.");
        }
    }

    /**
     * Tests if user is in a group and if the user should be in a group.
     *
     * @param user            The user to be tested.
     * @param shouldHaveGroup Boolean if the user should be in a group
     * @throws ResponseStatusException Throws exception if necessary.
     */
    public void userHasGroup(final User user, final boolean shouldHaveGroup) throws ResponseStatusException {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User is null.");
        }
        boolean hasGroup = user.getGroup() != null;
        if (hasGroup != shouldHaveGroup) {
            if (shouldHaveGroup) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User has no group.");
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already has a group.");
            }
        }
    }

    /**
     * Tests if user has a group.
     *
     * @param user The user to be tested.
     * @throws ResponseStatusException Throws exception if user not in Group.
     */
    public void userHasGroup(final User user) throws ResponseStatusException {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User is null.");
        }
        if (user.getGroup() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User has no group.");
        }
    }

    /**
     * Tests if user is not admin.
     *
     * @param user The user to be tested.
     * @throws ResponseStatusException Throws exception if user is admin.
     */
    public void userIsNotAdmin(final User user) throws ResponseStatusException {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User is null.");
        }
        if (user.getRole().getRole().equals(UserRole.ADMIN.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This would leave the group without admin.");
        }
    }

    /**
     * Tests if value is in range.
     *
     * @param value The value to be tested.
     * @param lo    The lower bound (inclusive).
     * @param hi    The upper bound (inclusive).
     * @throws ResponseStatusException Throws exception if not in bounds.
     */
    public void valueInRange(final double value, final double lo, final double hi) throws ResponseStatusException {
        if (value < lo || value > hi) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Value out of range.");
        }
    }

    /**
     * Tests if user is enabled.
     *
     * @param user The user to be tested.
     * @throws ResponseStatusException Throws exception if not enabled.
     */
    public void userEnabled(final User user) throws ResponseStatusException {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User is null.");
        }
        if (!user.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User must be enabled.");
        }
    }

}

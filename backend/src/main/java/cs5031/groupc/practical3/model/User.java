package cs5031.groupc.practical3.model;

import cs5031.groupc.practical3.vo.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User implements DataProtection {

    /**
     * The username of the user.
     */
    private String username;

    /**
     * The password of the user.
     */
    private String password;

    /**
     * The group the user belongs to.
     */
    private Group group;

    /**
     * The role the user has within the group.
     */
    private UserRole role;

    /**
     * Stores whether the user is active or not.
     */
    private boolean enabled;


    /**
     * Protects sensible data.
     */
    public void protect() {
        this.password = null;
    }
}

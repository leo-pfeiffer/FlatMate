package cs5031.groupc.practical3.vo;

import lombok.Getter;

@Getter
public enum UserRole {

    /**
     * ENUM for admin.
     */
    ADMIN("ADMIN"),

    /**
     * ENUM for user.
     */
    USER("USER");

    /**
     * The role.
     */
    private final String role;

    UserRole(final String role) {
        this.role = role;
    }
}

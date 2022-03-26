package cs5031.groupc.practical3.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {

    private String username;
    private String password;
    private long groupId;
    private String role;
    private boolean enabled;

}

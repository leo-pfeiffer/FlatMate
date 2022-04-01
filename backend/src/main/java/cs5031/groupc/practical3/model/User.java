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
public class User implements DataProtection{

    private String username;
    private String password;
    private Group group;
    private UserRole role;
    private boolean enabled;



    public void protect(){
        this.password = null;
    }
}

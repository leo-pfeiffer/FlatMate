package cs5031.groupc.practical3.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Group {

    /**
     * The id of th group.
     */
    private Long groupId;

    /**
     * The name of the group.
     */
    private String name;
}

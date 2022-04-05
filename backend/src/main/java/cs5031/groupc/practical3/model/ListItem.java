package cs5031.groupc.practical3.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListItem implements DataProtection {

    /**
     * The id of the listItem.
     */
    private Long listItemId;

    /**
     * The name of the listItem.
     */
    private String name;

    /**
     * the list the listItem belongs to.
     */
    private List list;

    /**
     * Protects sensible data.
     */
    @Override
    public void protect() {
        this.list.protect();
    }
}

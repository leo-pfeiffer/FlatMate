package cs5031.groupc.practical3.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class List implements DataProtection {

    /**
     * The id of the list.
     */
    private Long listId;

    /**
     * The name of the list.
     */
    private String name;

    /**
     * The description of the list.
     */
    private String description;

    /**
     * The owner of the list.
     */
    private User owner;

    /**
     * the bill of the list (optional).
     */
    private Bill bill;

    /**
     * The unix timestamp of the creation of the list.
     */
    private Long createTime;

    /**
     * Protects sensible data.
     */
    public void protect() {
        this.owner.protect();
        if (this.bill != null) {
            this.bill.protect();
        }
    }
}

package cs5031.groupc.practical3.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Bill implements DataProtection {

    /**
     * The id of the bill.
     */
    private Long billId;

    /**
     * The name of the bill.
     */
    private String name;

    /**
     * The dscription of the bill.
     */
    private String description;

    /**
     * The amount due of the bill.
     */
    private double amount;

    /**
     * The payment method of the bill.
     */
    private String paymentMethod;

    /**
     * The owner of the bill.
     */
    private User owner;

    /**
     * The unix timestamp of the creation of the bill.
     */
    private Long createTime;


    /**
     * Protects sensible data.
     */
    public void protect() {
        this.owner.protect();
    }
}

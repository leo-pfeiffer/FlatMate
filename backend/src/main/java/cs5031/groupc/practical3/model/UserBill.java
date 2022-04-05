package cs5031.groupc.practical3.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserBill implements DataProtection {

    /**
     * The id of the UserBill object.
     */
    private Long userBillId;

    /**
     * The User that the UserBill object belongs to.
     */
    private User user;

    /**
     * The Bill the UserBill object belongs to.
     */
    private Bill bill;

    /**
     * The percentage of the bill the user has to pay.
     */
    private double percentage;

    /**
     * Stores whether the user has poid the bill or not.
     */
    private boolean paid;

    /**
     * Protects sensible data.
     */
    public void protect() {
        this.user.protect();
        this.bill.protect();
    }
}

package cs5031.groupc.practical3.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserBill implements DataProtection{

    private Long userBillId;
    private User user;
    private Bill bill;
    private double percentage;
    private boolean paid;

    public void protect(){
        this.user.protect();
        this.bill.protect();
    }
}

package cs5031.groupc.practical3.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserBill {

    private Long userBillId;
    private User user;
    private Bill bill;
    private double percentage;
    private boolean paid;
}

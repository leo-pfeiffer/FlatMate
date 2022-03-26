package cs5031.groupc.practical3.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserBill {

    private Long userBillId;
    private String user;
    private Long bill;
    private double percentage;
    private boolean paid;
}

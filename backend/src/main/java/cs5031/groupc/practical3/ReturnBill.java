package cs5031.groupc.practical3;

import cs5031.groupc.practical3.model.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ReturnBill {

    private Long billId;
    private String name;
    private String description;
    private double amount;
    private String paymentMethod;
    private String owner;

    public ReturnBill(Bill bill){
        this.billId = bill.getBillId();
        this.name = bill.getName();
        this.description = bill.getDescription();
        this.amount = bill.getAmount();
        this.paymentMethod =bill.getPaymentMethod();
        this.owner = bill.getOwner().getUsername();
    }
}

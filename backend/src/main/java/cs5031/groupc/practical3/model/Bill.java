package cs5031.groupc.practical3.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Bill {

    private Long billId;
    private String name;
    private String description;
    private double amount;
    private String paymentMethod;
    private String owner;
}

package cs5031.groupc.practical3;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Todo could we just use the Bill class?

@Getter
@Setter
@NoArgsConstructor
public class BillAdder {

    private int bill_id;
    private String name;
    private String description;
    private double amount;
    private String payment_method;
}
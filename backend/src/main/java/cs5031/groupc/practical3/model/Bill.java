package cs5031.groupc.practical3.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Bill {

    private Long billId;
    private String name;
    private String description;
    private double amount;
    private String paymentMethod;
    private User owner;
    private Long createTime;    // unix timestamp
}

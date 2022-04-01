package cs5031.groupc.practical3.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class List implements DataProtection{

    private Long listId;
    private String name;
    private String description;
    private User owner;
    private Bill bill;
    private Long createTime;    // unix timestamp

    public void protect(){
        this.owner.protect();
        if (this.bill != null){
            this.bill.protect();
        }
    }
}

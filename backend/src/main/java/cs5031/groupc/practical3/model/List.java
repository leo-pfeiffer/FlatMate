package cs5031.groupc.practical3.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class List {

    private Long listId;
    private String name;
    private String description;
    private User owner;
    private Bill bill;
}

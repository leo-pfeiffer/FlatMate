package cs5031.groupc.practical3.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class List {

    private Long listId;
    private String name;
    private String description;
    private String owner;
    private Long bill;
}

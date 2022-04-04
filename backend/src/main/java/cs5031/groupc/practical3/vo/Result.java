package cs5031.groupc.practical3.vo;

import java.util.HashMap;
import lombok.Getter;

@Getter
public enum Result {

    SUCCESS(true),
    NO_SUCCESS(false);

    private final HashMap<String, Boolean> result = new HashMap<>();

    Result(Boolean isSuccess) {
        result.put("success", isSuccess);
    }
}

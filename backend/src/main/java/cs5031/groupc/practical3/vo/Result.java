package cs5031.groupc.practical3.vo;

import java.util.HashMap;

import lombok.Getter;

@Getter
public enum Result {

    /**
     * ENUM for success.
     */
    SUCCESS(true),

    /**
     * ENUM for no success.
     */
    NO_SUCCESS(false);

    /**
     * A Hashmap that stores the result.
     */
    private final HashMap<String, Boolean> result = new HashMap<>();

    Result(final Boolean isSuccess) {
        result.put("success", isSuccess);
    }
}

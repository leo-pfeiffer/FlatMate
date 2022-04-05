package cs5031.groupc.practical3.model;

/**
 * Interface to protect sensible data; password of all users should be nulled.
 */
public interface DataProtection {

    /**
     * Method to null all passwords.
     */
    void protect();
}
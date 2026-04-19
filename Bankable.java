package CSE1325project;

/**
 * Bankable.java
 */
public interface Bankable {

    /** Print account info. */
    void printInfo();

    /** Take money out of the account. */
    boolean withdraw(double amount);

    /** Add money to the account. */
    boolean deposit(double amount);
}

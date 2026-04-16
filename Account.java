import java.util.ArrayList;

// ── Enum (merged from AccountType.java) ──────────────────────────
enum AccountType {
    CHECKING
}

// ── Abstract base class (merged from BankEntity.java) ────────────
abstract class BankEntity {

    private String accountId;
    private String ownerName;

    public BankEntity(String accountId, String ownerName) {
        this.accountId = accountId;
        this.ownerName = ownerName;
    }

    public String getAccountId() { return accountId; }
    public String getOwnerName() { return ownerName; }

    /** Subclasses must print their own info. */
    public abstract void printInfo();
}

// ── Main Account class

/**
 * Account.java
 */
public class Account extends BankEntity implements Bankable {

    private double            balance;
    private AccountType       accountType;
    private String            pin;

    // ArrayList stores each action as a String
    private ArrayList<String> history;

    // Minimum allowed balance
    private static final double MIN_BALANCE = 0.0;

    /**
     * Creates a new account.
     */
    public Account(String accountId, String ownerName,
                   double initialDeposit, AccountType accountType, String pin) {
        super(accountId, ownerName);
        this.balance     = initialDeposit;
        this.accountType = accountType;
        this.pin         = pin;
        this.history     = new ArrayList<>();
        history.add("Account opened. Balance: $" + format(initialDeposit));
    }

    public double getBalance() { return balance; }
    public AccountType getAccountType() { return accountType; }
    public String getPin() { return pin; }

    // Used when loading from file
    protected void setBalance(double b) {
        this.balance = b;
    }

    // Records a transfer sent to another account
    public boolean transferOut(String toId, double amount) {
        if (balance - amount < MIN_BALANCE) {
            System.out.println("  [Error] Not enough money. Balance: $" + format(balance));
            return false;
        }
        balance -= amount;
        history.add("Transfer sent to " + toId + ": -$" + format(amount) + " | Balance: $" + format(balance));
        return true;
    }

    // Records a transfer received from another account
    public void transferIn(String fromId, double amount) {
        balance += amount;
        history.add("Transfer received from " + fromId + ": +$" + format(amount) + " | Balance: $" + format(balance));
    }


    // Resets history after loading from file
    protected void resetHistory() {
        history.clear();
        history.add("Account loaded. Balance: $" + format(balance));
    }

    /**
     * Converts account data to one line for saving to file.
     */
    public String toFileLine() {
        return getAccountId() + "|"
                + getOwnerName() + "|"
                + pin + "|"
                + accountType + "|"
                + format(balance);
    }

    // ── Bankable interface methods 

    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("  [Error] Withdrawal must be more than $0.");
            return false;
        }
        if (balance - amount < MIN_BALANCE) {
            System.out.println("  [Error] Not enough money. Balance: $" + format(balance));
            return false;
        }
        balance -= amount;
        history.add("Withdraw: -$" + format(amount) + " | Balance: $" + format(balance));
        return true;
    }

    public void printHistory() {
        System.out.println("\n  --- History for " + getAccountId() + " ---");
        for (String entry : history) {
            System.out.println("  • " + entry);
        }
    }

    protected String format(double value) {
        return String.format("%.2f", value);
    }

    public boolean deposit(double amount) {
        if (amount <= 0) {
            System.out.println("  [Error] Deposit must be more than $0.");
            return false;
        }
        balance += amount;
        history.add("Deposit: +$" + format(amount) + " | Balance: $" + format(balance));
        return true;
    }

    @Override
    public void printInfo() {
        System.out.println("  ID     : " + getAccountId());
        System.out.println("  Owner  : " + getOwnerName());
        System.out.println("  Type   : " + accountType.toString().toUpperCase());
        System.out.println("  Balance: $" + format(balance));
    }
}

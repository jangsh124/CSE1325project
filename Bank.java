import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Bank.java
 */
public class Bank {

    private String             bankName;
    private ArrayList<Account> accounts;

    private static final String ACCOUNTS_FILE = "accounts.txt";

    /**
     * Creates a Bank and loads any saved accounts from file.
     */
    public Bank(String bankName) {
        this.bankName = bankName;
        this.accounts = new ArrayList<>();
        loadFromFile();
    }

    // File Save / Load

    /**
     * Saves all accounts to accounts.txt.
     * Each line: id|name|type|balance
     */
    private void saveToFile() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(ACCOUNTS_FILE));
            for (Account acc : accounts) {
                writer.write(acc.toFileLine());
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("  [Error] Could not save accounts to file.");
        }
    }

    /**
     * Loads accounts from accounts.txt when the program starts.
     */
    private void loadFromFile() {
        File file = new File(ACCOUNTS_FILE);
        if (!file.exists()) {
            return; // no saved data yet, that's fine
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = reader.readLine()) != null) {
                // Split the line by | — String lecture
                String[] parts = line.split("\\|");
                if (parts.length != 5) continue; // skip bad lines

                String id          = parts[0];
                String name        = parts[1];
                String pinHash     = parts[2]; //updated to store hashed pin instead of normal pin
                double balance     = Double.parseDouble(parts[4]);
                Account acc = new CheckingAccount(id, name, 0, "0000");

                // Set the hashed PIN directly
                acc.setPinHash(pinHash);
                acc.setBalance(balance);
                acc.resetHistory();
                accounts.add(acc);
            }

            reader.close();

            if (!accounts.isEmpty()) {
                System.out.println("  [Info] Loaded " + accounts.size() + " account(s) from file.");
            }

        } catch (Exception e) {
            System.out.println("  [Error] Could not load accounts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    

    /** Finds an account by ID. Returns null if not found. */
    private Account find(String accountId) {
        for (Account acc : accounts) {
            if (acc.getAccountId().equals(accountId)) {
                return acc;
            }
        }
        return null;
    }

    /** Returns true if the given ID is already taken. */
    public boolean idExists(String accountId) {
        return find(accountId) != null;
    }

    /** Checks if the PIN matches. Prints error if not. */
    private boolean checkPin(Account acc, String pin) {
        if (!acc.verifyPin(pin)) {
            System.out.println("\n  [Error] Wrong PIN.");
            return false;
        }
        return true;
    }

    // ── Create / Remove 

    /**
     * Makes a new checking account with a user-chosen ID.
     */
    public void createAccount(String accountId, String ownerName, double initialDeposit, String pin) {
        if (initialDeposit < 100) {
            System.out.println("  [Error] Initial deposit must be at least $100.00.");
            return;
        }
        Account newAccount = new CheckingAccount(accountId, ownerName, initialDeposit, pin);

        accounts.add(newAccount);

        System.out.println("\n  [Done] Account created!");
        System.out.println("  ----------------------------");
        newAccount.printInfo();
        System.out.println("  ----------------------------");

        Logger.save(new Transaction(Transaction.CREATE, accountId, initialDeposit, Logger.now()));
        saveToFile();
    }

    /**
     * Removes an account from the list by ID.
     */
    public void removeAccount(String accountId, String pin) {
        Account target = find(accountId);
        if (target == null) {
            System.out.println("\n  [Error] Account not found: " + accountId);
            return;
        }
        if (!checkPin(target, pin)) return;

        Iterator<Account> it = accounts.iterator();
        while (it.hasNext()) {
            Account acc = it.next();
            if (acc.getAccountId().equals(accountId)) {
                it.remove();
                System.out.println("\n  [Done] Account " + accountId + " removed.");
                Logger.save(new Transaction(Transaction.REMOVE, accountId, 0.0, Logger.now()));
                saveToFile();
                return;
            }
        }
    }

    // ── Money Operations 

    /**
     * Deposits money into an account.
     */
    public void deposit(String accountId, String pin, double amount) {
        Account target = find(accountId);
        if (target == null) {
            System.out.println("\n  [Error] Account not found: " + accountId);
            return;
        }
        if (!checkPin(target, pin)) return;
        boolean ok = target.deposit(amount);
        if (ok) {
            System.out.println("  [Done] Deposited $" + String.format("%.2f", amount));
            System.out.println("  New Balance: $" + String.format("%.2f", target.getBalance()));
            Logger.save(new Transaction(Transaction.DEPOSIT, accountId, amount, Logger.now()));
            saveToFile();
        }
    }

    /**
     * Withdraws money from an account.
     *
     */
    public void withdraw(String accountId, String pin, double amount) {
        Account target = find(accountId);
        if (target == null) {
            System.out.println("\n  [Error] Account not found: " + accountId);
            return;
        }
        if (!checkPin(target, pin)) return;
        boolean ok = target.withdraw(amount);
        if (ok) {
            System.out.println("  [Done] Withdrew $" + String.format("%.2f", amount));
            System.out.println("  Remaining: $" + String.format("%.2f", target.getBalance()));
            Logger.save(new Transaction(Transaction.WITHDRAW, accountId, amount, Logger.now()));
            saveToFile();
        }
    }

    /**
     * Transfers money from one account to another.
     */
    public void transfer(String fromId, String pin, String toId, double amount) {
        Account sender   = find(fromId);
        Account receiver = find(toId);

        if (sender == null) {
            System.out.println("\n  [Error] Sender not found: " + fromId);
            return;
        }
        if (receiver == null) {
            System.out.println("\n  [Error] Receiver not found: " + toId);
            return;
        }
        if (!checkPin(sender, pin)) return;

        boolean ok = sender.transferOut(toId, amount);
        if (ok) {
            receiver.transferIn(fromId, amount);
            System.out.println("\n  [Done] Transferred $" + String.format("%.2f", amount));
            System.out.println("  From: " + fromId + " → To: " + toId);
            Logger.save(new Transaction(
                    Transaction.TRANSFER,
                    fromId + "->" + toId,
                    amount,
                    Logger.now()
            ));
            saveToFile();
        }
    }

    // ── Display 

    /**
     * Shows details for one account.
     */
    public void viewAccount(String accountId) {
        Account target = find(accountId);
        if (target == null) {
            System.out.println("\n  [Error] Account not found: " + accountId);
            return;
        }
        System.out.println("\n  ----------------------------");
        target.printInfo();
        target.printHistory();
        System.out.println("  ----------------------------");
    }

    /**
     * Shows account transaction summary
     */
    public void showAccountSummary(String accountId) {
        Account target = find(accountId);
        if (target == null) {
            System.out.println("\n  [Error] Account not found: " + accountId);
            return;
        }
        
        Logger.showAccountSummary(accountId);
    }

    /**
     * Lists all accounts.
     */
    public void listAll() {
        System.out.println("\n  ===== Accounts in " + bankName + " =====");
        if (accounts.isEmpty()) {
            System.out.println("  No accounts yet.");
            return;
        }
        for (Account acc : accounts) {
            System.out.printf("  [%s] %-15s | %s | $%.2f%n",
                    acc.getAccountId(),
                    acc.getOwnerName(),
                    acc.getAccountType(),
                    acc.getBalance()
            );
        }
        System.out.println("  =====================================");
    }

    /** Shows the log file. */
    public void showLog() {
        Logger.showLog();
    }
}

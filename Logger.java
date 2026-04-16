import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


// Stores info about one bank action.
class Transaction {

    private String actionType;
    private String accountId;
    private Double amount;     // Double wrapper class — Wrapper Classes lecture
    private String timestamp;

    public static final String CREATE   = "CREATE";
    public static final String DEPOSIT  = "DEPOSIT";
    public static final String REMOVE   = "REMOVE";
    public static final String TRANSFER = "TRANSFER";
    public static final String WITHDRAW = "WITHDRAW";

    /**
     * Creates a Transaction object.
     */
    public Transaction(String actionType, String accountId, Double amount, String timestamp) {
        this.actionType = actionType;
        this.accountId  = accountId;
        this.amount     = amount;
        this.timestamp  = timestamp;
    }

    /** Returns a formatted log line ready to write to file. */
    public String toLogLine() {
        return "[" + timestamp + "] "
                + actionType
                + " | Account: " + accountId
                + " | Amount: $" + String.format("%.2f", amount);
    }
}

// ── Logger class ─────────────────────────────────────────────────

/**
 * Logger.java
 * Writes and reads the transaction log file.
 */
public class Logger {

    private static final String LOG_FILE = "bank_log.txt";

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Returns the current date and time as a String.
     *
     * @return current time like "2025-02-22 16:33:00"
     */
    public static String now() {
        return LocalDateTime.now().format(TIME_FORMAT);
    }

    /**
     * Reads and prints all lines from the log file.
     */
    public static void showLog() {
        File logFile = new File(LOG_FILE);

        if (!logFile.exists()) {
            System.out.println("  [Log] No log file yet.");
            return;
        }

        System.out.println("\n  ===== Transaction Log =====");

        try {
            BufferedReader reader = new BufferedReader(new FileReader(logFile));
            String line;
            int count = 0;

            // Read each line until end of file
            while ((line = reader.readLine()) != null) {
                System.out.println("  " + line);
                count++;
            }
            reader.close();

            if (count == 0) {
                System.out.println("  (Log is empty.)");
            }

        } catch (IOException e) {
            System.out.println("  [Log Error] Could not read log.");
            e.printStackTrace();
        }

        System.out.println("  ===========================\n");
    }

    /**
     * Saves one transaction to the log file.
     */
    public static void save(Transaction transaction) {
        try {
            // Create the file if it doesn't exist yet
            File logFile = new File(LOG_FILE);
            if (logFile.createNewFile()) {
                System.out.println("  [Log] Log file created: " + LOG_FILE);
            }
            // true = append mode (don't overwrite old data)
            FileWriter writer = new FileWriter(logFile, true);
            writer.write(transaction.toLogLine() + "\n");
            writer.close();

        } catch (IOException e) {
            System.out.println("  [Log Error] Could not write to log.");
            e.printStackTrace();
        }
    }
}

package CSE1325project;

import java.util.Scanner;

/**
 * Main.java
 * Entry point. Only this class has main().
 */

//Compile Command: javac -d . *.java && java CSE1325project.Main


public class Main {

    /** Prints the main menu. */
    private static void showMenu() {
        System.out.println("\n  --- Menu ---");
        System.out.println("  1. Create account");
        System.out.println("  2. Remove account");
        System.out.println("  3. Deposit");
        System.out.println("  4. Withdraw");
        System.out.println("  5. Transfer");
        System.out.println("  6. View account");
        System.out.println("  7. Show account summary");
        System.out.println("  8. List all accounts");
        System.out.println("  9. View log");
        System.out.println("  0. Exit");
        System.out.println("  ------------");
    }

    /** Asks for an ID and amount, then withdraws. */
    private static void doWithdraw(Bank bank, Scanner scanner) {
        System.out.print("\n  Account ID: ");
        String id = scanner.nextLine().trim().toUpperCase();
        System.out.print("  PIN: ");
        String pin = scanner.nextLine().trim();
        try {
            System.out.print("  Withdraw amount: $");
            double amount = Double.parseDouble(scanner.nextLine().trim());

            // If under $5, warn about 10% fee and ask for confirmation
            if (amount < 5.0) {
                System.out.println("  [Notice] Withdrawals less than $5.00 are charged a 10% fee.");
                System.out.println("  Fee: $" + String.format("%.2f", amount * 0.10));
                System.out.println("  Proceed? yes(1) / no(0)");

                while (true) {
                    System.out.print("  Your choice: ");
                    String input = scanner.nextLine().trim();
                    if (input.equals("1")) {
                        bank.withdraw(id, pin, amount);
                        break;
                    } else if (input.equals("0")) {
                        System.out.println("  [Cancelled] Withdrawal cancelled.");
                        break;
                    } else {
                        System.out.println("  [Error] Please enter 1 (yes) or 0 (no).");
                    }
                }
            } else {
                bank.withdraw(id, pin, amount);
            }

        } catch (NumberFormatException e) {
            System.out.println("  [Error] Please enter a valid amount.");
        }
    }

    /** Asks for an ID and shows that account's info. */
    private static void doViewAccount(Bank bank, Scanner scanner) {
        System.out.print("\n  Account ID: ");
        String id = scanner.nextLine().trim().toUpperCase();
        bank.viewAccount(id);
    }

    /**Asks for an ID and shows account transaction summary */
    private static void doAccountSummary(Bank bank, Scanner scanner) {
        System.out.print("\n Account ID: ");
        String id = scanner.nextLine().trim().toUpperCase();
        bank.showAccountSummary(id);
    }

    /** Asks for an ID and amount, then deposits. */
    private static void doDeposit(Bank bank, Scanner scanner) {
        System.out.print("\n  Account ID: ");
        String id = scanner.nextLine().trim().toUpperCase();
        System.out.print("  PIN: ");
        String pin = scanner.nextLine().trim();
        try {
            System.out.print("  Deposit amount: $");
            double amount = Double.parseDouble(scanner.nextLine().trim());
            bank.deposit(id, pin, amount);
        } catch (NumberFormatException e) {
            System.out.println("  [Error] Please enter a valid amount.");
        }
    }

    /** Asks for an ID and removes that account. */
    private static void doRemoveAccount(Bank bank, Scanner scanner) {
        System.out.print("\n  Account ID to remove: ");
        String id = scanner.nextLine().trim().toUpperCase();
        System.out.print("  PIN: ");
        String pin = scanner.nextLine().trim();

        // Confirm deletion
        while (true) {
            System.out.print("  Are you sure you want to delete this account? yes(1) / no(0): ");
            String input = scanner.nextLine().trim();
            if (input.equals("1")) {
                bank.removeAccount(id, pin);
                break;
            } else if (input.equals("0")) {
                System.out.println("  [Cancelled] Account deletion cancelled.");
                break;
            } else {
                System.out.println("  [Error] Please enter 1 (yes) or 0 (no).");
            }
        }
    }

    /** Prints the welcome banner. */
    private static void showBanner() {
        System.out.println("========================================");
        System.out.println("     Bank Account Management System     ");
        System.out.println("          Welcome to JavaBank           ");
        System.out.println("========================================");
    }

    /** Asks for two IDs and an amount, then transfers. */
    private static void doTransfer(Bank bank, Scanner scanner) {
        System.out.print("\n  From account ID: ");
        String fromId = scanner.nextLine().trim().toUpperCase();
        System.out.print("  PIN: ");
        String pin = scanner.nextLine().trim();
        System.out.print("  To account ID: ");
        String toId = scanner.nextLine().trim().toUpperCase();
        try {
            System.out.print("  Transfer amount: $");
            double amount = Double.parseDouble(scanner.nextLine().trim());
            bank.transfer(fromId, pin, toId, amount);
        } catch (NumberFormatException e) {
            System.out.println("  [Error] Please enter a valid amount.");
        }
    }

    /**
     * Asks for info and creates a new account.
     * User sets their own account ID. Checks for duplicates.
     */
    private static void doCreateAccount(Bank bank, Scanner scanner) {
        System.out.print("\n  Owner name: ");
        String name = scanner.nextLine();

        // Keep asking until a unique ID is confirmed
        String accountId = "";
        while (accountId.isEmpty()) {
            System.out.print("  Set account ID: ");
            String first = scanner.nextLine().trim();
            if (first.isEmpty()) {
                System.out.println("  [Error] ID cannot be empty.");
                continue;
            }
            if (bank.idExists(first.toUpperCase())) {
                System.out.println("  [Error] That ID is already taken. Please choose another.");
                continue;
            }
            // Confirm loop — only re-asks confirm if it doesn't match
            while (true) {
                System.out.print("  Confirm account ID: ");
                String second = scanner.nextLine().trim();
                if (!first.equalsIgnoreCase(second)) {
                    System.out.println("  [Error] IDs do not match. Try again.");
                } else {
                    accountId = first.toUpperCase();
                    break;
                }
            }
        }

        // Ask for 4-digit PIN with confirmation
        String pin = "";
        while (pin.isEmpty()) {
            System.out.print("  Set 4-digit PIN: ");
            String first = scanner.nextLine().trim();
            if (!first.matches("\\d{4}")) {
                System.out.println("  [Error] PIN must be exactly 4 digits.");
                continue;
            }
            System.out.print("  Confirm PIN: ");
            String second = scanner.nextLine().trim();
            if (!first.equals(second)) {
                System.out.println("  [Error] PINs do not match. Try again.");
            } else {
                pin = first;
            }
        }

        double deposit = -1;
        while (deposit < 100) {
            try {
                System.out.print("  Initial deposit (min $100): $");
                deposit = Double.parseDouble(scanner.nextLine().trim());
                if (deposit < 100) {
                    System.out.println("  [Error] Initial deposit must be at least $100.00.");
                }
            } catch (NumberFormatException e) {
                System.out.println("  [Error] Please enter a valid amount.");
            }
        }
        bank.createAccount(accountId, name, deposit, pin);
    }

    public static void main(String[] args) {
        Bank    bank    = new Bank("JavaBank");
        Scanner scanner = new Scanner(System.in);
        int     choice  = -1;

        showBanner();


        do {
            showMenu();

            // try-catch for bad input 
            try {
                System.out.print("  Your choice: ");
                choice = Integer.parseInt(scanner.nextLine().trim()); // Integer wrapper class

            } catch (NumberFormatException e) {
                System.out.println("  [Error] Please type a number.");
                continue;
            }

            // Switch to the right action
            switch (choice) {
                 case 1 -> doCreateAccount(bank, scanner);
                case 2 -> doRemoveAccount(bank, scanner);
                case 3 -> doDeposit(bank, scanner);
                case 4 -> doWithdraw(bank, scanner);
                case 5 -> doTransfer(bank, scanner);
                case 6 -> doViewAccount(bank, scanner);
                case 7 -> doAccountSummary(bank, scanner);
                case 8 -> bank.listAll();
                case 9 -> bank.showLog();
                case 0 -> System.out.println("\n  Goodbye! Thank you for using JavaBank.\n");
                default -> System.out.println("  [Error] Invalid choice. Try 0-9.");
            }

        } while (choice != 0);

        scanner.close();
    }
}

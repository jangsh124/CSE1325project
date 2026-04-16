/**
 * CheckingAccount.java
 * A checking account. Withdrawals of $5 or less are charged a 10% fee.
 */
public class CheckingAccount extends Account {

    private static final double FEE_THRESHOLD = 5.0;
    private static final double FEE_RATE      = 0.10;

    public CheckingAccount(String accountId, String ownerName, double initialDeposit, String pin) {
        super(accountId, ownerName, initialDeposit, AccountType.CHECKING, pin);
    }

    @Override
    public void printInfo() {
        super.printInfo();
        System.out.println("  Fee: 10% on withdrawals less than $" + format(FEE_THRESHOLD));
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("  [Error] Withdrawal must be more than $0.");
            return false;
        }

        if (amount < FEE_THRESHOLD) {
            double fee   = amount * FEE_RATE;
            double total = amount + fee;

            if (getBalance() - total < 0) {
                System.out.println("  [Error] Not enough funds. With 10% fee, total is $" + format(total));
                return false;
            }

            boolean done = super.withdraw(total);
            if (done) {
                System.out.println("  [Fee] 10% fee charged: $" + format(fee));
                System.out.println("  Total deducted: $" + format(total));
            }
            return done;

        } else {
            return super.withdraw(amount);
        }
    }
}

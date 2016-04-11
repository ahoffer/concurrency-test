public class BankAccount {

    String name;

    int balance;

    BankAccount(String name, int balance) {
        this.name = name;
        this.balance = balance;
    }

    private static void log(String msg, Object... arguments) {
        System.err.println(String.format(msg, arguments));
    }

    public synchronized void credit(int amount) {

        balance += amount;
        log("%s's account credited by %d. Balance is now %d", name, amount, balance);
    }

    public void debit(int amount) {

        balance -= amount;
        log("%s's account debited by %d. Balance is now %d", name, amount, balance);

    }
}

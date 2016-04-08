import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

public class DeadlockTest {
    private static final int REPETITIONS = 4;

    Transaction trx1, trx2;

    int amountToBill, amountToSally, transferedToBill, openingBalance;

    private BankAccount sally, bill;

    @Before
    public void setup() {
        openingBalance = 500;

        sally = new BankAccount("Sally", openingBalance);
        bill = new BankAccount("Bill", openingBalance);

        amountToBill = 50;
        amountToSally = 30;

        transferedToBill = REPETITIONS * (amountToBill - amountToSally);
        trx1 = new Transaction(sally, bill, amountToBill);
        trx2 = new Transaction(bill, sally, amountToSally);

        System.err.println("------------------------------------");
    }

    //    @Test
    //    public void sendFunds() {
    //        int amount = 50;
    //        sally.transferFundsTo(bill, amount);
    //        assertThat(bill.balance, equalTo(openingBalance + amount));
    //        assertThat(sally.balance, equalTo(openingBalance - amount));
    //
    //    }

    //    @Test
    //    public void receiveFunds() {
    //        int amount = 150;
    //        sally.receiveFundsFrom(bill, amount);
    //        assertThat(sally.balance, equalTo(openingBalance + amount));
    //        assertThat(bill.balance, equalTo(openingBalance - amount));
    //    }

    @Test
    public void multiThreaded() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(REPETITIONS);

        for (int i = 0; i < REPETITIONS; i++) {
            executor.execute(() -> {
                trx1.transferFunds();
            });
            executor.execute(() -> {
                trx2.transferFunds();
            });
        }

        executor.shutdown();
        boolean allJobsCompleted = executor.awaitTermination(3, TimeUnit.SECONDS);
        assertThat(allJobsCompleted, is(true));
    }

    @Test
    public void singleThreaded() {

        for (int i = 0; i < REPETITIONS; i++) {
            trx1.transferFunds();
            trx2.transferFunds();
        }

        assertThat(bill.balance, equalTo(openingBalance + transferedToBill));
        assertThat(sally.balance, equalTo(openingBalance - transferedToBill));
        assertThat(bill.balance + sally.balance, equalTo(2 * openingBalance));

    }

}
package ua.yet.adv.java.concurrency.transfers;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;

import ua.yet.adv.java.concurrency.Account;

public class SimpleTransfer implements Callable<Boolean> {

    private static final AtomicInteger idGenerator = new AtomicInteger(1);

    private static final int LOCK_WAIT_SEC = 5;
    
    private static final int MAX_TRANSFER_SEC = 7;

    private final int id;

    private final Account accFrom;
    private final Account accTo;
    private final int amount;

    private final Random waitRandom = new Random();

    public SimpleTransfer(@Nonnull Account accFrom, @Nonnull Account accTo, int amount) {
        this.id = idGenerator.getAndIncrement();

        this.accFrom = accFrom;
        this.accTo = accTo;
        this.amount = amount;
    }

    @Override
    public Boolean call() throws Exception {
        if (accFrom.getLock().tryLock(LOCK_WAIT_SEC, TimeUnit.SECONDS)) {
            try {
                if (accFrom.getBalance() < amount) {
                    accFrom.incFailedTransferCount();
                    throw new IllegalStateException("Insufficient funds in Account " + accFrom.getId());
                }

                if (accTo.getLock().tryLock(LOCK_WAIT_SEC, TimeUnit.SECONDS)) {
                    try {

                        accFrom.withdraw(amount);
                        accTo.deposit(amount);

                        Thread.sleep(waitRandom.nextInt(MAX_TRANSFER_SEC*1000));

                        System.out.println("[" + id + "] " + "Transfer " + amount + " done from " + accFrom.getId()
                                + " to " + accTo.getId());

                        return true;

                    } finally {
                        accTo.getLock().unlock();
                    }
                } else {
                    accTo.incFailedTransferCount();
                    return false;
                }
            } finally {
                accFrom.getLock().unlock();
            }
        } else {
            accFrom.incFailedTransferCount();
            return false;
        }
    }

    public int getId() {
        return id;
    }

	public Boolean perform() {
		System.out.println("Performing transfer. Thread " + Thread.currentThread().getId());
		try {
			return call();
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage());
		}
	}

}

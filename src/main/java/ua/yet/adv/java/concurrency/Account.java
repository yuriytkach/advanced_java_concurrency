package ua.yet.adv.java.concurrency;

import java.util.concurrent.atomic.LongAdder;

public class Account {
	
	private final int id;
	
	private int balance;
	
	private final LongAdder failCounter = new LongAdder();

	public Account(int accountId, int initialBalance) {
		this.id = accountId;
		this.balance = initialBalance;
	}
	
	public void deposit(final int amount) {
		balance += amount;
	}
	
	public void withdraw(final int amount) {
		balance -= amount;
	}
	
	public void incFailedTransferCount() {
	    failCounter.increment();
	}
	
	public int getId() {
		return id;
	}

	public int getBalance() {
		return balance;
	}

    public long getFailCount() {
        return failCounter.sum();
    }
	
	

}

package ua.yet.adv.java.concurrency;

public class Account {
	
	private final int id;
	
	private int balance;

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
	
	public int getId() {
		return id;
	}

	public int getBalance() {
		return balance;
	}

}

package ua.yet.adv.java.concurrency;

/**
 * Deadlock simulation by acquiring resources in wrong order
 * 
 * @author yuriy
 */
public class OperationsDeadlock {

	public static void main(String[] args) throws InterruptedException {

		final Account a = new Account(1, 1000);
		final Account b = new Account(2, 2000);

		Thread t1 = new Thread(() -> transfer(a, b, 500) );

		Thread t2 = new Thread(() -> transfer(b, a, 300) );

		outputAmount(a);
		outputAmount(b);
		System.out.println("OperationsDeadlock...");

		t1.start();
		t2.start();

		t1.join();
		t2.join();

		outputAmount(a);
		outputAmount(b);
	}

	private static void outputAmount(Account acc) {
		System.out.println("Account " + acc.getId() + " balance: "
				+ acc.getBalance());
	}

	static void transfer(Account from, Account to, int amount) {

		synchronized (from) {
			System.out.println("Acquired " + from.getId());

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			synchronized (to) {
				System.out.println("Acquired " + to.getId());
				from.withdraw(amount);
				to.deposit(amount);
			}
		}
	}

}

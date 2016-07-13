package ua.yet.adv.java.concurrency;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import ua.yet.adv.java.concurrency.transfers.Transfer;

public class OperationsSynchronizers {

	private static final int NUM_A_B_TRANSFERS = 3;
	private static final int NUM_B_A_TRANSFERS = 4;

	static volatile long transferAbStart;
	static volatile long transferAbEnd;

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		final Account accA = new Account(1, 1000);
		final Account accB = new Account(2, 1000);

		Random rnd = new Random();

		transferAbStart = System.currentTimeMillis();

		// Latch to start all threads simultaneously
		CountDownLatch startLatch = new CountDownLatch(1);

		// Latch to start b->a transfers after a->b transfers
		CountDownLatch baLatch = new CountDownLatch(NUM_A_B_TRANSFERS);

		// Barrier to count overall times for a->b transfers
		CyclicBarrier abBarrier = new CyclicBarrier(NUM_A_B_TRANSFERS,
				new Runnable() {
					@Override
					public void run() {
						transferAbEnd = System.currentTimeMillis();
					}
				});

		// We need to have all threads working as we use cyclic barrier to wait
		// inside them
		ExecutorService service = Executors.newCachedThreadPool();

		// All a->b transfers have startLatch to start simultaneously,
		// baLatch to count down for b->a transfers, and abBarrier to count time
		for (int i = 0; i < NUM_A_B_TRANSFERS; i++) {
			service.submit(new Transfer(accA, accB, rnd.nextInt(400), true,
					startLatch, baLatch, abBarrier));
		}

		for (int i = 0; i < NUM_B_A_TRANSFERS; i++) {
			service.submit(new Transfer(accB, accA, rnd.nextInt(400), true,
					baLatch, null, null));
		}

		service.shutdown();

		// Starting all threads:
		startLatch.countDown();

		// Waiting for all tasks to complete
		boolean rezWait = service.awaitTermination(
				(NUM_A_B_TRANSFERS + NUM_B_A_TRANSFERS) * 2, TimeUnit.SECONDS);

		if (!rezWait) {
			System.err.println("Not all tasks have completed.");
		}

		System.out.println("Overall time for A->B transfers is: "
				+ (transferAbEnd - transferAbStart) + " ms");
	}
}

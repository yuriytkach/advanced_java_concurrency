package ua.yet.adv.java.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ua.yet.adv.java.concurrency.transfers.SimpleTransfer;

/**
 * Showcase for using ExecutorService to start threads in fixed thread pool. In
 * addition to that a scheduled thread is started every 3 secs to poll for
 * unsuccessful transfers.
 * 
 * @author yuriy
 */
public class OperationsService {

	public static void main(String[] args) throws InterruptedException {
		final Account acc1 = new Account(1, 1000);
		final Account acc2 = new Account(2, 1000);

		Random rnd = new Random();

		ScheduledExecutorService amountMonitoring = createSuccessMonitoringThread(acc1);

		List<SimpleTransfer> transfers = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			transfers.add(new SimpleTransfer(acc1, acc2, rnd.nextInt(400)));
		}

		ExecutorService service = Executors.newFixedThreadPool(3);
		List<Future<Boolean>> rez = service.invokeAll(transfers);
		service.shutdown();

		// Will throw exception if uncommented
		// service.submit(new Transfer(acc2, acc1, 500));
		
		System.out.println("Future results:");
		
		for (int i=0; i < rez.size(); i++) {
		    Future<Boolean> future = rez.get(i);
		    SimpleTransfer transfer = transfers.get(i);
		    try {
				System.out.println("[" + transfer.getId() + "] Transfer: " + future.get());
			} catch (ExecutionException e) {
			    System.out.println("[" + transfer.getId() + "] Transfer: " + e.getMessage());
			}
		}

		amountMonitoring.shutdown();
	}

	private static ScheduledExecutorService createSuccessMonitoringThread(
			final Account acc1) {
		ScheduledExecutorService amountMonitoring = Executors
				.newScheduledThreadPool(1);
		amountMonitoring.scheduleAtFixedRate(new Runnable() {
			public void run() {
				System.out.println("Failed transfers in Account 1: "
						+ acc1.getFailCount());
			}
		}, 2, 3, TimeUnit.SECONDS);
		return amountMonitoring;
	}
}

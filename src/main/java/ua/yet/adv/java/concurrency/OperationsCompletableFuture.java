package ua.yet.adv.java.concurrency;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.LongAdder;

import ua.yet.adv.java.concurrency.transfers.SimpleTransfer;

public class OperationsCompletableFuture {

	public static void main(String[] args) {
		new OperationsCompletableFuture().runTransfers(10);
	}
	
	private final Account acc1 = new Account(1, 1000);
	private final Account acc2 = new Account(2, 500);
	private final Account acc3 = new Account(3, 500);
	
	private final Random rnd = new Random();
	
	private LongAdder totalTransferAttempts = new LongAdder();
	private LongAdder totalUnsuccessTransfers = new LongAdder();
	
	public void runTransfers(int count) {
		CompletableFuture<?>[] futures = new CompletableFuture[count];
		
		for (int i = 0; i < count; i++) {
			CompletableFuture<Integer> futureAmount = CompletableFuture.supplyAsync(()->requestTransferAmount());
			
			CompletableFuture<Void> futureTransfer = CompletableFuture.supplyAsync(()->requestDestinationAccount())
				.thenCombineAsync(futureAmount, (acc2, amount) -> createTransfer(acc1, acc2, amount))
				.thenApplyAsync(transfer -> transfer.perform())
				.exceptionally(ex -> processTransferException(ex))
				.thenAcceptAsync(result -> processTransferResult(result))
				.thenRunAsync(() -> updateTransferCount());
			
			futures[i] = futureTransfer;
		}
		
		System.out.println("Waiting for all...");
		CompletableFuture.allOf(futures).join();
		
		System.out.println("Done.");
		System.out.println("Total transfers: " + totalTransferAttempts);
		System.out.println("Total failures: " + totalUnsuccessTransfers);
		System.out.println("Total failures on acc1: " + acc1.getFailCount());
	}
	
	private void updateTransferCount() {
		System.out.println("Updated transfer count. Thread " + Thread.currentThread().getId());
		totalTransferAttempts.increment();
	}

	private void processTransferResult(Boolean result) {
		System.out.println("Transfer result: " + result + ". Thread " + Thread.currentThread().getId());
	}

	private Account requestDestinationAccount() {
		System.out.println("Request amount. Thread " + Thread.currentThread().getId());
		return rnd.nextInt(1) == 1 ? acc2 : acc3;
	}
	
	private int requestTransferAmount() {
		System.out.println("Request amount. Thread " + Thread.currentThread().getId());
		return rnd.nextInt(400);
	}
	
	private SimpleTransfer createTransfer(Account acc1, Account acc2, int amount) {
		System.out.println("Creating transfer. Thread " + Thread.currentThread().getId());
		return new SimpleTransfer(acc1, acc2, amount);
	}
	
	private Boolean processTransferException(Throwable ex) {
		System.out.println("Failed transfer: " + ex.getMessage() + ". Thread " + Thread.currentThread().getId());
		totalUnsuccessTransfers.increment();
		return false;
	}

}

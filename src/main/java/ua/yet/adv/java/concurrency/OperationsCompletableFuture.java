package ua.yet.adv.java.concurrency;

import java.util.concurrent.CompletableFuture;

import ua.yet.adv.java.concurrency.transfers.SimpleTransfer;

public class OperationsCompletableFuture {

	public static void main(String[] args) {
		final Account acc1 = new Account(1, 10);
		
		CompletableFuture<Integer> futureAmount = CompletableFuture.supplyAsync(()->requestTransferAmount());
		
		CompletableFuture<Void> futureTransfer = CompletableFuture.supplyAsync(()->requestDestinationAccount())
			.thenCombineAsync(futureAmount, (acc2, amount) -> createTransfer(acc1, acc2, amount))
			.thenApplyAsync(transfer -> transfer.perform())
			.exceptionally(ex -> processTransferException(ex))
			.thenAcceptAsync(result -> processTransferResult(result))
			.thenRunAsync(() -> updateTransferCount());
		
		System.out.println("Waiting for the future...");
		futureTransfer.join();
		System.out.println("Done");
	}
	
	private static void updateTransferCount() {
		System.out.println("Updated transfer count. Thread " + Thread.currentThread().getId());
	}

	private static void processTransferResult(Boolean result) {
		System.out.println("Transfer result: " + result + ". Thread " + Thread.currentThread().getId());
	}

	private static Account requestDestinationAccount() {
		System.out.println("Request amount. Thread " + Thread.currentThread().getId());
		return new Account(2, 500);
	}
	
	private static int requestTransferAmount() {
		System.out.println("Request amount. Thread " + Thread.currentThread().getId());
		return 50;
	}
	
	private static SimpleTransfer createTransfer(Account acc1, Account acc2, int amount) {
		System.out.println("Creating transfer. Thread " + Thread.currentThread().getId());
		return new SimpleTransfer(acc1, acc2, amount);
	}
	
	private static Boolean processTransferException(Throwable ex) {
		System.out.println("Failed transfer: " + ex.getMessage() + ". Thread " + Thread.currentThread().getId());
		return false;
	}

}

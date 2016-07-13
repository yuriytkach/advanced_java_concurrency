package ua.yet.adv.java.concurrency;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestAccount {

	@Test
	public void testBalance() {
		Account acc = new Account(1, 1000);
		
		acc.deposit(500);
		assertEquals(1500, acc.getBalance());
		
		acc.withdraw(100);
		assertEquals(1400, acc.getBalance());
	}
	
	public void testIncFailedTransferCount() {
	    Account acc = new Account(1, 1);
	    assertEquals(0, acc.getFailCount());
	    
	    acc.incFailedTransferCount();
	    assertEquals(1, acc.getFailCount());
	    
	    acc.incFailedTransferCount();
	    acc.incFailedTransferCount();
	    assertEquals(3, acc.getFailCount());
	}

}

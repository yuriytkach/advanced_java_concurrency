package ua.yet.adv.java.concurrency;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import ua.yet.adv.java.concurrency.transfers.SimpleTransfer;

public class TestSimpleTransfer {

    @Test
    public void testTransferNoLocks() throws Exception {
        Account acc1 = new Account(1, 100);
        Account acc2 = new Account(2, 50);
        
        SimpleTransfer transfer = new SimpleTransfer(acc1, acc2, 10);
        boolean result = transfer.call();
        
        assertTrue(result);
        assertEquals(90, acc1.getBalance());
        assertEquals(60, acc2.getBalance());
        assertEquals(0, acc1.getFailCount());
        assertEquals(0, acc2.getFailCount());
    }
    
    @Test(expected = IllegalStateException.class)
    public void testTransferNoFunds() throws Exception {
        Account acc1 = new Account(1, 100);
        Account acc2 = new Account(2, 50);
        
        SimpleTransfer transfer = new SimpleTransfer(acc1, acc2, 1000);
        transfer.call();
    }
    
    @Test
    public void testTransferLockedFrom() throws Exception {
        Account acc1 = new Account(1, 100);
        Account acc2 = new Account(2, 50);
        
        acc1.getLock().lock();
        
        Thread checkThread = new Thread(() -> {
            SimpleTransfer transfer = new SimpleTransfer(acc1, acc2, 10);
            boolean result;
            try {
                result = transfer.call();
                assertFalse(result);
            } catch (Exception e) {
                fail(e.getMessage());
            }
            
            assertEquals(100, acc1.getBalance());
            assertEquals(50, acc2.getBalance());
            assertEquals(1, acc1.getFailCount());
            assertEquals(0, acc2.getFailCount());
        });
        
        try {
            checkThread.start();
            checkThread.join();
        } finally {
            acc1.getLock().unlock();
        }
    }
    
    @Test
    public void testTransferLockedTo() throws Exception {
        Account acc1 = new Account(1, 100);
        Account acc2 = new Account(2, 50);
        
        acc2.getLock().lock();
        
        Thread checkThread = new Thread(() -> {
            SimpleTransfer transfer = new SimpleTransfer(acc1, acc2, 10);
            boolean result;
            try {
                result = transfer.call();
                assertFalse(result);
            } catch (Exception e) {
                fail(e.getMessage());
            }
            
            assertEquals(100, acc1.getBalance());
            assertEquals(50, acc2.getBalance());
            assertEquals(0, acc1.getFailCount());
            assertEquals(1, acc2.getFailCount());
        });
        
        try {
            checkThread.start();
            checkThread.join();
        } finally {
            acc2.getLock().unlock();
        }
    }

}

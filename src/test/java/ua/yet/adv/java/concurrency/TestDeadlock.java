package ua.yet.adv.java.concurrency;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

import org.junit.Assert;
import org.junit.Test;

public class TestDeadlock {

	@Test
	public void testDeadlock() throws InterruptedException {
		new Thread(() -> OperationsDeadlock.main(null)).start();

		// Sleeping for deadlock to happen
		Thread.sleep(1000);

		// Programmatic way to detect deadlock

		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		long[] threadIds = bean.findDeadlockedThreads(); // Returns null if no
															// threads are
															// deadlocked.

		Assert.assertNotNull(threadIds);
		Assert.assertEquals(2, threadIds.length);

		ThreadInfo[] infos = bean.getThreadInfo(threadIds);

		for (ThreadInfo info : infos) {
			System.out.println(info);
		}
	}

}

package ua.yet.adv.java.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Small showcase for using the semaphore. Only 2 workers are have permits to
 * work. Later (after 200 milliseconds) the release() is called from outside
 * thread thus adding one more permit. The fourth worker will start after first
 * one will release the permit.
 * 
 * @author yuriy
 */
public class OperationsSemaphore {

	static private Semaphore sem = new Semaphore(2);

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		ExecutorService service = Executors.newCachedThreadPool();
		service.execute(new Worker(1));
		service.execute(new Worker(2));
		service.execute(new Worker(3));
		service.execute(new Worker(4));

		service.execute(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
				}
				System.out.println("releasing...");
				sem.release();
			}
		});

		service.shutdown();

	}

	static class Worker extends Thread {

		private final int id;

		public Worker(int id) {
			super();
			this.id = id;
		}

		@Override
		public void run() {
			System.out.println(id + " - trying to aquire...");
			try {
				sem.acquire();
				System.out.println(id + " - aquired");
				sleep(1000);
				sem.release();
				System.out.println(id + " - released");
			} catch (InterruptedException e) {
				System.err.println(id + " - interrupted");
			}
		}

	}

}

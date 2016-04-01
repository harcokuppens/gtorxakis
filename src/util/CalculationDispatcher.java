package util;

import java.util.concurrent.Semaphore;
import java.util.Stack;

/**
 * Manages the execution of calculators.
 * Calculators can be executed by calling
 */
public class CalculationDispatcher extends Thread {
	private CalculationWrapper current;
	private Stack<CalculationWrapper> pending;
	private boolean isRunning;
	private final Semaphore accessSemaphore;
	private final Semaphore workSemaphore;
	
	public CalculationDispatcher() {
		current = null;
		pending = new Stack<CalculationWrapper>();
		isRunning = false;
		accessSemaphore = new Semaphore(1);
		workSemaphore = new Semaphore(0);
	}

	public void run() {
		while(true) {
			workSemaphore.acquireUninterruptibly();
			accessSemaphore.acquireUninterruptibly();
			current = pending.pop();
			isRunning = true;
			accessSemaphore.release();
			try {
				current.run();
				System.out.println("finished");
			} catch(Exception e) {
				System.out.println("show must go on");
			}
			accessSemaphore.acquireUninterruptibly();
			isRunning = false;
			current = null;
			accessSemaphore.release();
		}
	}

	public void run(CalculationWrapper w) {
		accessSemaphore.acquireUninterruptibly();
		pending.add(w);
		if(isRunning) {
			System.out.println("aborting current");
			try {
				current.abort();
			} catch(Exception e) {
				System.out.println("show must go on");
			}
		}
		workSemaphore.release();
		accessSemaphore.release();

	}
}
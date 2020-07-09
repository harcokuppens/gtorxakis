package util;

import gui.control.DrawController;

import java.util.concurrent.Semaphore;

public abstract class SVGRunnable implements Runnable {
	private DrawController dc;
	private boolean isInvoked = false;

	private Semaphore blockingSemaphore;
	
	public SVGRunnable(DrawController dc, boolean invokeImmediately) {
		this.dc = dc;
		this.blockingSemaphore = new Semaphore(0);
		if(invokeImmediately) {
			invoke();
		}
	}
	
	public void invoke() {
		if(isInvoked) return;
		else {
			isInvoked = true;			
			dc.addToQueue(this);
		}
	}

	public void signalCompletion() {
		blockingSemaphore.release();
	}

	public void invokeAndWait() {
		invoke();
		blockingSemaphore.acquireUninterruptibly();
	}
}

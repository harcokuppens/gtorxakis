package util;


/**
 * A simple runnable that can execute a specified action on completion. 
 */
public abstract class AsynchronousTask implements Runnable{

	private boolean isCancelled = false;

	public AsynchronousTask() {

	}

	@Override
	public final void run() {
		executeTask();
		if(!isCancelled) {
			onCompletion();
		}
	}

	public final void start() {
		Thread t = new Thread(this);
		t.start();
	}

	public final void cancel() {
		isCancelled = true;
	}

	protected abstract void executeTask();

	protected abstract void onCompletion();


}
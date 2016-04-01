package gui.window;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * This class represents a handler which can be used to display messages in the statusbar. These messages can either be constantly visible or just for a specific amount of time.
 * @author Lars Bade
 *
 */
public class StatusBarMessageHandler extends Timer {
	private StatusBar bar;
	private TimerTask resetTask;
	
	public StatusBarMessageHandler(StatusBar bar) {
		this.bar = bar;
		this.resetTask = this.generateTask(StatusBar.DEFAULT_MESSAGE);
		this.showMessage("Double-click in the drawing area to add a state");
	}
	
	public void showMessage(String message) {
		this.resetTask.cancel();
		this.generateTask(message).run();
		this.resetTask = this.generateTask(StatusBar.DEFAULT_MESSAGE);
	}
	
	public void showMessage(String message, long duration) {
		this.showMessage(message);
		this.schedule(this.resetTask, duration);
	}
	
	public void showMessage(String message, long duration, TimeUnit unit) {
		this.showMessage(message, TimeUnit.MILLISECONDS.convert(duration, unit));
	}
	
	private TimerTask generateTask(final String message) {
		return new TimerTask() {
			@Override
			public void run() {
				bar.setMessage(message);
			}
		};
	}
}

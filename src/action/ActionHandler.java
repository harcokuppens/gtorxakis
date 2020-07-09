package action;

import java.util.Stack;
import java.util.concurrent.Semaphore;

import model.Model;
import core.Session;

public class ActionHandler extends Thread {
	public Stack<Action> actionStack;
	public Semaphore invocSem; 

	private final Model model;
	
	/**
	 * Creates a new ActionHandler for a specific model.
	 * @param model The model that owns this Action Handler
	 */
	public ActionHandler(Model model) {
		this.model = model;
		actionStack = new Stack<Action>();
		invocSem = new Semaphore(0);
	}
	
	public void invokeAction(Action a) {
		actionStack.add(a);
		invocSem.release();
	}
	
	@Override
	public void run() {
		while(true) {
			invocSem.acquireUninterruptibly();
			Action a = actionStack.pop();
			a.run(model.getDrawController());
		}
	}
}

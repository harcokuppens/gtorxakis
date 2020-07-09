package action;

import gui.control.DrawController;


public abstract class Action {
	private boolean needsConfirm;
	
	protected Action(boolean needsConfirm) {
		this.needsConfirm = needsConfirm;
	}
	
	public boolean needsConfirm() {
		return needsConfirm;
	}

	/**
	 * Executes the action
	 * @param dc The DrawController to use
	 */
	public abstract void run(DrawController dc);
}

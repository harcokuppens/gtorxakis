package model;

import action.Action;
import action.ActionHandler;
import util.DoubleLinkedList;

public abstract class Definition {
	
	protected DoubleLinkedList<Action> actionHistory;
	
	public Definition(){
		actionHistory = new DoubleLinkedList<Action>();

	}
	
	public boolean isSaved() {
		return actionHistory.isMarked();
	}

	public void setSaved() {
		actionHistory.mark();
	}

}

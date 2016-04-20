package action;

import gui.control.Movable;
import gui.draw.GraphInterface;
import gui.control.DrawController;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;

import util.Vector;

public class DrawMoveAction extends Action {
	private Runnable svgAction;
	
	public DrawMoveAction(ArrayList<Movable> movableList, Vector vector) {
		super(false);
		Vector[] tmp = new Vector[movableList.size()];
		Arrays.fill(tmp, vector);
		init(movableList, tmp);
	}
	
	public DrawMoveAction(ArrayList<Movable> movableList, Vector[] vectorA) {
		super(false);
		init(movableList, vectorA);
	}
	
	private void init(ArrayList<Movable> movableList, Vector[] vectorA) {
		final ArrayList<Movable> movables = new ArrayList<Movable>(movableList);
		final Vector[] vA = vectorA;
		svgAction = new Runnable(){
			@Override
			public void run(){
				for (int i = 0; i < movables.size(); i++) {
					movables.get(i).moveBy(vA[i]);
				}
			}
		};
	}
	
	@Override
	public void run(DrawController dc) {
		dc.addToQueue(svgAction);
	}

}

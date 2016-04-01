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
	
	public DrawMoveAction(ArrayList<Movable> movableList, Point point) {
		super(false);
		Point[] tmp = new Point[movableList.size()];
		Arrays.fill(tmp, point);
		init(movableList, tmp);
	}
	
	public DrawMoveAction(ArrayList<Movable> movableList, Point[] pointA) {
		super(false);
		init(movableList, pointA);
	}
	
	private void init(ArrayList<Movable> movableList, Point[] pointA) {
		final ArrayList<Movable> movables = new ArrayList<Movable>(movableList);
		final Point[] pA = pointA;
		svgAction = new Runnable(){
			@Override
			public void run(){
				for (int i = 0; i < movables.size(); i++) {
					movables.get(i).moveTo(pA[i]);
				}
			}
		};
	}
	
	
	@Override
	public void run(DrawController dc) {
		dc.addToQueue(svgAction);
	}

}

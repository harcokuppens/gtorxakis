package action;

import java.awt.Point;

import util.Vector;
import gui.control.Resizable;
import gui.control.DrawController;
import gui.draw.DrawableComment.ResizeType;
import gui.draw.GraphInterface;

public class DrawResizeAction extends Action{
	private Runnable svgAction;
	
	public DrawResizeAction(Resizable r, int dx, ResizeType resizeType) {
		super(false);
		init(r, dx, resizeType);
	}
	
	public void init(final Resizable r,final int dx,final ResizeType resizeType){
		svgAction = new Runnable(){
			@Override
			public void run(){
					r.resizeBy(dx, resizeType);
			}
		};
	}

	@Override
	public void run(DrawController dc) {
		dc.addToQueue(svgAction);
	}

}

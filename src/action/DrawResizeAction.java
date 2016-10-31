package action;

import gui.control.Resizable;
import gui.control.DrawController;
import gui.draw.DrawableComment.ResizeType;

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

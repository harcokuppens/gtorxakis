package model.graph;

import gui.draw.DrawableComment;

public class GraphComment {
	private DrawableComment drawable;
	
	public GraphComment(DrawableComment drawable){
		this.drawable = drawable;
	}
	
	public DrawableComment getDrawable() {
		return drawable;
	}
	
	public void setDrawable(DrawableComment drawable){
		this.drawable = drawable;
	}

}

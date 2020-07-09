package model.graph;

import gui.draw.DrawableComment;

public class GraphComment {
	private DrawableComment drawable;
	private GraphEdge edge;
	
	public GraphComment(DrawableComment drawable, GraphEdge edge){
		this.drawable = drawable;
		this.edge = edge;
	}
	
	public DrawableComment getDrawable() {
		return drawable;
	}
	
	public GraphEdge getEdge(){
		return edge;
	}
	
	public void setDrawable(DrawableComment drawable){
		this.drawable = drawable;
	}

}

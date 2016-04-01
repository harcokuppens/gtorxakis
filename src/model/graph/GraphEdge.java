package model.graph;

import action.Configurable;
import gui.control.DrawController;
import gui.draw.DrawableGraphEdge;


public class GraphEdge implements Configurable{
	private GraphState from, to;
	private String name;
	
	private DrawableGraphEdge drawable;
	
	public static final String ATTRIBUTE_NAME = "name";
	
	public GraphEdge(GraphState from, GraphState to, String name) {
		this.from = from;
		this.to = to;
		this.name = name;
	}
	
	/**
	 * Copy constructor
	 * @param draft The GraphEdge to be duplicated
	 */
	protected GraphEdge(GraphEdge draft) {
		this.from = draft.from;
		this.to = draft.to;
		this.name = draft.name;
	}
	
	public GraphState getFrom() {
		return from;
	}
	
	public GraphState getTo() {
		return to;
	}
	
	public void setFrom(GraphState from) {
		this.from = from;
		drawable.invalidate();
	}
	
	public void setTo(GraphState to) {
		this.to = to;
		drawable.invalidate();
	}
	
	public void setDrawable(DrawableGraphEdge drawable) {
		this.drawable = drawable;
	}

	public DrawableGraphEdge getDrawable() {
		return drawable;
	}
	
	@Override
	public GraphEdge clone() {
		return new GraphEdge(this);
	}
	
	public void setAttribute(String cmd, Object value){
		switch (cmd){
		case (GraphEdge.ATTRIBUTE_NAME) :
			name = (String) value;
		break;
		default :
			System.err.println("Tried to set invalid attribute " + cmd);			
		}

	}
	
	public Object getAttribute(String cmd){
		switch(cmd) {
		case GraphEdge.ATTRIBUTE_NAME:
			return name;
		default:
			System.err.println("Tried to get non existing attribute of GraphEdge (" + cmd + ")!");
			return null;
		}
	}

	@Override
	public void updateConfigs(DrawController dc) {
		drawable.update(dc);
	}

	public boolean hasName() {
		return !name.equals("");
	}
}

package model.graph;

import gui.draw.DrawableGraphState;
import gui.control.DrawController;

import java.util.ArrayList;
import action.Configurable;

public class GraphState implements Configurable {
	private Graph g;
	
	private ArrayList<GraphEdge> outgoingEdges, incomingEdges;
	private String name;
	private static final String DEFAULT_NAME = "State_0";
	private boolean startState = false;
	
	public static final String 
			ATTRIBUTE_NAME = "name",
			ATTRIBUTE_START_STATE = "startState";
	;
	
	private DrawableGraphState drawable;
	
	public GraphState() {
		this(DEFAULT_NAME);
	}
	
	public GraphState(String name) {
		this.name = name;
		incomingEdges = new ArrayList<GraphEdge>();
		outgoingEdges = new ArrayList<GraphEdge>();
	}
	
	
	/**
	 * Copy constructor
	 * @param draft the GraphNode to be duplicated
	 */
	public GraphState(GraphState draft) {
		this.name = draft.name;
		incomingEdges = new ArrayList<GraphEdge>();
		outgoingEdges = new ArrayList<GraphEdge>();
	}
	
	public String getName() {
		return name;
	}
	
	public boolean hasEdgetoNode(GraphState n){
		for(GraphEdge e : this.getOutgoingEdges()){
			if(e.getTo().equals(n)){
				return true;
			}
		}
		return false;
	}
	
	public boolean isEndogenetic() {
		return incomingEdges.size() >= 1;
	}
	
	public boolean isExogenetic() {
		return incomingEdges.size() == 0;
	}
	
	public boolean hasOutgoingEdges() {
		return outgoingEdges.size() > 0;
	}
	
	public void setDrawable(DrawableGraphState drawable) {
		this.drawable = drawable;
	}
	
	public DrawableGraphState getDrawable() {
		return drawable;
	}
	
	public void setAttribute(String cmd, Object value){
		switch (cmd){
		case (GraphState.ATTRIBUTE_NAME) :
			String candidateName = (String) value;
			if(g == null || name != null && name.equals(candidateName)) {
				name = candidateName;
			} else {
				name = g.getValidName(candidateName, name);
			}
			break;
		case ATTRIBUTE_START_STATE:
			startState = (boolean) value;
			break;
		default :
		}

	}
	
	public Object getAttribute(String cmd){
		switch(cmd) {
		case GraphState.ATTRIBUTE_NAME:
			return getName();
		case ATTRIBUTE_START_STATE:
			return startState;
		default:
			return null;
		}
	}
	
	public void addOutgoingEdge(GraphEdge edge) {
		outgoingEdges.add(edge);
	}
	
	public void addIncomingEdge(GraphEdge edge) {
		incomingEdges.add(edge);
	}
	
	public void removeOutgoingEdge(GraphEdge edge) {
		outgoingEdges.remove(edge);
	}
	
	public void removeIncomingEdge(GraphEdge edge) {
		incomingEdges.remove(edge);
	}
	
	public ArrayList<GraphEdge> getOutgoingEdges() {
		return outgoingEdges;
	}

	public ArrayList<GraphEdge> getIncomingEdges() {
		return incomingEdges;
	}
	
	@Override
	public GraphState clone() {
		return new GraphState(this);
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof GraphState)) return false;
		else {
			GraphState n = (GraphState) o;
			return this.name.equals(n.name);
		}
	}
	
	@Override
	public String toString() {
		return name;
	}

	public void setGraph(Graph g) {
		this.g = g;
	}

	@Override
	public void updateConfigs(DrawController dc) {
		drawable.update(dc);
	}


}

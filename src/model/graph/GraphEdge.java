package model.graph;

import java.util.ArrayList;

import action.Configurable;
import gui.control.DrawController;
import gui.draw.DrawableGraphEdge;
import model.Transition;


public class GraphEdge implements Configurable{
	private GraphState from, to;
//	private String name;
	private ArrayList<Transition> transitions;
	
	private DrawableGraphEdge drawable;
	
	public static final String ATTRIBUTE_NAME = "name";
	public static final String ATTRIBUTE_TRANSITIONS = "transitions";
	
	public GraphEdge(GraphState from, GraphState to) {
		this(from, to, new ArrayList<Transition>());
	}
	
	public GraphEdge(GraphState from, GraphState to, ArrayList<Transition> transitions){
		this.from = from;
		this.to = to;
		this.transitions = transitions;
		this.transitions.add(Transition.getDefaultTransition());
	}
	
	/**
	 * Copy constructor
	 * @param draft The GraphEdge to be duplicated
	 */
	protected GraphEdge(GraphEdge draft) {
		this.from = draft.from;
		this.to = draft.to;
		this.transitions = new ArrayList<Transition>(draft.transitions);
	}
	
	public GraphState getFrom() {
		return from;
	}
	
	public GraphState getTo() {
		return to;
	}
	
	public ArrayList<Transition> getTransitions(){
		return transitions;
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
		case (GraphEdge.ATTRIBUTE_TRANSITIONS) :
			transitions = new ArrayList<Transition>((ArrayList<Transition>) value);
		break;
		default :
			System.err.println("Tried to set invalid attribute " + cmd);			
		}

	}
	
	public Object getAttribute(String cmd){
		switch(cmd) {
		case GraphEdge.ATTRIBUTE_TRANSITIONS:
			return transitions;
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
		return transitions.size() > 0;
	}
	
	public String getTransitionText(){
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for(Transition t : transitions){
			System.out.println(t);
			if(!t.getCondition().equals("")){
				sb.append("if("+t.getCondition()+")");
			}
			sb.append("\t"+t.getChannel() + " {" + t.getAction() + "}");
			i++;
			if(i < transitions.size()){
				sb.append("\n");
			}
		}
		return sb.toString();
	}
}

package model.graph;


import java.util.ArrayList;
import java.util.Iterator;

import core.Session;

public class Graph {

	private ArrayList<GraphState> states;
	private ArrayList<GraphComment> comments;
	
	public Graph() {
		states = new ArrayList<GraphState>();
		comments = new ArrayList<GraphComment> ();
	}
	
	public boolean isValid(){
		if(states.size() == 0){
			return false;
		}
		if(states.size() == 1){
			return true;
		}
		for(GraphState n : states){
			if(n.getOutgoingEdges().size() == 0 || n.getIncomingEdges().size() == 0){
				return false;
			}
		}
		return true;
	}
	
	public void addState(GraphState state) {
		String validName = this.getValidName(state.getName(), null);
		state.setGraph(this);
		state.setAttribute(GraphState.ATTRIBUTE_NAME, validName);
		this.states.add(state);
	}
	
	public void addStates(ArrayList<? extends GraphState> states) {
		boolean showLimitMessage = false;
		for(GraphState s: states) {
			addState(s);
		}
	}
	
	public void removeStates(ArrayList<? extends GraphState> states) {
		Iterator<? extends GraphState> i = states.iterator();
		while(i.hasNext()) {
			removeState(i.next());
		}
	}
	
	public void removeState(GraphState s) {
		s.setGraph(null);
		states.remove(s);
	}
	
	public ArrayList<GraphState> getStates(){
		return states;
	}
	
	public ArrayList<GraphState> getEndogenStates() {
		ArrayList<GraphState> endNodes = new ArrayList<GraphState>();
		for(GraphState n: states) {
			if(!n.getIncomingEdges().isEmpty())
				endNodes.add(n);
		}
		return endNodes;
	}
	
	public ArrayList<GraphState> getExogenStates() {
		ArrayList<GraphState> exoStates = new ArrayList<GraphState>();
		for(GraphState n: states) {
			if(n.getIncomingEdges().isEmpty())
				exoStates.add(n);
		}
		return exoStates;
	}

	public void addComments(ArrayList<GraphComment> graphComments){
		for(GraphComment gc : graphComments){
			if(!comments.contains(gc)){
				comments.add(gc);
			}
		}
	}
	
	public void removeComments(ArrayList<GraphComment> graphComments){
		for(GraphComment gc : graphComments){
			if(comments.contains(gc)){
				System.out.println("[Graph] Delete Comment!");
				comments.remove(gc);
			}
		}
	}
	
	public ArrayList<GraphComment> getComments() {
		return comments;
	}
	
	public ArrayList<String> getNodeNames() {
		ArrayList<String> names = new ArrayList<String>();
		for(GraphState node : getStates()) {
			names.add(node.getName());
		}
		return names;
	}
	
	public int getNodeIndex(GraphState node) {
		return states.indexOf(node);
	}
	
	public GraphState getStateWithName(String name){
		for(GraphState s : states){
			if(s.getName().equalsIgnoreCase(name)){
				return s;
			}
		}
		return null;
	}
	
	/**
	 * Checks if the graph already contains a state named 'name'. 
	 * @param name the name of interest
	 * @return true if the name is used, false otherwise
	 */
	public boolean containsName (String name){
		for(GraphState s : states){
			if(s.getAttribute("name").equals(name)){
				return true;
			}
		}
		return false;
	}
	
	public String getValidName(String candidateName, String originalName) {
		if(this.containsName(candidateName)) {
			// check if candidate name already contains an index
			String[] components = candidateName.split("_");
			if(components.length >= 2) {
				// composed name. We try to extract the index.
				try {
					// Attempt to convert name tail to index
					Integer.valueOf(components[components.length - 1]);
					// We've extracted the index, now we'll compose the head of the name.
					StringBuilder sb = new StringBuilder();
					for(int i = 0; i < components.length - 1; i++) {
						sb.append(components[i]);
						if(i < components.length - 2) {
							sb.append("_");
						}
					}
					candidateName = sb.toString();
				} catch(NumberFormatException e) {
					// Apparently it's not only an index. Stick to the current name.
				}
			} 
			// Always start searchign at index 1
			return getValidName(candidateName, 1, originalName);
		} else {
			return candidateName;
		}
	}

	private String getValidName(String candidateName, int index, String originalName) {
		String composedName = candidateName + "_" + String.valueOf(index);
		if(this.containsName(composedName)) {
			if(originalName != null && composedName.equals(originalName)) {
				return originalName;
			} else {
				return getValidName(candidateName, ++index, originalName);
			}
		} else {
			return composedName;
		}
	}
}

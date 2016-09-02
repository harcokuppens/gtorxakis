package model;

import org.w3c.dom.svg.SVGDocument;

import core.Session;
import gui.control.DrawController;
import gui.draw.DrawableGraph;
import gui.draw.DrawableGraphState;
import gui.draw.DrawableGraphEdge;
import gui.draw.GraphInterface;
import model.Gates.Gate;
import model.Variables.Variable;
import model.graph.Graph;
import model.graph.GraphState;
import model.graph.GraphEdge;
import util.DoubleLinkedList;
import action.Action;
import action.ActionHandler;


import java.util.ArrayList;

/**
 * A model holding a graph. 
 */
public class Model extends Definition {
	private String name;
	private final Project project;
	private Graph graph;
	private DrawableGraph drawableGraph;
	private DrawController drawController;
	private GraphInterface graphInterface;
	
	private ActionHandler actionHandler;
	private DoubleLinkedList<Action> actionHistory;
	
	private Gates gates;
	private Variables variables;
	
	public Model(Project project, String name, Graph graph, DrawableGraph drawableGraph, ArrayList<Gate> gates, ArrayList<Variable> variables) {
		super(name);
		this.project = project;
		this.name = name;
		this.graph = graph;
		this.drawableGraph = drawableGraph;
		this.graphInterface = new GraphInterface(this, graph, drawableGraph);
		this.drawController = new DrawController(graphInterface);
		this.gates = new Gates(gates);
		this.variables = new Variables(variables);
		actionHistory = new DoubleLinkedList<Action>();
		
		actionHandler = new ActionHandler(this);
		actionHandler.start();
	}
	
	public Model(Project project, String name, Graph graph, DrawableGraph drawableGraph) {
		this(project, name, graph, drawableGraph, new ArrayList<Gate>(), new ArrayList<Variable>());
	}
	
	

	public Model clone(Project p, String name, Graph graph, DrawableGraph drawableGraph) {
		SVGDocument doc = drawableGraph.getDocument();
		ArrayList<GraphState> clonedNodes = new ArrayList<GraphState>();
		ArrayList<DrawableGraphState> clonedDrawableNodes = new ArrayList<DrawableGraphState>();
		GraphState start = this.graph.getStartState();
		
		for(DrawableGraphState n : this.drawableGraph.getNodes()){
			DrawableGraphState dgn = n.clone(doc);
			if(n.getNode().equals(start)) dgn.getNode().setAttribute(GraphState.ATTRIBUTE_START_STATE, true);
			clonedDrawableNodes.add(dgn);
			clonedNodes.add(dgn.getNode());
		}
		graph.addStates(clonedNodes);
		
		for(DrawableGraphEdge dge: this.drawableGraph.getEdges()) {
			DrawableGraphEdge clonedDge = dge.clone(doc);
			GraphEdge e = clonedDge.getEdge();
			int index_from = this.drawableGraph.getNodes().indexOf(e.getFrom().getDrawable());
			e.setFrom(clonedDrawableNodes.get(index_from).getNode());
			e.getFrom().addOutgoingEdge(e);
			int index_to = this.drawableGraph.getNodes().indexOf(e.getTo().getDrawable());
			e.setTo(clonedDrawableNodes.get(index_to).getNode());
			e.getTo().addIncomingEdge(e);
		}

		return new Model(p, name, graph, drawableGraph);
	}

	public Gates getGates(){
		return gates;
	}
	
	public Variables getVariables(){
		if(variables == null)
			System.err.println("Model get variables is null");
		return variables;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DrawController getDrawController() {
		return drawController;
	}

	public GraphInterface getGraphInterface() {
		return graphInterface;
	}

	public Graph getGraph() {
		return graph;
	}

	public void resetActionHistory(){
		this.actionHistory = new DoubleLinkedList<Action>();
	}
	
	public boolean canUndo() {
		return actionHistory.hasPrevious();
	}

	public boolean canRedo() {
		return actionHistory.hasNext();
	}

	public void performAction(Action a, Action u) {
		actionHistory.append(a, u);
		performAction(a);
	}

	public void undoAction() {
		performAction(actionHistory.toPrevious());
	}
 
	public void redoAction() {
		performAction(actionHistory.toNext());
	}
	
	private void performAction(Action a) {
		actionHandler.invokeAction(a);
		Session.getSession().invalidate();
	}

	public static Model newModel(Project project, String name) {
		Graph graph = new Graph();
		DrawableGraph drawableGraph = new DrawableGraph();
		SVGDocument doc = drawableGraph.getDocument();
		int docW = Integer.valueOf(doc.getRootElement().getAttribute("width"));
		int docH = Integer.valueOf(doc.getRootElement().getAttribute("height"));
		DrawableGraphState dgn = new DrawableGraphState(doc, docW/2, docH/2);
		dgn.getNode().setAttribute(GraphState.ATTRIBUTE_NAME, "First construct");
		return new Model(project, name, graph, drawableGraph);
	}
	
	public Project getProject(){
		return project;
	}
	
	public boolean isSaved() {
		return actionHistory.isMarked();
	}

	public void setSaved() {
		actionHistory.mark();
	}
	
	private String getGatesText(){
		StringBuilder sb = new StringBuilder();
		sb.append("[ ");
		for(int i = 0; i < gates.getGates().size(); i++){
			Gate g = gates.getGates().get(i);
			sb.append(g.getName());
			if(!g.getType().equals("")){
				sb.append(" :: " + g.getType());
			}
			if(!((i+1) == gates.getGates().size()))
				sb.append("; ");
		}
		sb.append(" ]");
		return sb.toString();
	}
	
	private String getStates(){
		String states = "";
		int i = 0;
		for(GraphState s : this.getGraph().getStates()){
			states += s.getName();
			i++;
			if(i < graph.getStates().size()){
				states += ", ";
			}
		}
		return states;
	}
	
	private String getVariablesText(){
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < variables.getVariables().size(); i++){
			Variable v = variables.getVariables().get(i);
			sb.append(v.getName() + " :: " + v.getType());
			if(!((i+1) == variables.getVariables().size()))
				sb.append("; ");
		}
		return sb.toString();
	}
	
	private String getInitVariables(){
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < variables.getVariables().size(); i++){
			Variable v = variables.getVariables().get(i);
			sb.append(v.getName() + " := " + v.getInitValue());
			if(!((i+1) == variables.getVariables().size()))
				sb.append(", ");
		}
		return sb.toString();
	}
	
	private String getTransitions(){
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for(GraphState s : graph.getStates()){
			for(GraphEdge e : s.getOutgoingEdges()){ 
				for(Transition t : e.getTransitions()){
					if(i > 0) sb.append("\t\t");
					sb.append("\t" + e.getFrom().getName() + "\t->\t" + t.getChannel() + "\t" + t.getConditionText() + "\t" + t.getActionText() + "\t->\t" + e.getTo().getName() + "\n");					
					i++;
				}
			}
		}
		return sb.toString();
	}
	
	@Override
	public String getDefinitionAsText() {
		StringBuilder sb = new StringBuilder();
		sb.append("STAUTDEF\t" + this.name + " " + getGatesText() + "()\n");
		sb.append(" ::=\n");
		sb.append("\tSTATE\t" + getStates() + "\n");
		sb.append("\tVAR\t" + getVariablesText() + "\n");
		sb.append("\tINIT\t" + graph.getStartState().getName() + "\t{ " + getInitVariables() + " }\n");
		sb.append("\tTRANS");
		sb.append(getTransitions()+"\n");
		sb.append("ENDDEF");
		return sb.toString();
	}
}
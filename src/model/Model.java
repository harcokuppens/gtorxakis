package model;

import org.w3c.dom.svg.SVGDocument;

import core.Session;
import gui.control.DrawController;
import gui.draw.DrawableGraph;
import gui.draw.DrawableGraphState;
import gui.draw.DrawableGraphEdge;
import gui.draw.GraphInterface;
import model.graph.Graph;
import model.graph.GraphState;
import model.graph.GraphEdge;
import util.DoubleLinkedList;
import action.Action;
import action.DrawCreateAction;
import action.DrawDeleteAction;
import action.ActionHandler;


import java.util.ArrayList;

/**
 * A model holding a graph. 
 */
public class Model extends Definition {
	private String name;
	
	private Graph graph;
	private DrawableGraph drawableGraph;
	private DrawController drawController;
	private GraphInterface graphInterface;
	
	private ActionHandler actionHandler;

	private final Project project;
	
	public Model(Project project, String name, Graph graph, DrawableGraph drawableGraph) {
		this.project = project;
		this.name = name;
		this.graph = graph;
		this.drawableGraph = drawableGraph;
		this.graphInterface = new GraphInterface(this, graph, drawableGraph);
		this.drawController = new DrawController(graphInterface);

		actionHandler = new ActionHandler(this);
		actionHandler.start();
	}

	public Model clone(Project p, String name, Graph graph, DrawableGraph drawableGraph) {
		SVGDocument doc = drawableGraph.getDocument();
		ArrayList<GraphState> clonedNodes = new ArrayList<GraphState>();
		ArrayList<DrawableGraphState> clonedDrawableNodes = new ArrayList<DrawableGraphState>();
		
		for(DrawableGraphState n : this.drawableGraph.getNodes()){
			DrawableGraphState dgn = n.clone(doc);
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



	public Project getProject() {
		return this.project;
	}

	public static Model newModel(Project project, String name) {
		Graph graph = new Graph();
		DrawableGraph drawableGraph = new DrawableGraph();
		SVGDocument doc = drawableGraph.getDocument();
		int docW = Integer.valueOf(doc.getRootElement().getAttribute("width"));
		int docH = Integer.valueOf(doc.getRootElement().getAttribute("height"));
		DrawableGraphState dgn = new DrawableGraphState(doc, docW/2, docH/2);
		dgn.getNode().setAttribute(GraphState.ATTRIBUTE_NAME, "First construct");
//		if(graph.addNode(dgn.getNode())) {
//			Edition.showLimitMessage();
//		}
		return new Model(project, name, graph, drawableGraph);
	}
}
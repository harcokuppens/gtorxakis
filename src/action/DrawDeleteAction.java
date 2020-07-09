package action;

import java.util.ArrayList;
import model.graph.GraphComment;
import model.graph.GraphEdge;
import model.graph.GraphState;
import gui.draw.DrawableComment;
import gui.draw.DrawableGraphEdge;
import gui.draw.DrawableGraphState;
import gui.draw.GraphInterface;
import gui.control.DrawController;

public class DrawDeleteAction extends Action {
	private ArrayList<DrawableGraphState> drawableStates;
	private ArrayList<GraphState> states;
	private ArrayList<DrawableGraphEdge> drawableEdges;
	private ArrayList<GraphEdge> edges;
	private ArrayList<DrawableComment> drawableComments;
	private ArrayList<GraphComment> comments;
	
	public DrawDeleteAction(ArrayList<DrawableGraphState> drawableStateList, ArrayList<DrawableGraphEdge> drawableEdgeList,  ArrayList<DrawableComment> drawableCommentList) {
		super(true);
		drawableStates = new ArrayList<DrawableGraphState>(drawableStateList);
		states = new ArrayList<GraphState>();
		for(DrawableGraphState s: drawableStates) {
			states.add(s.getState());
		}
		drawableEdges = new ArrayList<DrawableGraphEdge>();
		edges = new ArrayList<GraphEdge>();
		for(DrawableGraphEdge e: drawableEdgeList) {
			drawableEdges.add(e);
			edges.add(e.getEdge());
		}
		
		drawableComments = new ArrayList<DrawableComment>(drawableCommentList);
		comments = new ArrayList<GraphComment>();
		for(DrawableComment dc : drawableComments){
			comments.add(dc.getGraphComment());
		}
	}
	
	@Override
	public void run(DrawController dc) {
		GraphInterface gi = dc.getGraphInterface();
		gi.getGraph().removeStates(states);
		gi.getGraph().removeComments(comments);
		for(GraphEdge e: edges) {
			e.getFrom().removeOutgoingEdge(e);
			e.getTo().removeIncomingEdge(e);
		}
		gi.getDrawableGraph().removeNodes(drawableStates);
		gi.getDrawableGraph().removeEdges(drawableEdges);
		gi.getDrawableGraph().removeComments(drawableComments);
		gi.clearSelection();
	}

}

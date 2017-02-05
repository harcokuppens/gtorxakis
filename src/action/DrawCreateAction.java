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

public class DrawCreateAction extends Action {
	private ArrayList<DrawableGraphState> drawableStates;
	private ArrayList<GraphState> states;
	private ArrayList<DrawableGraphEdge> drawableEdges;
	private ArrayList<GraphEdge> edges;
	private ArrayList<DrawableComment> drawableComments;
	private ArrayList<GraphComment> comments;
	
	public DrawCreateAction(ArrayList<DrawableGraphState> drawableNodeList, ArrayList<DrawableGraphEdge> drawableEdgeList, ArrayList<DrawableComment> drawableCommentList) {
		super(true);
		drawableStates = new ArrayList<DrawableGraphState>(drawableNodeList);
		states = new ArrayList<GraphState>();
		for(DrawableGraphState n: drawableStates) {
			states.add(n.getState());
		}
		drawableEdges = new ArrayList<DrawableGraphEdge>(drawableEdgeList);
		edges = new ArrayList<GraphEdge>();
		for(DrawableGraphEdge e: drawableEdges) {
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
		gi.getGraph().addStates(states);
		gi.getGraph().addComments(comments);
		
		for(GraphEdge e: edges) {
			e.getFrom().addOutgoingEdge(e);
			e.getTo().addIncomingEdge(e);
		}
		gi.getDrawableGraph().addStates(drawableStates);
		gi.getDrawableGraph().addEdges(drawableEdges);
		gi.getDrawableGraph().addComments(drawableComments);
	}
}

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
	private ArrayList<DrawableGraphState> drawableNodes;
	private ArrayList<GraphState> nodes;
	private ArrayList<DrawableGraphEdge> drawableEdges;
	private ArrayList<GraphEdge> edges;
	private ArrayList<DrawableComment> drawableComments;
	private ArrayList<GraphComment> comments;
	
	public DrawDeleteAction(ArrayList<DrawableGraphState> drawableNodeList, ArrayList<DrawableGraphEdge> drawableEdgeList,  ArrayList<DrawableComment> drawableCommentList) {
		super(true);
		drawableNodes = new ArrayList<DrawableGraphState>(drawableNodeList);
		nodes = new ArrayList<GraphState>();
		for(DrawableGraphState n: drawableNodes) {
			nodes.add(n.getState());
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
		gi.getGraph().removeStates(nodes);
		gi.getGraph().removeComments(comments);
		for(GraphEdge e: edges) {
			e.getFrom().removeOutgoingEdge(e);
			e.getTo().removeIncomingEdge(e);
		}
		gi.getDrawableGraph().removeNodes(drawableNodes);
		gi.getDrawableGraph().removeEdges(drawableEdges);
		gi.getDrawableGraph().removeComments(drawableComments);
		gi.clearSelection();
	}

}

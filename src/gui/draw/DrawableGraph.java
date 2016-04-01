package gui.draw;

import gui.control.Selectable;
import gui.control.DrawController;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import util.SVGRunnable;

public class DrawableGraph {
//	private GraphInterface gi;
	
	private SVGDocument document;
	private Element definitions;
	
	private DrawableGrid grid;
	private ArrayList<DrawableGraphState> nodes;
	private ArrayList<DrawableGraphEdge> edges;
	private ArrayList<DrawableComment> comments;
	private DrawableSelectionBox selectionBox;
	private Element nodeGroup, edgeGroup, commentGroup;

	private DrawController dc;
	
	public DrawableGraph() {
		nodes = new ArrayList<DrawableGraphState>();
		edges = new ArrayList<DrawableGraphEdge>();
		comments = new ArrayList<DrawableComment>();
		
		SVGFactory factory = new SVGFactory();
		factory.generateDoc();
		document = factory.getDoc();
		definitions = factory.getDefs();
		nodeGroup = factory.getNodeGroup();
		edgeGroup = factory.getEdgeGroup();
		commentGroup = factory.getCommentGroup();
		
		grid = new DrawableGrid(document);
	}

	public void setDrawController(DrawController dc) {
		this.dc = dc;
	}

	/**
	 * Returns a shallow copy of the DrawableGraphNodes in this DrawableGraph.
	 */
	public ArrayList<DrawableGraphState> getNodes() {
		return new ArrayList<DrawableGraphState>(nodes);
	}
	
	/**
	 * Returns a shallow copy of the DrawableGraphEdges in this DrawableGraph.
	 */
	public ArrayList<DrawableGraphEdge> getEdges() {
		return new ArrayList<DrawableGraphEdge>(edges);
	}
	
	//TODO: Finish this function
	@Override
	public DrawableGraph clone() {
		DrawableGraph clone = new DrawableGraph();
		clone.definitions = definitions;
		return clone;
	}
	
	public SVGDocument getDocument(){
		return document;
	}
	
	public Element getDefinitionsElement(){
		return definitions;
	}
	
	public DrawableGrid getGrid() {
		return grid;
	}

	public Element getNodeGroup() {
		return nodeGroup;
	}
	

	public Element getEdgeGroup(){
		return edgeGroup;
	}
	
	public Element getCommentGroup(){
		return commentGroup;
	}
	
	public DrawableSelectionBox getSelectionBox() {
		return selectionBox;
	}
	
	public void setSelectionBox(final DrawableSelectionBox selectionBox) {
		this.selectionBox = selectionBox;
		new SVGRunnable(this.dc, true) {
			@Override
			public void run() {
				document.getRootElement().appendChild(selectionBox.getElement());
			}
		};
	}
	
	public void removeSelectionBox() {
		new SVGRunnable(this.dc, true) {
			@Override
			public void run() {
				// Check if there is a selection box. Return, if not.
				if(selectionBox == null || selectionBox.getElement() == null || selectionBox.getElement().getParentNode() == null) return;
				document.getRootElement().removeChild(selectionBox.getElement());
			}
		};
	}
	
	public void setSelectedElements(final Selectable[] sl,final boolean status){
		new SVGRunnable(this.dc, true){
			@Override
			public void run() {
				for(Selectable s : sl){
					s.setSelected(status);
				}
			}
		};
	}
	
	public void addComments(final ArrayList<DrawableComment> newComments){
		for(DrawableComment n : newComments) {
			comments.add(n);
		}
		
		new SVGRunnable(this.dc, true) {
			@Override
			public void run() {
				for(DrawableComment n : newComments) {
					commentGroup.appendChild(n.getElement());
					n.invalidate();
				}
			}
		};
	}
	
	public void removeComments(final ArrayList<DrawableComment> oldComments){
		for(DrawableComment n : oldComments) {
			comments.remove(n);
		}
		
		new SVGRunnable(this.dc, true) {
			@Override
			public void run() {
				for(DrawableComment n : oldComments) {
					commentGroup.removeChild(n.getElement());
					n.invalidate();
				}
			}
		};
	}
		
	public void addNodes(final ArrayList<DrawableGraphState> newNodes) {
		for(DrawableGraphState n : newNodes) {
			nodes.add(n);
		}
		
		new SVGRunnable(this.dc, true) {
			@Override
			public void run() {
				for(DrawableGraphState n : newNodes) {
					nodeGroup.appendChild(n.getElement());
					n.invalidate();
				}
			}
		};
	}
	
	public void removeNodes(final ArrayList<DrawableGraphState> oldNodes){
		for(DrawableGraphState n : oldNodes) {
			nodes.remove(n);
		}
		
		new SVGRunnable(this.dc, true) {
			@Override
			public void run() {
				for(DrawableGraphState n : oldNodes) {
					nodeGroup.removeChild(n.getElement());
				}
			}
		};
	}
	
	public void addEdges(final ArrayList<DrawableGraphEdge> newEdges) {
		for(DrawableGraphEdge e : newEdges) {
			edges.add(0, e);
		}
		
		new SVGRunnable(this.dc, true) {
			@Override
			public void run() {
				for(DrawableGraphEdge e : newEdges) {
					edgeGroup.appendChild(e.getElement());
				}
			}
		};
	}
	
	public void removeEdges(final ArrayList<DrawableGraphEdge> oldEdges){
		for(DrawableGraphEdge e : oldEdges) {
			edges.remove(e);
		}
		
		new SVGRunnable(this.dc, true) {
			@Override
			public void run() {
				for(DrawableGraphEdge e : oldEdges) {
					edgeGroup.removeChild(e.getElement());
				}
			}
		};
	}

}

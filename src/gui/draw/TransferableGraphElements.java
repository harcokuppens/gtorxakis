package gui.draw;

import gui.control.Selectable;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import model.graph.GraphEdge;

public class TransferableGraphElements implements Transferable /* ,Serializable*/ {
	private ArrayList<DrawableGraphState> nodes;
	private ArrayList<DrawableGraphEdge> edges;
	private ArrayList<DrawableComment> comments;
	private int centerX, centerY;
	
	private final GraphInterface graphInterface;
	
	public static final DataFlavor df = new DataFlavor(TransferableGraphElements.class, "");
	
	public TransferableGraphElements(ArrayList<Selectable> selectedElements, GraphInterface graphInterface) {
		this.graphInterface = graphInterface;
		nodes = new ArrayList<DrawableGraphState>();
		edges = new ArrayList<DrawableGraphEdge>();
		comments = new ArrayList<DrawableComment>();
		
		ArrayList<DrawableGraphState> selectedNodes = new ArrayList<DrawableGraphState>();
		ArrayList<DrawableComment> selectedComments = new ArrayList<DrawableComment>();
		for(Selectable s : selectedElements) {
			if( s instanceof DrawableGraphState){
				DrawableGraphState n = (DrawableGraphState) s;
				selectedNodes.add(n);
			}else if (s instanceof DrawableComment){
				DrawableComment c = (DrawableComment) s;
				selectedComments.add(c);
			}
		}
		for(DrawableComment c : selectedComments){
			DrawableComment dc = new DrawableComment(c);
			comments.add(dc);
		}
		
		
		for(DrawableGraphState d: selectedNodes) {
			DrawableGraphState n = d.clone(); 
			nodes.add(n);
		}
		calculateCenter();
		int o = 0;
		for(DrawableGraphState n: selectedNodes) {
			for(GraphEdge e: n.getNode().getOutgoingEdges()) {
				if(selectedNodes.contains(e.getTo().getDrawable())) {
					int d = selectedNodes.indexOf(e.getTo().getDrawable());
					DrawableGraphState no = nodes.get(o), nd = nodes.get(d);
					DrawableGraphEdge edge = new DrawableGraphEdge(graphInterface.getSVGDocument(), no, nd); 
					edges.add(edge);
				}
			}
			o++;
		}
	}
	
	protected TransferableGraphElements(TransferableGraphElements draft) {
		nodes = new ArrayList<DrawableGraphState>();
		edges = new ArrayList<DrawableGraphEdge>();
		comments = new ArrayList<DrawableComment>();
		for(DrawableGraphState n: draft.nodes) {
			nodes.add(n.clone());
		}
		for(DrawableComment c : draft.comments){
			comments.add(new DrawableComment(c));
		}
		calculateCenter();
		graphInterface = draft.graphInterface;
		for(DrawableGraphEdge e: draft.edges) {
			int o = draft.nodes.indexOf(e.getEdge().getFrom().getDrawable());
			int d = draft.nodes.indexOf(e.getEdge().getTo().getDrawable());
			edges.add(new DrawableGraphEdge(graphInterface.getSVGDocument(), nodes.get(o), nodes.get(d)));
		}
	}
	
	private void calculateCenter() {
		int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, maxX = 0, maxY = 0;
		for(DrawableGraphState n: nodes) {
			int x = (int) n.getLocation().getX();
			int y = (int) n.getLocation().getY();
			if(x < minX) {
				minX = x;
			}
			if(y < minY) {
				minY = y;
			}
			if(x > maxX) {
				maxX = x;
			}
			if(y > maxY) {
				maxY = y;
			}
		}
		for(DrawableComment c: comments) {
			//Position is TOP-LEFT, we have to use the center, thus:
			int x = (int) c.getPosition().getX()+(c.getWidth()/2);
			int y = (int) c.getPosition().getY()+(c.getHeight()/2);
			if(x < minX) {
				minX = x;
			}
			if(x < minY) {
				minY = y;
			}
			if(x> maxX) {
				maxX = x;
			}
			if(y > maxY) {
				maxY = y;
			}
		}
		centerX = (int) ((double) (maxX + minX) / 2d);
		centerY = (int) ((double) (maxY + minY) / 2d);
	}

	@Override
	public Object getTransferData(DataFlavor df)
			throws UnsupportedFlavorException, IOException {
		if(!df.equals(TransferableGraphElements.df)) throw new UnsupportedFlavorException(df);
		else 
			return this;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] {df};
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor df) {
		return df.equals(TransferableGraphElements.df);
	}
	
	public ArrayList<DrawableGraphState> getNodes() {
		return nodes;
	}
	
	public ArrayList<DrawableGraphEdge> getEdges() {
		return edges;
	}
	
	public ArrayList<DrawableComment> getComments(){
		return comments;
	}
	
	public Selectable[] getDrawables() {
		Selectable[] selected = new Selectable[nodes.size()];
		for(int i = 0; i < nodes.size(); i++) {
			selected[i] = nodes.get(i);
		}
		return selected;
	}
	
	public int getCenterX() {
		return centerX;
	}
	
	public int getCenterY() {
		return centerY;
	}
	
	public TransferableGraphElements clone() {
		return new TransferableGraphElements(this);
	}

}

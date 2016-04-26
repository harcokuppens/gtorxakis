package gui.draw;

import gui.control.Movable;
import gui.control.Resizable;
import gui.control.Selectable;
import gui.control.DrawController;
import gui.draw.DrawableComment.CommentType;
import gui.draw.DrawableComment.ResizeType;

import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import model.graph.Graph;
import model.Model;
import model.Project;
import model.graph.GraphComment;
import model.graph.GraphEdge;
import model.graph.GraphState;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import util.Vector;
import action.Action;
import action.DrawCreateAction;
import action.DrawDeleteAction;
import action.DrawMoveAction;
import action.DrawResizeAction;
import core.Session;

/**
 * Handles the interaction between the user, the SVG graph, and the graph model
 * 
 * @author
 * 
 */
public class GraphInterface extends Observable implements ClipboardOwner {
	//Diese Variabele umbedingt auslagern!!! inkl. zugehoerige elemente - Lars
	public static int ARROW_MARKER_SIZE = 5;
	
	private final Model model;
	private final Graph graph;
	private final DrawableGraph drawableGraph;
	private final SVGDocument doc;

	private ArrayList<Selectable> selectedElements;

	public GraphInterface(Model model, Graph graph, DrawableGraph drawableGraph) {
		this.model = model;
		this.graph = graph;
		this.drawableGraph = drawableGraph;
		this.doc = drawableGraph.getDocument();
		selectedElements = new ArrayList<Selectable>();
	}
	
	public void propertiesChanged(){
		setChanged();
		notifyObservers(selectedElements);
	}
	
	public Graph getGraph() {
		return graph;
	}


	public DrawableGraph getDrawableGraph(){
		return drawableGraph;
	}
	
	public SVGDocument getSVGDocument() {
		return doc; 
	}
	
	/*
	 * Selection Management
	 */
	public ArrayList<Selectable> getSelection() {
		return selectedElements;
	}
	
	public ArrayList<DrawableGraphState> getSelectedNodes() {
		ArrayList<DrawableGraphState> nodes = new ArrayList<DrawableGraphState>();
		for(GraphState n: graph.getStates()) {
			if(isSelected(n.getDrawable()))
				nodes.add(n.getDrawable());
		}
		return nodes;
	}
	
	public ArrayList<Movable> getSelectedMovables() {
		ArrayList<Movable> elements = new ArrayList<Movable>();
		for(Selectable s: selectedElements) {
			if(s instanceof Movable) elements.add((Movable) s);
		}
		return elements;
	}
	
	public ArrayList<Movable> getSelectedMovablesToMove() {
		ArrayList<Movable> elements = new ArrayList<Movable>();
		for(Selectable s: selectedElements) {
			if(s instanceof Movable) {
				elements.add((Movable) s);
			}
		}
		return elements;
	}
	
	public Resizable getSelectedCommentToResize(){
		return (Resizable) selectedElements.get(0);
	}
	
	public void addToSelection(Selectable[] sl){
		for(Selectable s: sl) {
			if(!selectedElements.contains(s))
				selectedElements.add(s);
		}
		drawableGraph.setSelectedElements(sl, true);
		setChanged();
		notifyObservers(selectedElements);
	}

	public void addToSelection(Selectable s) {
		addToSelection(new Selectable[] {s});
	}
	
	public void removeFromSelection(Selectable[] sl) {
		for(Selectable s:sl) 
			selectedElements.remove(s);
		drawableGraph.setSelectedElements(sl, false);
		setChanged();
		notifyObservers(selectedElements);
	}

	public void removeFromSelection(Selectable s) {
		removeFromSelection(new Selectable[] {s});
	}

	public void clearSelection() {
		removeFromSelection(selectedElements.toArray(new Selectable[]{}));
	}
	
	public void setSelection(Selectable[] sl) {
		clearSelection();
		addToSelection(sl);
	}
	
	public void setSelection(Selectable s) {
		setSelection(new Selectable[]{s});
	}
	
	public boolean isSelected(Selectable s) {
		return selectedElements.contains(s);
	}
	
	public void selectAll() {
		clearSelection();
		DrawableGraphState[] list = new DrawableGraphState[graph.getStates().size()];
		Iterator<GraphState> i = graph.getStates().iterator();
		for(int n = 0; n < list.length; n++) {
			list[n] = i.next().getDrawable();
		}
		addToSelection(list);
	}
	
	public void selectNextNode() {
		if(graph.getStates().isEmpty()) 
			return;
		else {
			ArrayList<DrawableGraphState> selectedNodes = getSelectedNodes();
			int index = 0;
			if(!selectedNodes.isEmpty()){
				index = graph.getNodeIndex(selectedNodes.get(0).getNode()) + 1;
				clearSelection();
				if(index >= graph.getStates().size()){
					index = 0;
				}
			}
			addToSelection(graph.getStates().get(index).getDrawable());												
		}
	}
		
	/*
	 * Graph Management
	 */
	public void addNode(Point position){
		ArrayList<DrawableGraphState> nodes = new ArrayList<DrawableGraphState>();
		DrawableGraphState dn = new DrawableGraphState(doc, position.x, position.y);
		nodes.add(dn);
		Action a1 = new DrawCreateAction(nodes, new ArrayList<DrawableGraphEdge>(), new ArrayList<DrawableComment>());
		Action a2 = new DrawDeleteAction(nodes, new ArrayList<DrawableGraphEdge>(), new ArrayList<DrawableComment>());
		model.performAction(a1, a2);
	}
	
	public void addNode(int x, int y) {
		ArrayList<DrawableGraphState> nodes = new ArrayList<DrawableGraphState>();
		DrawableGraphState dn = new DrawableGraphState(doc, x, y);
		if(this.getGraph().getStartState() == null){
			dn.getNode().setAttribute(GraphState.ATTRIBUTE_START_STATE, true);
		}
		nodes.add(dn);
		
		Action a1 = new DrawCreateAction(nodes, new ArrayList<DrawableGraphEdge>(), new ArrayList<DrawableComment>());
		Action a2 = new DrawDeleteAction(nodes, new ArrayList<DrawableGraphEdge>(), new ArrayList<DrawableComment>());
		model.performAction(a1, a2);
	}
	
	
	public void addEdge(DrawableGraphState start, DrawableGraphState end) {
//		GraphEdge newEdge = new GraphEdge(start.getNode(), end.getNode(),"");
		ArrayList<DrawableGraphState> nodeList = new ArrayList<DrawableGraphState>();
		ArrayList<DrawableGraphEdge> edgeList = new ArrayList<DrawableGraphEdge>();
//		Point anchorPoint = new Point(end.getPosition().x - start.getPosition().x, end.getPosition().y - start.getPosition().y);
		edgeList.add(new DrawableGraphEdge(doc, start, end));
		ArrayList<DrawableComment> commentList = new ArrayList<DrawableComment>();
		
		Action a1 = new DrawCreateAction(nodeList, edgeList, commentList);
		Action a2 = new DrawDeleteAction(nodeList, edgeList, commentList);
		model.performAction(a1,  a2);
//		if(!graph.isCyclic(start.getNode(), end.getNode())) {
//		}
	}
	
	/**
	 * Places duplicates of the selected objects in the copyCache
	 */
	public void copyEvent(){
		// no need to confirm changes
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable t = new TransferableGraphElements(selectedElements, this);
		clipboard.setContents(t, this);
	}
	
	public void cutEvent() {
		copyEvent();
		deleteEvent();
	}

	public void pasteEvent(int posX, int posY) {
		DataFlavor df = TransferableGraphElements.df;
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		if(!clipboard.isDataFlavorAvailable(df)) return;
		try {
			TransferableGraphElements tg = (TransferableGraphElements) Toolkit.getDefaultToolkit().getSystemClipboard().getData(df);
			clipboard.setContents(tg.clone(), this);
			int dX = posX - tg.getCenterX();
			int dY = posY - tg.getCenterY();

			ArrayList<DrawableGraphState> pasteNodes = new ArrayList<DrawableGraphState>();
			ArrayList<DrawableGraphEdge> pasteEdges = new ArrayList<DrawableGraphEdge>();
			ArrayList<DrawableComment> pasteComments = new ArrayList<DrawableComment>();
			
			for(DrawableGraphState n : tg.getNodes()){
				DrawableGraphState dgn = n.clone(doc);
				int x = Integer.valueOf(n.getAttribute(DrawableGraphState.ATTRIBUTE_POSITION_X)) + dX;
				int y = Integer.valueOf(n.getAttribute(DrawableGraphState.ATTRIBUTE_POSITION_Y)) + dY;
				Point p = DrawableGrid.getPoint(new Point(x, y));
				dgn.setAttribute(DrawableGraphState.ATTRIBUTE_POSITION_X, p.x);
				dgn.setAttribute(DrawableGraphState.ATTRIBUTE_POSITION_Y, p.y);
				pasteNodes.add(dgn);
			}
			
			for(DrawableGraphEdge e: tg.getEdges()) {
				DrawableGraphEdge dge = e.clone(doc);
				pasteEdges.add(dge);
				int index_from = tg.getNodes().indexOf(dge.getEdge().getFrom().getDrawable());
				int index_to = tg.getNodes().indexOf(dge.getEdge().getTo().getDrawable());
				dge.getEdge().setFrom(pasteNodes.get(index_from).getNode());
				dge.getEdge().setTo(pasteNodes.get(index_to).getNode());
				dge.invalidate();
			}
			
			for(DrawableComment c : tg.getComments()){
				DrawableComment dc = new DrawableComment(c);
				int x = Integer.valueOf(dc.getAttribute(DrawableComment.POSITION_X)) + dX;
				int y = Integer.valueOf(dc.getAttribute(DrawableComment.POSITION_Y)) + dY;
				dc.setAttribute(DrawableComment.POSITION_X, x);
				dc.setAttribute(DrawableComment.POSITION_Y, y);
				pasteComments.add(dc);
			}

			Action a1 = new DrawCreateAction(pasteNodes, pasteEdges, pasteComments);
			Action a2 = new DrawDeleteAction(pasteNodes, pasteEdges, pasteComments);
			
			model.performAction(a1, a2);
			clearSelection();
			addToSelection(tg.getDrawables());
			
			setChanged();
			notifyObservers(selectedElements);
			
		} catch (HeadlessException | UnsupportedFlavorException | IOException e) {
			System.err.println("Something went wrong while trying to paste data");
			e.printStackTrace();
		}
	}
		
	public void deleteEvent() {
		if(selectedElements.isEmpty()) return;
		deleteSelectables(selectedElements);
	}


	
	private void deleteSelectables(ArrayList<Selectable> sl){
		ArrayList<DrawableGraphState> nodeList = new ArrayList<DrawableGraphState>();
		ArrayList<DrawableGraphEdge> edgeList = new ArrayList<DrawableGraphEdge>();
		ArrayList<DrawableComment> commentList = new ArrayList<DrawableComment>();
		
		for(Selectable s : sl){
			if(s instanceof DrawableGraphState){
				DrawableGraphState n = (DrawableGraphState)s;
				nodeList.add(n);
				for(GraphEdge e : n.getNode().getOutgoingEdges()){
					if(!edgeList.contains(e.getDrawable())){
						edgeList.add(e.getDrawable());
					}
				}
				for(GraphEdge e : n.getNode().getIncomingEdges()){
					if(!edgeList.contains(e.getDrawable())){
						edgeList.add(e.getDrawable());
					}
				}
			}else if(s instanceof DrawableGraphEdge){
				if(!edgeList.contains((DrawableGraphEdge)s)){
					edgeList.add((DrawableGraphEdge)s);
				}
			}else{
				if(!commentList.contains((DrawableComment)s)){
					commentList.add((DrawableComment)s);
				}
			}

		}
		Action a1 = new DrawDeleteAction(nodeList, edgeList, commentList);
		Action a2 = new DrawCreateAction(nodeList, edgeList, commentList);
		model.performAction(a1, a2);
		
	}

	public void moveElements(int offsetX, int offsetY) {
		Vector v1 = new Vector(offsetX, offsetY), v2 = new Vector(-offsetX, -offsetY); 
		Action a1 = new DrawMoveAction(getSelectedMovablesToMove(), v1);
		Action a2 = new DrawMoveAction(getSelectedMovablesToMove(), v2);
		model.performAction(a1, a2);
	}
	
	public void resizeElement(int offsetX, ResizeType r){
		DrawableComment dc  = (DrawableComment) this.getSelectedCommentToResize();
		int mw = dc.getMinimalWidth();
		int w = dc.getWidth();
		int tmp1 = w-mw;
		if(r.equals(ResizeType.LEFT)){
			int tmp2 = w-offsetX;
			int result = w - tmp1 -tmp2;
			if(w-offsetX < mw){
				offsetX = offsetX - result;
			}
		}else if(r.equals(ResizeType.RIGHT)){
			int tmp2 = w + offsetX;
			int result = w - tmp1 -tmp2;
			if(w+offsetX < mw){
				offsetX = offsetX + result;
			}
		}
		if(offsetX != 0){
			Action a1 = new DrawResizeAction(getSelectedCommentToResize(), offsetX, r);
			Action a2 = new DrawResizeAction(getSelectedCommentToResize(), -offsetX, r);
			model.performAction(a1, a2);
		}
	}

	// End of Graph management

	public void build(DrawController dc) {
		drawableGraph.setDrawController(dc);
		ArrayList<DrawableGraphState> newNodes = new ArrayList<DrawableGraphState>();
		for (GraphState n : graph.getStates()) {
			n.getDrawable().buildElement();
			newNodes.add(n.getDrawable());
		}
		drawableGraph.addNodes(newNodes);
		ArrayList<DrawableGraphEdge> newEdges = new ArrayList<DrawableGraphEdge>();
		for (GraphState n : graph.getStates()) {
			for (GraphEdge e : n.getOutgoingEdges()) {
				e.getDrawable().buildElement();
				newEdges.add(e.getDrawable());
			}
		}
		drawableGraph.addEdges(newEdges);
		ArrayList<DrawableComment> newComments = new ArrayList<DrawableComment>();
		for(GraphComment gc : graph.getComments()){
			newComments.add(gc.getDrawable());
		}
		drawableGraph.addComments(newComments);
	}
	
	public Element getDefsElement() {
		return drawableGraph.getDefinitionsElement();
	}
	
	public Selectable getElementAt(Point point) {
		ArrayList<GraphState> nodes = graph.getStates();
		GraphEdge edge = null;
		//Search from behind to ahead, to get the nodes that are at the front.
//		for(GraphNode n: graph.getNodes()) {
		for(int index = nodes.size()-1; index >= 0; index--){
			GraphState n = nodes.get(index);
			
			Point p = (Point) point.clone();
			DrawableGraphState drawableNode = n.getDrawable();
			if(drawableNode.contains(p)) return drawableNode;
			else {
				for(GraphEdge e: n.getIncomingEdges()) {
					if(e.getDrawable().contains(p)) edge = e;
				}
				// The outgoing edges are already covered by the first loop
				Point drawableNodePosition = drawableNode.getPosition();
				p.translate((int) -drawableNodePosition.getX(), (int) -drawableNodePosition.getY());
			}
		}
		Point p = (Point) point.clone();
		if(edge != null){
			return edge.getDrawable();
		}
		for(GraphComment c: graph.getComments()) {
			if(c.getDrawable().contains(point)) {
				return c.getDrawable();
			}
		}
		return null;
	}
	
	public boolean containsEdgeAt(int x, int y){
		return (getEdgeAt(x,y) != null);
	}
	
	public GraphEdge getEdgeAt (int x, int y){
		for(GraphState n : graph.getStates()){
			for(GraphEdge e : n.getOutgoingEdges()){
				Point start = e.getDrawable().getStartPoint();
				Point end = e.getDrawable().getEndPoint();

				int pointLength = (int) Math.sqrt(Math.pow(x - start.getX(), 2)+ Math.pow(y - start.getY(), 2));
				Point newPoint = new Point(x,y);
				
				double edgeLength = Math.sqrt(Math.pow(start.getX() - end.getX(), 2) + Math.pow(start.getY() - end.getY(), 2));
				
				double angleEdge = Math.tanh((end.getY()-start.getY())/(end.getX()-start.getX()));
				double anglePoint = Math.tanh((newPoint.getY()-start.getY())/(newPoint.getX()-start.getX()));
				double angle = Math.abs(anglePoint - angleEdge);
				System.out.println(angle);
				
				double distanceV =  Math.sin(angle) * pointLength;
				double distanceH = Math.cos(angle) * pointLength;
				System.out.println("vertical Distance : "+distanceV);
				System.out.println("horizontal distance: " + distanceH);
				if(distanceV <= 10 && distanceH < edgeLength && distanceH > 0){
					return e;
				}
			}			
		}
		return null;
	}

	public ArrayList<Selectable> getElementsWithin(Rectangle rect) {
		// To be changed. For now we just care about Nodes and Indicators, but eventually we'll also check edges and comments
		ArrayList<Selectable> nodes = new ArrayList<Selectable>();
		ArrayList<Selectable> indicators = new ArrayList<Selectable>();
		ArrayList<Selectable> comments = new ArrayList<Selectable>();
		for (GraphState node : graph.getStates()) {
			DrawableGraphState n = node.getDrawable();
			if (n.isWithin(rect)) {
				nodes.add(n);
			}
			Rectangle translatedRect = new Rectangle(rect);
			Point drawableNodePosition = n.getPosition();
			translatedRect.translate((int) -drawableNodePosition.getX(), (int) -drawableNodePosition.getY());
		}
		if(!nodes.isEmpty()) {
			return nodes;
		} else if(!indicators.isEmpty()) {
			return indicators;
		} else{
			for(GraphComment comment : graph.getComments()){
				DrawableComment c = comment.getDrawable();
				if(c.isWithin(rect)){
					comments.add(c);
				}
			}
			return comments;
		}
	}

	@Override
	public void lostOwnership(Clipboard arg0, Transferable arg1) {
		// Do nothing
		
	}

	public void addCommentEvent(int x, int y, CommentType commentType) {
		ArrayList<DrawableComment> comments = new ArrayList<DrawableComment>();
		DrawableComment dc = new DrawableComment(doc,x,y, commentType);
		comments.add(dc);
		Action a1 = new DrawCreateAction(new ArrayList<DrawableGraphState>(), new ArrayList<DrawableGraphEdge>(), comments);
		Action a2 = new DrawDeleteAction(new ArrayList<DrawableGraphState>(), new ArrayList<DrawableGraphEdge>(), comments);
		model.performAction(a1, a2);
	}

}
 
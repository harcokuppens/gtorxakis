package gui.control;

import gui.draw.DrawableComment.ResizeType;
import gui.draw.DrawableGraphEdge;
import gui.draw.DrawableGraphState;
import gui.draw.DrawableSelectionBox;
import gui.draw.GraphInterface;
import gui.draw.GraphPanel;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import model.graph.GraphEdge;
import model.graph.GraphState;

import org.apache.batik.swing.gvt.GVTTreeRendererListener;

import util.SVGRunnable;
import util.Vector;
import core.Session;

public class DrawController {
	private boolean isDeployed = false;

	private GraphPanel panel;	
	private GraphInterface gi;
	private ViewBoxController vbc;
	
	private Queue<Runnable> bufferQueue;
	private boolean mouseOnPane = false;
	private Point pastePos;

	private Point dragStart;
	private Point dragEnd;
	private DrawableGraphState dragStartNode;
	private DrawableGraphState dragEndNode;
	private DrawableGraphEdge tempEdge;
	private DrawableSelectionBox drawableSelectionBox;
	
	private EconomicMovementEvent movementEvent;
	private EconomicBoxResizeEvent resizeEvent;
	private EconomicEdgeDragEvent edgeEvent;
	private EconomicResizeEvent commentResizeEvent;
	private GVTTreeRendererListener trl;
	
	public DrawController(GraphInterface gi) {
		this.gi = gi;
		bufferQueue = new LinkedList<Runnable>();
		gi.build(this);
		gi.getDrawableGraph().getGrid().init(gi.getSVGDocument(), gi);
	}
	
	public void attach(GraphPanel p) {
		this.panel = p;
		drawableSelectionBox = new DrawableSelectionBox(this.gi.getSVGDocument());
		
		panel.stopProcessing();
		trl = new TreeRendererListener(this, panel, bufferQueue);
		panel.setGVTTreeRendererListener(trl);
		panel.setDocument(gi.getDrawableGraph().getDocument());
		
		panel.resumeProcessing();

	}

	public void detach() {
		isDeployed = false;
		panel.removeGVTTreeRendererListener(trl);
		panel = null;
	}
	
	public void deploy() {
		vbc = new ViewBoxController(panel, this);
		initGrid();
		pastePos = vbc.getCenterPosition();
		isDeployed = true;
	}
	
	public boolean isDeployed() {
		return isDeployed;
	}
	
	public GraphInterface getGraphInterface() {
		return gi;
	}

	public void setViewGrid(boolean view) {
		gi.getDrawableGraph().getGrid().setVisible(this, view);
	}
	
	public ViewBoxController getViewBoxController() {
		return vbc;
	}

	public double getZoom() {
		return vbc.getZoom();
	}
	
	/*
	 * Panel Management
	 */
	public void setCursor(Cursor cursor) {
		panel.setCursor(cursor);
	}
	
	public void zoomIn() {
		vbc.zoomIn();
	}
	
	public void zoomOut() {
		vbc.zoomOut();
	}
	
	public void zoomReset() {
		vbc.zoomReset();
	}
	
	public void translatePanel(Point p) {
		Point pos = vbc.getClickPosition(p);
		Point start = vbc.getClickPosition(dragStart);
		int transX = (int) (pos.getX() - start.getX());
		int transY = (int) (pos.getY() - start.getY());
		
		vbc.move(transX, transY);
		dragStart = p;
	}
	
	public void invalidateAll() {
		final ArrayList<DrawableGraphState> gns = new ArrayList<DrawableGraphState>();
		final ArrayList<DrawableGraphEdge> ges = new ArrayList<DrawableGraphEdge>();
		for(GraphState gn: gi.getGraph().getStates()) {
			gns.add(gn.getDrawable());
			for(GraphEdge edge : gn.getOutgoingEdges()) {
				ges.add(edge.getDrawable());
			}
			
		}
		new SVGRunnable(this, true) {
			
			@Override
			public void run() {
				for(DrawableGraphState dgn: gns) {
					dgn.update(DrawController.this);
				}
				for(DrawableGraphEdge dge: ges) {
					dge.update(DrawController.this);
				}
			}
		};
	}
	
	public void initGrid() {
		this.setViewGrid(true);
	}
	
	private Point mouseLineAnchor(Point origin, Point mouse) {
		double offset = DrawableGraphEdge.STROKE_WIDTH * GraphInterface.ARROW_MARKER_SIZE;
		double dX = mouse.x - origin.x;
		double dY = mouse.y - origin.y;
		
		double l = Math.sqrt(dX * dX + dY * dY);
		if(offset > l) return origin;
		double ratio = (l - offset) / l;
		dX *= ratio;
		dY *= ratio;
		Point p = new Point((int) (origin.x + dX), (int) (origin.y + dY));
		return p;
	}
	
	public void setDragStart(Point pos) {
		dragStart = pos;
		
		Selectable s = gi.getElementAt(vbc.getClickPosition(dragStart));
		if(s instanceof DrawableGraphState) {
			dragStartNode = (DrawableGraphState) s;
		}else{
			dragStartNode = null;
		}
	}
	
	public void setDragEnd(Point pos) {
		dragEnd = pos;
		
		Selectable s = gi.getElementAt(vbc.getClickPosition(dragEnd));
		if(s instanceof DrawableGraphState){
			dragEndNode = (DrawableGraphState) s;
		}else{
			dragEndNode = null;
		}
	}
	
	public void setPastePosition(Point p) {
		this.pastePos = p;
	}
	
	public boolean getMouseOnPane(){
		return this.mouseOnPane;
	}
	
	public void setMouseOnPane(boolean b){
		this.mouseOnPane = b;
	}
	
	/*
	 * Selection Box
	 */
	
	private class EconomicBoxResizeEvent implements Runnable {
		private int x, y;
		private boolean isExecuting = false;

		public EconomicBoxResizeEvent(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public void updatePos(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public void run() {
			isExecuting = true;
			gi.getDrawableGraph().getSelectionBox().setLRCorner(x, y);
		}
	}
	
	public void setSelectionBox() {
		Point pos = vbc.getClickPosition(dragStart);
		drawableSelectionBox.reinit(gi.getSVGDocument(),(int) pos.getX(), (int) pos.getY());
		gi.getDrawableGraph().setSelectionBox(drawableSelectionBox);
	}

	public void translateSelectionBox(Point p) {
		Point pos = vbc.getClickPosition(p);
		if (resizeEvent == null || resizeEvent.isExecuting) {
			resizeEvent = new EconomicBoxResizeEvent((int) pos.getX(), (int) pos.getY());
			addToQueue(resizeEvent);
		} else {
			resizeEvent.updatePos((int) pos.getX(), (int) pos.getY());
		}
	}

	public void removeSelectionBox(boolean clearSelection) {
		if(clearSelection)gi.clearSelection();
		Point transDragStart = vbc.getClickPosition(dragStart);
		Point transDragEnd = vbc.getClickPosition(dragEnd);
		int ulx = (int) (transDragStart.getX() < transDragEnd.getX() ? transDragStart.getX() : transDragEnd.getX());
		int uly = (int) (transDragStart.getY() < transDragEnd.getY() ? transDragStart.getY() : transDragEnd.getY());
		int w = (int) Math.abs(transDragStart.getX() - transDragEnd.getX());
		int h = (int) Math.abs(transDragStart.getY() - transDragEnd.getY());
		
		gi.addToSelection(gi.getElementsWithin(new Rectangle(ulx, uly, w, h)).toArray(new Selectable[] {}));
		gi.getDrawableGraph().removeSelectionBox();
	}
	
	/*
	 * Temporary Edge
	 */
	
	private class EconomicEdgeDragEvent implements Runnable {
		private int x, y;
		private boolean isExecuting = false;

		public EconomicEdgeDragEvent(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public void updatePos(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public void run() {
			isExecuting = true;
			tempEdge.setEndPoint(new Point(x, y));
		}
	}
	
	public void removeTempEdge() {
		if(dragEndNode != null){
			if (!dragEndNode.equals(dragStartNode) && !dragStartNode.getNode().hasEdgetoNode(dragEndNode.getNode())) {
				gi.addEdge(dragStartNode, dragEndNode);
			}
		}
		ArrayList<DrawableGraphEdge> edgeList = new ArrayList<DrawableGraphEdge>();
		edgeList.add(tempEdge);
		gi.getDrawableGraph().removeEdges(edgeList);
	}
	
	public void setTempEdge() {
		tempEdge = new DrawableGraphEdge(gi.getSVGDocument(), dragStartNode, new Point(vbc.getClickPosition(dragStart)));
		ArrayList<DrawableGraphEdge> edgeList = new ArrayList<DrawableGraphEdge>();
		edgeList.add(tempEdge);
		gi.getDrawableGraph().addEdges(edgeList);
	}
	
	public void translateTempEdge(Point p) {
		Point pos = vbc.getClickPosition(p);
		Point adaptedPos = new Point(pos);
		Selectable s = gi.getElementAt(pos);
		
		if(s != null && s instanceof DrawableGraphState) {
			if(!s.equals(dragStartNode)) {
				DrawableGraphState n = (DrawableGraphState) s;
				adaptedPos = n.getLineAnchor(tempEdge.getStartPoint().getLocation(), DrawableGraphEdge.STROKE_WIDTH);
			}
		} else {
			adaptedPos = mouseLineAnchor(tempEdge.getStartPoint().getLocation(), pos);
		}
		if (edgeEvent == null || edgeEvent.isExecuting) {
			edgeEvent = new EconomicEdgeDragEvent((int) adaptedPos.getX(), (int) adaptedPos.getY());
			addToQueue(edgeEvent);
		} else {
			edgeEvent.updatePos((int) adaptedPos.getX(), (int) adaptedPos.getY());
		}
	}
	
	/*
	 * Graph Manipulations
	 */
	
	private class EconomicMovementEvent implements Runnable {
		private Vector v;
		private boolean isExecuting = false;

		public EconomicMovementEvent(Vector v) {
			this.v = v;
		}

		public void updateOffset(Vector v) {
			this.v = v;
		}

		@Override
		public void run() {
			isExecuting = true;
			for (Movable m : gi.getSelectedMovablesToMove()) {
				m.setOffset(v);
			}
		}
	}
	
	public void translateElement(Point p) {
		Point pos = vbc.getClickPosition(p);
		int dX = (int) (pos.getX() - vbc.getClickPosition(dragStart).getX());
		int dY = (int) (pos.getY() - vbc.getClickPosition(dragStart).getY());
		Vector v = new Vector(dX, dY);
		if (movementEvent == null || movementEvent.isExecuting) {
			movementEvent = new EconomicMovementEvent(v);
			addToQueue(movementEvent);
		} else {
			movementEvent.updateOffset(v);
		}
	}
	
	public void moveElements() {
		int dX = (int) (vbc.getClickPosition(dragEnd).getX() - vbc.getClickPosition(dragStart).getX());
		int dY = (int) (vbc.getClickPosition(dragEnd).getY() - vbc.getClickPosition(dragStart).getY());
		gi.moveElements(dX, dY);
	}

	/**
	 * Class to resize one comment
	 * @author Tobias
	 *
	 */
	
	private class EconomicResizeEvent implements Runnable {
		private Vector v;
		private ResizeType resizeType;
		private boolean isExecuting = false;

		public EconomicResizeEvent(Vector v, ResizeType r) {
			this.v = v;
			this.resizeType = r;
		}

		public void updateOffset(Vector v) {
			this.v = v;
		}

		@Override
		public void run() {
			isExecuting = true;
			Resizable r = gi.getSelectedCommentToResize();
			r.setResizeOffset(v, resizeType);
		}
	}
	
	public void translateComment(Point p, ResizeType r) {
		Point pos = vbc.getClickPosition(p);
		int dX = (int) (pos.getX() - vbc.getClickPosition(dragStart).getX());
		int dY = (int) (pos.getY() - vbc.getClickPosition(dragStart).getY());
		Vector v = new Vector(dX, dY);
		if (commentResizeEvent == null || commentResizeEvent.isExecuting) {
			commentResizeEvent = new EconomicResizeEvent(v, r);
			addToQueue(commentResizeEvent);
		} else {
			commentResizeEvent.updateOffset(v);
		}
	}
	
	public void resizeElement(ResizeType r) {
		int dX = (int) (vbc.getClickPosition(dragEnd).getX() - vbc.getClickPosition(dragStart).getX());
		gi.resizeElement(dX, r);		
	}

	/**
	 * Adds a runnable to the rendering queue of the SVG panel and 
	 * returns.
	 * The runnable is invoked on a separate thread as soon as possible.
	 * @param r The runnable to be executed
	 */
	public synchronized void addToQueue(Runnable r) {
		if(!isDeployed) {
			bufferQueue.add(r);
			System.out.println("[GraphInterface] buffering runnable in queue. Items in queue: " + bufferQueue.size());
		} else {
			try {
				panel.getUpdateManager()
						.getUpdateRunnableQueue().invokeLater(r);	
			} catch(NullPointerException | IllegalStateException e) {
				bufferQueue.add(r);
				System.out.println("[GraphInterface] buffering runnable in queue. Items in queue: " + bufferQueue.size());
			} 
		}
	}
	
	public Point getPastePos() {
		return pastePos;
	}


	
}

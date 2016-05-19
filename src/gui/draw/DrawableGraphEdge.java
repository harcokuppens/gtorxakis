package gui.draw;

import gui.control.Movable;
import gui.control.Selectable;

import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import model.graph.GraphComment;
import model.graph.GraphEdge;
import util.Vector;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import core.Session;

/**
 * 
 * @author Tobias Schröter
 *
 */
public class DrawableGraphEdge extends DrawableElement implements Drawable, Selectable, Movable{
	private static final int EL_INDEX_SHAPE = 0;
	private static final int EL_INDEX_COMMENT = 1;
//	private static final int EL_INDEX_RESULT = 1;
//	private static final int EL_INDEX_BACKGROUND = 2;
//	private static final int EL_INDEX_NAME = 3;
	private static final int EL_INDEX_ANCHOR = 4;
	
	public static final int STROKE_WIDTH = 2,
							TEXT_SIZE = 14;
	public static final String TEXT_WEIGHT = "bold",
							   TEXT_FAMILY = "arial",
							   TEXT_ANCHOR = "middle",
							   TEXT_COLOR = "black",
							   BACKGROUND_ANCHOR = "center",
							   BACKGROUND_COLOR = "white";
	
	private static final int MARGIN = ((STROKE_WIDTH - 1) * GraphInterface.ARROW_MARKER_SIZE) / 2,
							 RESULT_MARGIN = 3;
	
	private Element element;
	private Element[] edge;
	private Point[] anchorPoints;
	private Point anchorVector;
	private int offsetX = 0, offsetY = 0;
	private boolean selected = false;
	private GraphEdge graphEdge;
	
	private DrawableComment transitionComment;

	private int width = 0,
				height = 0;
	
	public DrawableGraphEdge(SVGDocument doc, DrawableGraphState from, DrawableGraphState to) {
		super(doc);
		anchorPoints = new Point[3];
		anchorVector = new Point(0,0);
		graphEdge = new GraphEdge(from.getNode(), to.getNode());
		Point lineAnchor = to.getLineAnchor(from.getLocation(),DrawableGraphEdge.STROKE_WIDTH);
		Point comment = new Point((from.getPosition().x + lineAnchor.x)/2, (from.getPosition().y + lineAnchor.y)/2);
		transitionComment = new DrawableComment(doc, comment.x, comment.y, DrawableComment.CommentType.COMMENT,this.graphEdge);
		graphEdge.setDrawable(this);
		setStart(from.getPosition());
		setEnd(lineAnchor);
		buildElement();			
		if(from.equals(to)){
			anchorPoints[1] = new Point(from.getPosition().x, from.getPosition().y-500);
		}
	}
	
	/**
	 * Only for temp edges!
	 * @param doc
	 * @param from
	 * @param to
	 */
	public DrawableGraphEdge(SVGDocument doc, DrawableGraphState from, Point to) {
		super(doc);
//		transitionComment = new DrawableComment(doc, from.getPosition().x + 50, from.getPosition().y, DrawableComment.CommentType.COMMENT);
		anchorPoints = new Point[3];
		anchorVector = new Point(0,0);
		graphEdge = new GraphEdge(from.getNode(), null);
		graphEdge.setDrawable(this);
		setStart(from.getPosition());
		setEnd(to);
		buildElement();
	}
	
	/**
	 * Copy constructor
	 * @param draft The DrawableGraphEdge to be duplicated
	 */
	protected DrawableGraphEdge(SVGDocument doc, DrawableGraphEdge draft) {
		super(doc);
		this.graphEdge = draft.graphEdge.clone();
		graphEdge.setDrawable(this);
		buildElement();
	}
	
	public DrawableComment getComment(){
		return transitionComment;
	}
	
	public GraphEdge getEdge(){
		return graphEdge;
	}
	
	public Point getStartPoint(){
		return anchorPoints[0];
	}
	
	public Point getEndPoint(){
		return anchorPoints[anchorPoints.length-1];
	}
	
	@Override
	public Element getElement() {
		return element;
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public void setStartPoint(Point start) {
		anchorPoints[0] = start;
		invalidatePositions(true);
	}
	
	public void setEndPoint(Point end) {
		anchorPoints[anchorPoints.length-1] = end;
		invalidatePositions(true);
	}
	
	private void setStart(Point start){
		anchorPoints[0] = start;
	}
	
	private void setEnd(Point end){
		anchorPoints[anchorPoints.length-1] = end;
	}
	
	private void setAnchor(boolean finalMove){
		Point start = getStartPoint(), end = getEndPoint();
		int x = (start.x + end.x) / 2;
		int y = (start.y + end.y) / 2;
		Point anchorPoint;

		if(finalMove){
			anchorPoint = new Point(x + anchorVector.x, y + anchorVector.y);
		}else{
			anchorPoint = new Point(x + anchorVector.x + offsetX, y + anchorVector.y + offsetY);
		}
		
		anchorPoints[1] = anchorPoint;
		edge[EL_INDEX_ANCHOR].setAttribute("cx", String.valueOf(anchorPoints[1].x));
		edge[EL_INDEX_ANCHOR].setAttribute("cy", String.valueOf(anchorPoints[1].y));
	}

	@Override
	public void setSelected(boolean b) {
		setSelected(b, true);
	}
	
	/**
	 * Sets the edge selected or not-selected.
	 * @param selected - true if the edge is selected, otherwise false.
	 * @param comment - true if the comment should also be setSelected
	 */
	public void setSelected(boolean selected, boolean comment){
		this.selected = selected;
		if(selected){
			edge[EL_INDEX_SHAPE].setAttribute("stroke", String.valueOf("red"));
			edge[EL_INDEX_SHAPE].setAttribute("marker-end", "url(#pf1red)");
			edge[EL_INDEX_ANCHOR].setAttribute("fill", "red");
		}else{
			edge[EL_INDEX_SHAPE].setAttribute("stroke", String.valueOf("black"));
			edge[EL_INDEX_SHAPE].setAttribute("marker-end", "url(#pf1)");
			edge[EL_INDEX_ANCHOR].setAttribute("fill", "none");
		}
		if(transitionComment != null && comment)transitionComment.setSelected(selected);
	}
	
	protected void buildElement() {
		element = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "g");
		edge = new Element[5];
		edge[EL_INDEX_SHAPE] = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "polyline");
		edge[EL_INDEX_ANCHOR] = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "ellipse");
		if(transitionComment != null){
			element.appendChild(this.transitionComment.getElement());
		}
		element.appendChild(edge[EL_INDEX_SHAPE]);
		element.appendChild(edge[EL_INDEX_ANCHOR]);
		invalidate();
	}
	
	/**
	 * Calculates the positions of the edge.
	 */
	private void calculatePositions(boolean finalMove){
		if(graphEdge.getFrom() != null) {
			Point2D dest; 
			if(graphEdge.getTo() != null){
				dest = graphEdge.getTo().getDrawable().getLocation(); 
			}else{
				dest = this.getEndPoint();
			}
			setStart(graphEdge.getFrom().getDrawable().getLineAnchor(dest));
		}
		if(graphEdge.getTo() != null) {
			Point2D dest; 
			if(graphEdge.getFrom() != null){
				dest = graphEdge.getFrom().getDrawable().getLocation(); 				
			}else{
				dest = this.getStartPoint();
			}
			setAnchor(finalMove);
			setEnd(graphEdge.getTo().getDrawable().getLineAnchor(anchorPoints[1], DrawableGraphEdge.STROKE_WIDTH));
		}
	}
	
	/**
	 * The positions will be (re-)calculated and set.
	 */
	
	
	
	public void invalidatePositions(boolean finalMove){
		calculatePositions(finalMove);
		String transitionText = this.getEdge().getTransitionText();
		if(transitionComment != null) transitionComment.setAttribute(DrawableComment.TEXT, transitionText);
		
		StringBuilder lineAnchorPoints = new StringBuilder();
		for (Point p : anchorPoints) {
			if(p != null){
				lineAnchorPoints.append(p.getX());
				lineAnchorPoints.append(",");
				lineAnchorPoints.append(p.getY());
				lineAnchorPoints.append(" ");
			}
		}	
		edge[EL_INDEX_SHAPE].setAttribute("points", lineAnchorPoints.toString());
		
		Point2D p1 = graphEdge.getFrom().getDrawable().getLocation();
		Point2D p2;
		if(graphEdge.getTo() != null){
			p2 = graphEdge.getTo().getDrawable().getLocation();			
		}else{
			//TempEdge!
			p2 = getEndPoint();
		}
	}
	
	@Override
	public void invalidate() {
		invalidatePositions(true);
	
		edge[EL_INDEX_ANCHOR].setAttribute("rx", "4");
		edge[EL_INDEX_ANCHOR].setAttribute("ry", "4");

		setSelected(selected);
		
		edge[EL_INDEX_SHAPE].setAttribute("stroke-width", String.valueOf(STROKE_WIDTH));
		edge[EL_INDEX_SHAPE].setAttribute("fill", "none");
		edge[EL_INDEX_SHAPE].setAttribute("stroke", "black");
	}
	
	@Override
	public DrawableGraphEdge clone() {
		return clone(doc);
	}
	
	/**
	 * Clones this DrawableGraphEdge so that it can be used in the
	 * provided document.
	 * @param doc The SVGDocument that this element will be part of
	 * @return A clone of this DrawableGraphEdge
	 */
	public DrawableGraphEdge clone(SVGDocument doc) {
		return new DrawableGraphEdge(doc, this);
	}

	@Override
	public String getAttribute(String cmd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isWithin(Rectangle r) {
		Point start = this.getStartPoint(), end = this.getEndPoint();
		int dx = start.x - end.x;
		int dy = start.y - end.y;
		Point p = new Point(start.x + dx, start.y + dy);
		
		return r.contains(p);
	}

	@Override
	public boolean contains(Point p) {
		ArrayList<Point> usedPoints = new ArrayList<Point>();
		for(Point point : anchorPoints){
			if(point != null){
				usedPoints.add(point);
			}
		}
		for(int i = 1; i < usedPoints.size(); i++){
			if(contains(usedPoints.get(i-1), usedPoints.get(i), p)){
				return true;
			}
		}
		return false;
	}
	
	private boolean contains(Point start, Point end, Point p){
		double p0x = start.getX(),
			   p0y = start.getY(),
			   px = p.getX(),
			   py = p.getY(),
			   p2x = end.getX(),
			   p2y = end.getY();
		

		double h = Math.sqrt(Math.pow(p2y-p0y, 2) + Math.pow(p2x-p0x, 2));
		double h2 = h + DrawableGraphEdge.STROKE_WIDTH * GraphInterface.ARROW_MARKER_SIZE;
		
		double p3x = p0x + (h2 * (p2x-p0x)) / h;
		double p3y = p0y + (h2 * (p2y-p0y)) / h;
		
		double l1 = Math.sqrt(Math.pow(py-p0y, 2) + Math.pow(px-p0x, 2));
		double l2 = Math.sqrt(Math.pow(py-p3y, 2) + Math.pow(px-p3x, 2));
		
		double alpha, beta, gamma;
		beta = Math.atan((py-p0y)/(px-p0x));
		gamma = Math.atan((p3y-p0y)/(p3x-p0x));
		alpha = beta - gamma;
		double dw = Math.sin(alpha) * l1;
		double dh = Math.cos(alpha) * l1;
		if(Math.abs(dw) > MARGIN + (STROKE_WIDTH / 2)) return false;
		if(dh < 0 || dh > h2 || l1 > h2 || l2 > h2) return false;
		return true;
	}
	
	@Override
	public void moveTo(Point p) {
		// not used
	}

	@Override
	public void moveBy(Vector v) {
		anchorVector.translate(v.getX(), v.getY());		
		offsetX = 0;
		offsetY = 0;
//		System.err.println(anchorVector.x + ", " + anchorVector.y);
		invalidatePositions(true);
		transitionComment.moveBy(v);
	}

	@Override
	public void setOffset(Vector v) {
//		System.out.println(v.getX() + ", " + v.getY());
		offsetX = v.getX();
		offsetY = v.getY();
		invalidatePositions(false);
		transitionComment.setOffset(v);
	}

}

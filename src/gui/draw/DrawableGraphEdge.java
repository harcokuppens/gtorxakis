package gui.draw;

import gui.control.Movable;
import gui.control.Selectable;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import model.Transition;
import model.graph.GraphEdge;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import util.Vector;


/**
 * 
 * @author Tobias
 */

public class DrawableGraphEdge extends DrawableElement implements Drawable, Selectable, Movable{
	private static final int EL_INDEX_SHAPE = 0;
	private static final int EL_INDEX_ANCHOR = 4;
	
	public static final int STROKE_WIDTH = 2,
							TEXT_SIZE = 14;
	
	public static final String TEXT_WEIGHT = "bold",
							   TEXT_FAMILY = "arial",
							   TEXT_ANCHOR = "middle",
							   TEXT_COLOR = "black",
							   BACKGROUND_ANCHOR = "center",
							   BACKGROUND_COLOR = "white";
	
	private static final int MARGIN = ((STROKE_WIDTH - 1) * GraphInterface.ARROW_MARKER_SIZE) / 2;
	
	private enum EdgeType{
		NORMAL,
		SAME_ROOT;
	}
	private EdgeType edgeType = EdgeType.NORMAL;
	private Element element;
	private Element[] edge;
	protected Point start,
					end,
					anchorPoint;
	private int offsetX = 0, offsetY = 0;
	private boolean selected = false;
	private GraphEdge graphEdge;
	
	private DrawableComment transitionComment;
	private DrawableGraphState from,
							   to;
	
	public DrawableGraphEdge(SVGDocument doc, DrawableGraphState from, DrawableGraphState to) {
		super(doc);
		this.from = from;
		this.to = to;
		System.err.println("Create edge");
		graphEdge = new GraphEdge(from.getState(), to.getState());
		Point lineAnchor = to.getLineAnchor(from.getLocation(),DrawableGraphEdge.STROKE_WIDTH);
		Point comment = new Point((from.getPosition().x + lineAnchor.x)/2, (from.getPosition().y + lineAnchor.y)/2);
		transitionComment = new DrawableComment(doc, comment.x, comment.y, DrawableComment.CommentType.COMMENT,this.graphEdge);
		graphEdge.setDrawable(this);
		setStart(from.getPosition());
		setEnd(lineAnchor);
		anchorPoint = this.getHalfPoint();
		if(from.equals(to)){
			edgeType = EdgeType.SAME_ROOT;
		}else{
			edgeType = EdgeType.NORMAL;
		}
		System.out.println("Constructor anchorPoint = " + anchorPoint);
		buildElement();			
	}
	
	/**
	 * Only for temp edges!
	 * @param doc
	 * @param from
	 * @param to
	 */
	public DrawableGraphEdge(SVGDocument doc, DrawableGraphState from, Point to) {
		super(doc);
		this.from = from;
		System.err.println("Create temp edge");
		graphEdge = new GraphEdge(from.getState(), null);
		graphEdge.setDrawable(this);
		if(from.contains(to)){
			edgeType = EdgeType.SAME_ROOT;
		}else{
			edgeType = EdgeType.NORMAL;
		}
		setStart(from.getPosition());
		end = to;
		buildElement();
	}
	
	/**
	 * Copy constructor
	 * @param draft The DrawableGraphEdge to be duplicated
	 */
	protected DrawableGraphEdge(SVGDocument doc, DrawableGraphEdge draft) {
		super(doc);
		this.graphEdge = draft.graphEdge.clone();
		this.edgeType = draft.edgeType;
		this.from = draft.from;
		this.to = draft.to;
		this.anchorPoint = draft.anchorPoint;
		graphEdge.setDrawable(this);
		buildElement();
	}
	
	public DrawableGraphEdge(SVGDocument svgDoc, DrawableGraphState from, DrawableGraphState to, Point anchorPoint,
			ArrayList<Transition> transitions, Point commentPosition, int commentWidth) {
		super(svgDoc);
		this.from = from;
		this.to = to;
		this.anchorPoint = anchorPoint;
		System.out.println("Create edge with commentPosition");
		graphEdge = new GraphEdge(from.getState(), to.getState(), transitions);
		Point lineAnchor = to.getLineAnchor(from.getLocation(),DrawableGraphEdge.STROKE_WIDTH);
		transitionComment = new DrawableComment(doc, commentPosition.x, commentPosition.y, commentWidth, DrawableComment.CommentType.COMMENT, new String[]{this.getEdge().getTransitionText()}, graphEdge);
		graphEdge.setDrawable(this);
		start = from.getPosition();
		end = lineAnchor;
		if(from.equals(to)){
			edgeType = EdgeType.SAME_ROOT;
		}else{
			edgeType = EdgeType.NORMAL;
		}
		buildElement();			
	}

	public DrawableComment getComment(){
		return transitionComment;
	}
	
	public GraphEdge getEdge(){
		return graphEdge;
	}
	
	public Point getStartPoint(){
		return start;
	}
	
	public Point getEndPoint(){
		return end;
	}

	public Point getAnchorPoint(){
		return anchorPoint;
	}
	
	@Override
	public Element getElement() {
		return element;
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public void setEndPoint(Point end) {
		if(from.contains(end)){
			this.edgeType = EdgeType.SAME_ROOT;
		}else{
			this.edgeType = EdgeType.NORMAL;
		}
		this.end = end;
		invalidatePositions();
	}
	
	private void setStart(Point start){
		this.start = start;
	}
	
	private void setEnd(Point end){
		this.end = end;
	}
	
	
	private Point getHalfPoint(){
		int x = (start.x + end.x) / 2;
		int y = (start.y + end.y) / 2;
		return new Point(x,y);
	}
	
	public boolean anchorOnHalf(){
		return (anchorPoint.x == getHalfPoint().x) && (anchorPoint.y == getHalfPoint().y);
	}
	
	private Point[] getAnchorPointsForContains(){
		return new Point[]{start,anchorPoint,end};
	}
	
	private ArrayList<Point> getAnchorPoints(){
		ArrayList<Point> points = new ArrayList<Point>();
		points.add(start);
		if(anchorPoint == null || to == null){
			points.add(end);
		}else if(offsetX  != 0 || offsetY != 0){
			Point p = new Point(anchorPoint);
			p.translate(offsetX, offsetY);
			p = DrawableGrid.getPoint(p, true);
			points.add(p);
			edge[EL_INDEX_ANCHOR].setAttribute("cx", String.valueOf(p.x));
			edge[EL_INDEX_ANCHOR].setAttribute("cy", String.valueOf(p.y));
			transitionComment.setOffset(new Vector(offsetX,offsetY));
			points.add(to.getLineAnchor(p, DrawableGraphEdge.STROKE_WIDTH));
		}else{
			Point p;
			if(anchorOnHalf()){
				System.out.println("Anchor " + anchorPoint);
				System.out.println("Half " + this.getHalfPoint());
				p = start;
			}else{
				points.add(anchorPoint);
				p = anchorPoint;
			}
			edge[EL_INDEX_ANCHOR].setAttribute("cx", String.valueOf(anchorPoint.x));
			edge[EL_INDEX_ANCHOR].setAttribute("cy", String.valueOf(anchorPoint.y));
			System.out.println(to.getState().getName());
			points.add(to.getLineAnchor(p, DrawableGraphEdge.STROKE_WIDTH));
		}
		System.out.println("Size of points: " + points.size());
		return points;
	}
	
	private Point[] getSameRootPoints(){
		int size = DrawableGrid.GRID_SIZE + DrawableGrid.GRID_SIZE/2;
		Point temp1 = new Point(start.x + size, start.y);
		Point temp2 = new Point(temp1.x, temp1.y - size);
		Point temp3 = new Point(start.x, temp2.y);
		Point end = from.getLineAnchor(temp3, DrawableGraphEdge.STROKE_WIDTH);
		return new Point[]{start, temp1, temp2,temp3, end};
	}
	
	private void setAnchor(Point vector){
		anchorPoint.translate(vector.x, vector.y);
		anchorPoint = DrawableGrid.getPoint(anchorPoint, true);
		edge[EL_INDEX_ANCHOR].setAttribute("cx", String.valueOf(anchorPoint.x));
		edge[EL_INDEX_ANCHOR].setAttribute("cy", String.valueOf(anchorPoint.y));
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
	private void calculatePositions(){
		boolean isSame = anchorPoint == null || anchorOnHalf();
		start = graphEdge.getFrom().getDrawable().getLineAnchor(anchorPoint);
		if(graphEdge.getTo() != null) {
			end = graphEdge.getTo().getDrawable().getLineAnchor(anchorPoint, DrawableGraphEdge.STROKE_WIDTH);
		}
		if(isSame){
			anchorPoint = this.getHalfPoint();
		}
		
	}
	
	/**
	 * The positions will be (re-)calculated and set.
	 */
	public void invalidatePositions(){
		calculatePositions();
		String transitionText = this.getEdge().getTransitionText();
		if(transitionComment != null) transitionComment.setAttribute(DrawableComment.TEXT, transitionText);
		
		StringBuilder lineAnchorPoints = new StringBuilder();
		if(edgeType.equals(EdgeType.NORMAL)){
			int i = 0;
			ArrayList<Point> points = this.getAnchorPoints();
			for (Point p : points) {
					lineAnchorPoints.append(p.getX());
					lineAnchorPoints.append(",");
					lineAnchorPoints.append(p.getY());
					lineAnchorPoints.append(" ");
				i++;
			}	
		}else{
			for (Point p : this.getSameRootPoints()) {
				if(p != null){
					lineAnchorPoints.append(p.getX());
					lineAnchorPoints.append(",");
					lineAnchorPoints.append(p.getY());
					lineAnchorPoints.append(" ");
				}
			}
		}
		edge[EL_INDEX_SHAPE].setAttribute("points", lineAnchorPoints.toString());
	}
	
	@Override
	public void invalidate() {
		invalidatePositions();
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
		return null;
	}

	@Override
	public boolean isWithin(Rectangle r) {
		return r.contains(anchorPoint);
	}

	@Override
	public boolean contains(Point p) {
		ArrayList<Point> usedPoints = new ArrayList<Point>();
		if(edgeType.equals(EdgeType.NORMAL)){
			for(Point point : this.getAnchorPointsForContains()){
				if(point != null){
					usedPoints.add(point);
				}
			}
			for(int i = 1; i < usedPoints.size(); i++){
				if(contains(usedPoints.get(i-1), usedPoints.get(i), p)){
					return true;
				}
			}
		}else{
			Point points[] = this.getSameRootPoints();
			for(int i = 1; i < points.length; i++){
				if(contains(points[i-1], points[i], p)){
					return true;
				}
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
	}
	
	@Override
	public void moveBy(Vector v) {
		if(edgeType.equals(EdgeType.NORMAL)){
			offsetX = 0;
			offsetY = 0;
			setAnchor(new Point(v.getX(), v.getY()));
			invalidatePositions();
		}
		transitionComment.moveBy(v);
	}

	@Override
	public void setOffset(Vector v) {
		System.out.println(anchorPoint.toString());
		if(edgeType.equals(EdgeType.NORMAL)){
			offsetX = v.getX();
			offsetY = v.getY();
			invalidatePositions();
		}
		transitionComment.setOffset(v);
	}
	
	public Rectangle getBoundingBox() {
		int startX, startY, width, height;
		
		Point anchorPoint = (this.anchorPoint != null)?this.anchorPoint:getHalfPoint();
		
		if(start.y > anchorPoint.y){
			if(anchorPoint.y > end.y){
				startY = end.y;
			}else{
				startY = anchorPoint.y;
			}
		}else{
			if(start.y > end.y){
				startY = end.y;
			}else{
				startY = start.y;
			}
		}
		if(start.x > anchorPoint.x){
			if(anchorPoint.x > end.x){
				startX = end.x;
			}else{
				startX = anchorPoint.x;
			}
		}else{
			if(start.x > end.x){
				startX = end.x;
			}else{
				startX = start.x;
			}
		}
		
		if(Math.abs(startX - start.x) < Math.abs(startX - anchorPoint.x)){
			if(Math.abs(startX - anchorPoint.x) < Math.abs(startX - end.x)){
				width = Math.abs(startX - end.x);
			}else{
				width = Math.abs(startX - anchorPoint.x);
			}
		}else{
			if(Math.abs(startX - start.x) < Math.abs(startX - end.x)){
				width = Math.abs(startX - end.x);
			}else{
				width = Math.abs(startX - start.x);
			}
		}
		
		if(Math.abs(startY - start.y) < Math.abs(startY - anchorPoint.y)){
			if(Math.abs(startY - anchorPoint.y) < Math.abs(startY - end.y)){
				height = Math.abs(startY - end.y);
			}else{
				height = Math.abs(startY - anchorPoint.y);
			}
		}else{
			if(Math.abs(startY - start.y) < Math.abs(startY - end.y)){
				height = Math.abs(startY - end.y);
			}else{
				height = Math.abs(startY - start.y);
			}
		}
		return new Rectangle (startX, startY, width, height);
	}

}

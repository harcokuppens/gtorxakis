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
	private static final int EL_INDEX_RESULT = 1;
	private static final int EL_INDEX_BACKGROUND = 2;
	private static final int EL_INDEX_NAME = 3;
	
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
//	TODO change result to constraint
	private boolean hasResult = false,
					hasName = false;
	private String result = "",
				   name = "";
	private int width = 0,
				height = 0;
	
	public DrawableGraphEdge(SVGDocument doc, DrawableGraphState from, DrawableGraphState to) {
		super(doc);
		anchorPoints = new Point[3];
		anchorVector = new Point(0,0);
		graphEdge = new GraphEdge(from.getNode(), to.getNode(), "");
		graphEdge.setDrawable(this);
		setStart(from.getPosition());
		setEnd(to.getPosition());
		buildElement();
	}
	
	public DrawableGraphEdge(SVGDocument doc, DrawableGraphState from, Point to) {
		super(doc);
		anchorPoints = new Point[3];
		anchorVector = new Point(0,0);
		graphEdge = new GraphEdge(from.getNode(), null, "");
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
		if(finalMove){
			anchorPoints[1] = DrawableGrid.getPoint(new Point(x + anchorVector.x, y + anchorVector.y));
		}else{
			anchorPoints[1] = DrawableGrid.getPoint(new Point(x + anchorVector.x + offsetX, y + anchorVector.y + offsetY));
		}
	}

	/**
	 * Sets the edge selected or not-selected.
	 * @param selected - true if the edge is selected, otherwise false.
	 */
	public void setSelected(boolean selected){
		this.selected = selected;
		if(selected){
			edge[EL_INDEX_SHAPE].setAttribute("stroke", String.valueOf("red"));
			edge[EL_INDEX_SHAPE].setAttribute("marker-end", "url(#pf1red)");
		}else{
			edge[EL_INDEX_SHAPE].setAttribute("stroke", String.valueOf("black"));
			edge[EL_INDEX_SHAPE].setAttribute("marker-end", "url(#pf1)");
		}
	}
	
	protected void buildElement() {
		element = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "g");
		edge = new Element[4];
		edge[EL_INDEX_SHAPE] = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "polyline");
		edge[EL_INDEX_RESULT] = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "text");
		edge[EL_INDEX_BACKGROUND] = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "rect");
		edge[EL_INDEX_NAME] = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "text");
		element.appendChild(edge[EL_INDEX_SHAPE]);
		element.appendChild(edge[EL_INDEX_BACKGROUND]);
		element.appendChild(edge[EL_INDEX_RESULT]);
		element.appendChild(edge[EL_INDEX_NAME]);
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
//			setEnd(graphEdge.getTo().getDrawable().getLineAnchor(dest, DrawableGraphEdge.STROKE_WIDTH));
			setAnchor(finalMove);
			
			setEnd(graphEdge.getTo().getDrawable().getLineAnchor(anchorPoints[1], DrawableGraphEdge.STROKE_WIDTH));
		}
	}
	
	/**
	 * The positions will be (re-)calculated and set.
	 */
	public void invalidatePositions(boolean finalMove){
		calculatePositions(finalMove);
		
//		Point start = this.getStartPoint(), end = this.getEndPoint();
//		
//		edge[EL_INDEX_SHAPE].setAttribute("x1", String.valueOf(start.x));
//		edge[EL_INDEX_SHAPE].setAttribute("y1", String.valueOf(start.y));
//		edge[EL_INDEX_SHAPE].setAttribute("x2", String.valueOf(end.x));
//		edge[EL_INDEX_SHAPE].setAttribute("y2", String.valueOf(end.y));
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

		Point p = this.calculateStringSize(name, result);
		width = p.x;
		height = p.y;

		double x = (p1.getX()+p2.getX())/2;
		double y = (p1.getY()+p2.getY())/2-(height/2);
		
		edge[EL_INDEX_NAME].setAttribute("x", String.valueOf(x + RESULT_MARGIN));
		edge[EL_INDEX_NAME].setAttribute("y", String.valueOf(y + (hasResult?height/2:height) - RESULT_MARGIN));
		
		edge[EL_INDEX_RESULT].setAttribute("x", String.valueOf(x + RESULT_MARGIN));
		edge[EL_INDEX_RESULT].setAttribute("y", String.valueOf(y + height - RESULT_MARGIN));
	
		edge[EL_INDEX_BACKGROUND].setAttribute("x", String.valueOf(x-(width/2)));
		edge[EL_INDEX_BACKGROUND].setAttribute("y", String.valueOf(y));
		
		edge[EL_INDEX_BACKGROUND].setAttribute("width", String.valueOf(width + 2 * RESULT_MARGIN));
		edge[EL_INDEX_BACKGROUND].setAttribute("height", String.valueOf(height));
	}
	
	private void invalidateResultBackground(){
		//Background
		if(hasResult() || graphEdge.hasName()){
			edge[EL_INDEX_BACKGROUND].setAttribute("fill-opacity", String.valueOf(1.0));			
		}else{
			edge[EL_INDEX_BACKGROUND].setAttribute("fill-opacity", String.valueOf(0.0));	
		}
	}
	
	@Override
	public void invalidate() {
		name = (String) graphEdge.getAttribute(GraphEdge.ATTRIBUTE_NAME);
		
		invalidatePositions(true);
		
		setSelected(selected);
		
		edge[EL_INDEX_SHAPE].setAttribute("stroke-width", String.valueOf(STROKE_WIDTH));
		edge[EL_INDEX_SHAPE].setAttribute("fill", "none");
		edge[EL_INDEX_SHAPE].setAttribute("stroke", "black");
		
		edge[EL_INDEX_NAME].setAttribute("font-size", String.valueOf(TEXT_SIZE));
		edge[EL_INDEX_NAME].setAttribute("text-anchor", TEXT_ANCHOR);
		edge[EL_INDEX_NAME].setAttribute("font-weight", TEXT_WEIGHT);
		edge[EL_INDEX_NAME].setAttribute("font-family", TEXT_FAMILY);
		edge[EL_INDEX_NAME].setAttribute("fill", TEXT_COLOR);
		edge[EL_INDEX_NAME].setTextContent(name);
		
		edge[EL_INDEX_RESULT].setAttribute("font-size", String.valueOf(TEXT_SIZE));
		edge[EL_INDEX_RESULT].setAttribute("text-anchor", TEXT_ANCHOR);
		edge[EL_INDEX_RESULT].setAttribute("font-weight", TEXT_WEIGHT);
		edge[EL_INDEX_RESULT].setAttribute("font-family", TEXT_FAMILY);
		edge[EL_INDEX_RESULT].setAttribute("fill", TEXT_COLOR);
		
		edge[EL_INDEX_BACKGROUND].setAttribute("anchor", BACKGROUND_ANCHOR);
		edge[EL_INDEX_BACKGROUND].setAttribute("fill", BACKGROUND_COLOR);
		
		invalidateResultBackground();
	}
	
	private Point calculateStringSize(String name, String result){
		Point nameSize = calculateStringSize(name);
		Point resultSize = calculateStringSize(result);
		int width = (nameSize.x>resultSize.x)?nameSize.x:resultSize.x;
		int height = nameSize.y + resultSize.y;
		return new Point(width, height);
	}
	
	private Point calculateStringSize (String text){
		if(text.equals("")) return new Point(0,0);
		AffineTransform affinetransform = new AffineTransform();     
		FontRenderContext frc = new FontRenderContext(affinetransform,true,true);     
		Font font = new Font(TEXT_FAMILY, Font.BOLD, TEXT_SIZE);
		int textWidth = (int)(font.getStringBounds(text, frc).getWidth());
		int textHeight = (int)(font.getStringBounds(text, frc).getHeight());
		return new Point(textWidth, textHeight);
	}
	
	public boolean hasResult(){
		return hasResult;
	}
	
	/**
	 * Sets the name of the edge and invalidates the positions and the background of the result/name!
	 * @param name - new name of the edge!
	 */
	public void setName(String name){
		hasName = true;
		this.name = name;
		edge[EL_INDEX_NAME].setTextContent(name);
		invalidateResultBackground();
		invalidatePositions(true);
	}
	
	/**
	 * Clears the result.
	 */
	public void clearResult() {
		hasResult = false;
		result = "";
		edge[EL_INDEX_RESULT].setTextContent("");
		invalidateResultBackground();
		invalidatePositions(true);
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
	}

	@Override
	public void setOffset(Vector v) {
//		System.out.println(v.getX() + ", " + v.getY());
		offsetX = v.getX();
		offsetY = v.getY();
		invalidatePositions(false);
	}
}

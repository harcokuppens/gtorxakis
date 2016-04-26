package gui.draw;

import gui.control.Movable;
import gui.control.DrawController;
import gui.control.Selectable;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import model.graph.GraphEdge;
import model.graph.GraphState;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import util.Vector;
import action.Configurable;
import core.Session;

public class DrawableGraphState extends DrawableElement implements Configurable, Selectable, Movable {
	private enum Elements {
		WRAPPER, GROUP, ELLIPSE, START_STATE, TEXT;
	}
	
	public static final String ATTRIBUTE_POSITION_X = "xPosition";
	public static final String ATTRIBUTE_POSITION_Y = "yPosition";
	public static final String ATTRIBUTE_VIEW_INDICATORS = "viewIndicators";

	private Point position;
	private Element textTSpanElement, valueTSpanElement, superScriptElement;
	private int offsetX, offsetY;
	private Element[] elements;
    private boolean selected = false; 
	
    private boolean viewIndicators = true;
    
	private GraphState node;

	public static final int WIDTH = 60; 
	public static final int HEIGHT = 60; 
	private static final String COLOR = "#FFDF00"; 
	private static final String STROKE_COLOR = "black";
	private static final int STROKE_WIDTH = 1;
	private static final String STROKE_COLOR_SELECTED = "red"; 
	private static final int TEXT_SIZE = 11; 
	private static final String TEXT_FAMILY = "Arial"; 
	private static final String TEXT_WEIGHT = "bold"; 
	private static final String TEXT_COLOR = "black"; 
	
	
	public DrawableGraphState(SVGDocument doc, int posX, int posY) {
		super(doc);
		this.node = new GraphState();
		this.node.setDrawable(this);

		
		this.position = DrawableGrid.getPoint(new Point(posX, posY));
		buildElement();
	}

	public DrawableGraphState(SVGDocument doc, int posX, int posY, String name) {
		super(doc);
		this.node = new GraphState(name);
		this.node.setDrawable(this);
		this.viewIndicators = viewIndicators;
		
		this.position = DrawableGrid.getPoint(new Point(posX, posY));
		
		buildElement();
	}

	/**
	 * Copy constructor
	 * 
	 * @param draft
	 *            The DrawableGraphNode to be duplicated
	 */
	protected DrawableGraphState(SVGDocument doc, DrawableGraphState draft) {
		super(doc);
		this.node = new GraphState(draft.getNode());
		this.node.setDrawable(this);
		
		
		this.position = (Point) (draft.getPosition().clone());
		buildElement();
	}
	
	public GraphState getNode() {
		return this.node;
	}

	public Point getPosition() {
		return this.position;
	}
	
	public Point2D getLocation() {
		return new Point((int) this.position.getX() + offsetX, (int) this.position.getY() + offsetY);
	}
	
	/**
	 * Calculates the anchor point at the ellipse dependent on the position of another point.
	 * @param d another point
	 * @return the calculated anchor point
	 */
	public Point getLineAnchor(Point2D d) {
		return new Point(position.x + offsetX, position.y + offsetY);
	}
	
	/**
	 * Calculates the anchor point at the ellipse dependent on the position of another point. See the documentation for a graphical representation of the several variables.
	 * @param d another point
	 * @param arrowSize the size of the extra spacing between the ellipse and the anchor points (useful for arrows)
	 * @return the calculated anchor point
	 */
	public Point getLineAnchor(Point2D d, int arrowSize) {
		double dx = d.getX() - (this.position.getX() + offsetX);
		double dy = d.getY() - (this.position.getY() + offsetY);
		double alpha = Math.atan(dy/dx);
		double a = ((double) DrawableGraphState.WIDTH) / 2.0;
		double b = ((double) DrawableGraphState.HEIGHT) / 2.0;
		double r = (a*b)/Math.sqrt(
				Math.pow(a * Math.sin(alpha), 2)+Math.pow(b * Math.cos(alpha), 2)
				);
		
		// Add some extra spacing between the ellipse and the anchor
		r += GraphInterface.ARROW_MARKER_SIZE * arrowSize + 1;
		
		double rToV;
		double distance = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
		if(distance == 0.0) {
			rToV = 0;
		} else {
			rToV = r / distance;
		}
		return new Point((int) (this.position.getX() + offsetX + dx * rToV), (int) (this.position.getY() + offsetY + dy * rToV));
	}

	private void updateEdges() {
		for (GraphEdge edge : this.node.getIncomingEdges()) {
			edge.getDrawable().invalidate();
		}
		for (GraphEdge edge : this.node.getOutgoingEdges()) {
			edge.getDrawable().invalidate();
		}
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
		if (this.isSelected()) {
			elements[Elements.ELLIPSE.ordinal()].setAttribute("stroke", STROKE_COLOR_SELECTED);
		} else {
			elements[Elements.ELLIPSE.ordinal()].setAttribute("stroke", STROKE_COLOR);
		}
		this.invalidate();
	}

	public boolean isSelected() {
		return this.selected;
	}

	public void invalidate() {
		// Set group properties
		elements[Elements.WRAPPER.ordinal()].setAttribute("transform", "translate(" + (int) this.getPosition().getX() + ", " + (int) this.getPosition().getY() + ")");
		elements[Elements.GROUP.ordinal()].setAttribute("render-order", "-1");
		
		//Set start_state properties
		boolean isStartState = (boolean) this.getNode().getAttribute(GraphState.ATTRIBUTE_START_STATE);
		if(isStartState){
			elements[Elements.START_STATE.ordinal()].setAttribute("x1", "-50");
			elements[Elements.START_STATE.ordinal()].setAttribute("y1", "-50");
			Point p = this.getLineAnchor(new Point(-50 + position.x, -50 + position.y), 2);
			System.err.println("Position:"+this.position.toString());
			System.err.println("LineStart:"+p.toString());
			elements[Elements.START_STATE.ordinal()].setAttribute("x2", (p.x-position.x)+"");
			elements[Elements.START_STATE.ordinal()].setAttribute("y2", (p.y-position.y)+"");
			elements[Elements.START_STATE.ordinal()].setAttribute("stroke-width", "2");
			elements[Elements.START_STATE.ordinal()].setAttribute("fill", "none");
			if(selected){
				elements[Elements.START_STATE.ordinal()].setAttribute("stroke", String.valueOf("red"));
				elements[Elements.START_STATE.ordinal()].setAttribute("marker-end", "url(#pf1red)");
			}else{
				elements[Elements.START_STATE.ordinal()].setAttribute("stroke", String.valueOf("black"));
				elements[Elements.START_STATE.ordinal()].setAttribute("marker-end", "url(#pf1)");
			}
		}else{
			elements[Elements.START_STATE.ordinal()].setAttribute("stroke", "none");
			elements[Elements.START_STATE.ordinal()].removeAttribute("marker-end");
		}
		
		// Set ellipse properties
		elements[Elements.ELLIPSE.ordinal()].setAttribute("cx", "0");
		elements[Elements.ELLIPSE.ordinal()].setAttribute("cy", "0");
		elements[Elements.ELLIPSE.ordinal()].setAttribute("rx", String.valueOf(WIDTH / 2));
		elements[Elements.ELLIPSE.ordinal()].setAttribute("ry", String.valueOf(HEIGHT / 2));
		elements[Elements.ELLIPSE.ordinal()].setAttribute("fill", COLOR);
		elements[Elements.ELLIPSE.ordinal()].setAttribute("stroke-width", String.valueOf(STROKE_WIDTH));

		// Set text properties
		elements[Elements.TEXT.ordinal()].setAttribute("text-anchor", "middle");
		elements[Elements.TEXT.ordinal()].setAttribute("x", "0");
		elements[Elements.TEXT.ordinal()].setAttribute("y", String.valueOf(TEXT_SIZE / 2));
		elements[Elements.TEXT.ordinal()].setAttribute("font-size", String.valueOf(TEXT_SIZE));
		elements[Elements.TEXT.ordinal()].setAttribute("font-weight", TEXT_WEIGHT);
		elements[Elements.TEXT.ordinal()].setAttribute("font-family", TEXT_FAMILY);
		elements[Elements.TEXT.ordinal()].setAttribute("fill", TEXT_COLOR);
		elements[Elements.TEXT.ordinal()].setTextContent(this.node.getName());
	}

	protected void buildElement() {
		this.elements = new Element[Elements.values().length];
		
		textTSpanElement = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "tspan");
		superScriptElement = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "tspan");
		superScriptElement.setAttribute("baseline-shift", "super");
		superScriptElement.setAttribute("font-size", String.valueOf(TEXT_SIZE - 3));
		valueTSpanElement = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "tspan");
		
		elements[Elements.WRAPPER.ordinal()] = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "g");
		elements[Elements.GROUP.ordinal()] = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "g");
		elements[Elements.ELLIPSE.ordinal()] = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "ellipse");
		elements[Elements.START_STATE.ordinal()] = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
		elements[Elements.TEXT.ordinal()] = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "text");

		elements[Elements.GROUP.ordinal()].appendChild(elements[Elements.ELLIPSE.ordinal()]);
		elements[Elements.GROUP.ordinal()].appendChild(elements[Elements.TEXT.ordinal()]);
		elements[Elements.GROUP.ordinal()].appendChild(elements[Elements.START_STATE.ordinal()]);		
		elements[Elements.WRAPPER.ordinal()].appendChild(elements[Elements.GROUP.ordinal()]);
		invalidate();
		setSelected(false);
	}

	public Element getElement() {
		return elements[Elements.WRAPPER.ordinal()];
	}

	@Override
	public boolean isWithin(Rectangle r) {
		return r.contains(this.getPosition());
	}

	
	@Override
	public boolean contains(Point p) {
		return Math.pow(((double) (p.getX() - this.getPosition().getX())) / ((double) (WIDTH / 2)), 2)
				+ Math.pow(((double) (p.getY() - this.getPosition().getY())) / ((double) (HEIGHT / 2)),
						2) <= 1;
	}
	
	public boolean contains(Point p, boolean relative) {
		if(relative) {
			return Math.pow((double) p.getX() / (double) (WIDTH / 2), 2)
					+ Math.pow((double) p.getY() / (double) (HEIGHT / 2),
							2) <= 1;
		} else {
			return this.contains(p);
		}
	}

	@Override
	public String getAttribute(String cmd) {
		switch(cmd) {
		case ATTRIBUTE_POSITION_X:
			return String.valueOf(getPosition().x);
		case ATTRIBUTE_POSITION_Y:
			return String.valueOf(getPosition().y);
		case ATTRIBUTE_VIEW_INDICATORS:
			return String.valueOf(viewIndicators);
		default:
			System.err.println("Tried to get non existing attribute of DrawableGraphNode (" + cmd + ")!");
			return null;
		}
	}
	
	@Override
	public void setAttribute(String cmd, Object value) {
		switch(cmd) {
		case ATTRIBUTE_POSITION_X:
			this.position.setLocation((Integer) value, this.position.getY());
			break;
		case ATTRIBUTE_POSITION_Y:
			this.position.setLocation(this.position.getX(), (Integer) value);
			break;
		case ATTRIBUTE_VIEW_INDICATORS:
			this.viewIndicators = (Boolean.valueOf((String) value));
			break;
		default:
			System.err.println("Tried to set non existing attribute of DrawableGraphNode (" + cmd + ")!");
		}
	}

	@Override
	public void setOffset(Vector v) {
		this.offsetX = v.getX();
		this.offsetY = v.getY();
		updateLocation();
	}

	private void updateLocation() {
		int x = (int) this.position.getX() + offsetX;
		int y = (int) this.position.getY() + offsetY;
		Point p = DrawableGrid.getPoint(new Point(x, y));
		//adjust offsets:
		offsetX = p.x - position.x;
		offsetY = p.y - position.y;
		
		elements[Elements.WRAPPER.ordinal()].setAttribute("transform", "translate(" + p.x + ", " + p.y + ")");
		updateEdges();
	}

	@Override
	public DrawableGraphState clone() {
		return clone(doc);
	}
	
	/**
	 * Clones this DrawableGraphNode so that it can be used in the
	 * provided document.
	 * @param doc The SVGDocument that this element will be part of
	 * @return A clone of this DrawableGraphEdge
	 */
	public DrawableGraphState clone(SVGDocument doc) {
		return new DrawableGraphState(doc, this);
	}

	@Override
	public void moveBy(Vector v) {
		this.position.translate(v.getX(), v.getY());
		this.position = DrawableGrid.getPoint(this.position);
		offsetX = 0;
		offsetY = 0;
		updateLocation();
	}
	
	@Override
	public void moveTo(Point p) {
		moveBy(new Vector(p.x - position.x, p.y - position.y));
	}
	
	public Rectangle getBoundingBox(boolean includeIndicators) {
		Rectangle boundingBox = new Rectangle(new Point((int) (this.position.getX() - (DrawableGraphState.WIDTH / 2)), (int) (this.position.getY() - (DrawableGraphState.HEIGHT / 2))), new Dimension(DrawableGraphState.WIDTH, DrawableGraphState.HEIGHT));
		return boundingBox;
	}

	@Override
	public void updateConfigs(DrawController dc) {
		this.update(dc);
	}
}

package gui.draw;

import java.awt.Point;
import java.awt.Rectangle;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import util.SVGRunnable;
import gui.control.DrawController;

public class DrawableGrid extends DrawableElement{
	private Element element;
	
	private static final int POINT_SIZE = 2;
	private static final int POINT_MARGIN = 40;
	private static final String COLOR_DARK = "dimgrey";
	private static final String COLOR_LIGHT = "lightgrey";
	public static final int GRID_SIZE = POINT_SIZE + POINT_MARGIN;
	
	public DrawableGrid(SVGDocument doc) {
		super(doc);
	}
	
	public void init(Document doc, GraphInterface gi) {
		buildDefs(doc, gi);
		buildElement(doc);
	}
	
	private void buildDefs(Document doc, GraphInterface gi) {
		//Grid Pattern
		Element gridPattern = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "pattern");
		gridPattern.setAttribute("id", "gridPattern");
		gridPattern.setAttribute("x", String.valueOf(-POINT_SIZE/2));
		gridPattern.setAttribute("y", String.valueOf(-POINT_SIZE/2));
		gridPattern.setAttribute("width", String.valueOf(GraphPanel.width + GRID_SIZE));
		gridPattern.setAttribute("height", String.valueOf(GRID_SIZE));
		gridPattern.setAttribute("patternUnits", "userSpaceOnUse");

		Element line1 = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
		line1.setAttribute("x1", String.valueOf(POINT_SIZE/2));
		line1.setAttribute("y1", String.valueOf(POINT_SIZE/2));
		line1.setAttribute("x2", String.valueOf(GraphPanel.width + GRID_SIZE));
		line1.setAttribute("y2", String.valueOf(POINT_SIZE/2));
		line1.setAttribute("stroke", COLOR_DARK);
		line1.setAttribute("stroke-width", String.valueOf(POINT_SIZE));
		line1.setAttribute("stroke-dasharray", String.valueOf(POINT_SIZE)+", "+String.valueOf(POINT_MARGIN));

		Element line2 = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
		line2.setAttribute("x1", String.valueOf(POINT_SIZE + POINT_MARGIN/2));
		line2.setAttribute("y1", String.valueOf(POINT_SIZE/2));
		line2.setAttribute("x2", String.valueOf(GraphPanel.width + GRID_SIZE));
		line2.setAttribute("y2", String.valueOf(POINT_SIZE/2));
		line2.setAttribute("stroke", COLOR_LIGHT);
		line2.setAttribute("stroke-width", String.valueOf(POINT_SIZE));
		line2.setAttribute("stroke-dasharray", String.valueOf(POINT_SIZE)+", "+String.valueOf(POINT_MARGIN));

		Element line3 = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
		line3.setAttribute("x1", String.valueOf(POINT_SIZE + POINT_MARGIN/2));
		line3.setAttribute("y1", String.valueOf(POINT_SIZE + POINT_MARGIN/2));
		line3.setAttribute("x2", String.valueOf(GraphPanel.width + GRID_SIZE));
		line3.setAttribute("y2", String.valueOf(POINT_SIZE + POINT_MARGIN/2));
		line3.setAttribute("stroke", COLOR_LIGHT);
		line3.setAttribute("stroke-width", String.valueOf(POINT_SIZE));
		line3.setAttribute("stroke-dasharray", String.valueOf(POINT_SIZE)+", "+String.valueOf((POINT_MARGIN-POINT_SIZE)/2));

		gridPattern.appendChild(line1);
		gridPattern.appendChild(line2);
		gridPattern.appendChild(line3);
		
		gi.getDefsElement().appendChild(gridPattern);
	}
	
	private void buildElement(Document doc) {
		element = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "rect");
		element.setAttribute("x", "0");
		element.setAttribute("y", "0");
		element.setAttribute("width", String.valueOf(GraphPanel.width));
		element.setAttribute("height", String.valueOf(GraphPanel.height));
		element.setAttribute("fill", "url(#gridPattern)");
	}
	
	public Element getElement() {
		return element;
	}
	
	public void setVisible(DrawController dc, boolean visible) {
		GraphInterface gi = dc.getGraphInterface();
		final GraphInterface giF = gi;
		if (visible && element.getParentNode() == null) {
			new SVGRunnable(dc, true) {
				@Override
				public void run() {
					doc.getRootElement()
						.insertBefore(element, 
								giF
								.getDefsElement()
								.getNextSibling());
				}
			};
		} else if (!visible && element.getParentNode() != null) {
			new SVGRunnable(dc, true) {
				@Override
				public void run() {
					doc.getRootElement().removeChild(element);
				}
			};
		}
	}
	
	public static Point getPoint(Point p){
		return getPoint(p, false);
	}
	
	/**
	 * Calculates a point on the grid, belonging to a point possibly not on the grid
	 * @param p a point
	 * @return a the closest point on the grid
	 */
	public static Point getPoint(Point p, boolean half) {
		int modX,
			modY;
		if(!half){
			modX = (int) (p.getX() % DrawableGrid.GRID_SIZE);
			modY = (int) (p.getY() % DrawableGrid.GRID_SIZE);			
		}else{
			modX = (int) (p.getX() % (DrawableGrid.GRID_SIZE/2));
			modY = (int) (p.getY() % (DrawableGrid.GRID_SIZE/2));
		}
		return new Point((int) (p.getX() + (modX < (DrawableGrid.GRID_SIZE / 2) ? -modX : DrawableGrid.GRID_SIZE - modX)), (int) (p.getY() + (modY < (DrawableGrid.GRID_SIZE / 2) ? -modY : DrawableGrid.GRID_SIZE - modY)));
	}
	
	public static Point getPoint(Point p, Rectangle restrictedArea) {
		Point point = getPoint(p,false);
		if(restrictedArea.contains(point)) {
			Point nearest = calculateNearestPointOnRect(p, restrictedArea);
			point = getPoint(nearest,false);
		}
		return point;
	}
	
	private static Point calculateNearestPointOnRect(Point p, Rectangle r) {
		Point rectUL = new Point((int) r.getX(), (int) r.getY());
		Point rectLR = (Point) rectUL.clone();
		rectLR.translate((int) r.getWidth(), (int) r.getHeight());
		
		double dxUL = p.getX() - rectUL.getX();
		double dyUL = p.getY() - rectUL.getY();
		double dxLR = rectLR.getX() - p.getX();
		double dyLR = rectLR.getY() - p.getY();
		
		double minX = Math.min(dxUL, dxLR);
		double minY = Math.min(dyUL, dyLR);
		double min = Math.min(minX, minY);
		
		if(min == dxUL) {
			return new Point((int) rectUL.getX(), (int) p.getY());
		} else if (min == dyUL) {
			return new Point((int) p.getX(), (int) rectUL.getY());
		} else if(min == dxLR) {
			return new Point((int) rectLR.getX(), (int) p.getY());
		} else {
			return new Point((int) p.getX(), (int) rectLR.getY());
		}
	}

	@Override
	protected void invalidate() {
		// TODO Auto-generated method stub
		
	}
}

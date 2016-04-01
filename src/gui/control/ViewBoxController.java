package gui.control;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.concurrent.Semaphore;

import gui.draw.GraphPanel;
import gui.control.DrawController;

import org.w3c.dom.svg.SVGDocument;

public class ViewBoxController {
	private SVGDocument d;
	private GraphPanel panel;
	private DrawController dc;
	private int docW, docH, frameW, frameH;
	private double fr, dr;
	private ViewBoxManipulator vbm;
	private Rectangle viewBox;
	
	private int MINWIDTH = 40;
	// MINHEIGHT will be deduced from the graphPanel w/h ratio
	private int MINHEIGHT;
	private int MAXWIDTH, MAXHEIGHT;

	private final double zoomFactor = 1.1;
	
	public ViewBoxController(GraphPanel panel, DrawController dc) {
		this.d = panel.getSVGDocument();
		this.panel = panel;
		this.dc = dc;
		docW = Integer.valueOf(d.getRootElement().getAttribute("width"));
		docH = Integer.valueOf(d.getRootElement().getAttribute("height"));
		recalibrate();
		
		vbm = new ViewBoxManipulator();
		viewBox = fetchViewBox(d);
		vbm.setAttributes(viewBox.x, viewBox.y, viewBox.width, viewBox.height);
		// System.out.println("[ViewBoxController] Doc dimensions: " + docW + " " + docH);
		// System.out.println("[ViewBoxController] Frame dimensions: " + frameW + " " + frameH);
		focus(docW/2, docH/2);
		
	}
	
	public Point getCenterPosition(){
		return new Point(viewBox.x + viewBox.width/2, viewBox.y + viewBox.height/2);
	}
	
	private void recalibrate() {
		// System.out.println("[ViewBoxController] recalibrating...");
		frameW = panel.getWidth();
		frameH = panel.getHeight();
		fr = (double) frameW / (double) frameH;
		dr = (double) docW / (double) docH;
		MINHEIGHT = (int) ((double) MINWIDTH / fr);
		
		if(dr > fr){
			MAXWIDTH = (int) (docH * fr);
			MAXHEIGHT = docH; 
		} else {
			MAXWIDTH = docW;
			MAXHEIGHT = (int) ((double)docW / fr);
		}
		
		AffineTransform rt = new AffineTransform();
		double scale;
		double dx, dy;
		if(fr < dr) {
			scale = (double) frameH / (double) docH;
			dx = -(double) docW * (dr - fr) / (2d*dr);
			dy = 0;
		} else {
			scale = (double) frameW / (double) docW;
			dx = 0;
			dy = -(double) docH * (fr - dr) / (2d*fr);
		}
		rt.scale(scale, scale);
		rt.translate(dx, dy);
		// System.out.println("[ViewBoxController] applying RenderingTransform: " + rt);
		panel.setRenderingTransform(rt);
		panel.setRecenterOnResize(true);
	}

	private int getBaseWidth() {
		return frameW;
	}

	private int getBaseHeight() {
		return frameH;
	}
	
	public Point getClickPosition(Point p) {
		AffineTransform at = panel.getViewBoxTransform();
		Point2D p2;
		try {
			p2 = at.inverseTransform(p, null);
		} catch (NoninvertibleTransformException e) {
			System.err.println("[ViewBoxController] Severe: Unable to inverse viewBox transform");
			e.printStackTrace();
			return null;
		}
//		System.out.println(p2);
		return new Point((int) p2.getX(), (int) p2.getY());
	}
	
	private Rectangle fetchViewBox(SVGDocument d) {
		try {
			String[] s = d.getRootElement().getAttribute("viewBox").split(" ");			
			int[] attributes = new int[4];
			for(int i = 0; i < 4; i++) {
				attributes[i] = Integer.valueOf(s[i]);
			}
			Rectangle r = new Rectangle(attributes[0], attributes[1], getBaseWidth(), getBaseHeight());
//			r.height = (int) ((double) r.width * aspectRatio);
			// System.out.println("[ViewBoxController] found viewBox: " + r);
			return r;
		} catch(NullPointerException | NumberFormatException e) {
			System.err.println("[ViewBoxController] Severe: The provided document does not have a viewbox attribute");
			return null;
		}
	}
	
	/**
	 * Zooms in, so that the new part of the graph is focused on the provided coordinates
	 * @param p The point that the view is centered on after zooming in
	 */
	public void zoomIn() {
		int width = (int) ((double) viewBox.width / zoomFactor);
		int height = (int) ((double) viewBox.height / zoomFactor);
		if(width < MINWIDTH || height < MINHEIGHT) return; 
		int x = viewBox.x + (viewBox.width - width) / 2;
		int y = viewBox.y + (viewBox.height - height) / 2;
		vbm.setAttributes(x, y, width, height);
	}
	
	public void zoomOut() {
		int width = (int) ((double) viewBox.width * zoomFactor);
		int height = (int) ((double) viewBox.height * zoomFactor);
		if(width > MAXWIDTH|| height > MAXHEIGHT) return;
		int x = viewBox.x + (viewBox.width - width) / 2;
		int y = viewBox.y + (viewBox.height - height) / 2;
		vbm.setAttributes(x, y, width, height);
	}
	
	public void zoomReset() {
		int width = getBaseWidth();
		int height = getBaseHeight();
		int x = viewBox.x + (viewBox.width - width) / 2;
		int y = viewBox.y + (viewBox.height - height) / 2;
		vbm.setAttributes(x, y, width, height);	
	}
	
	public double getZoom() {
		return (double) frameW / (double) viewBox.width; 
	}
	
	
	/**
	 * Moves the viewbox by a given vector
	 * @param dx difference in x coordinate in graph panel coordinates
	 * @param dy difference in y coordinate in graph panel coordinates
	 */
	public void move(int dx, int dy) {
		vbm.setPos(viewBox.x - dx, viewBox.y - dy);
		
	}
	
	public void focus(Point p) {
		Point p2 = getClickPosition(p);
		focus(p2.x, p2.y);
	}
	
	
	
	/**
	 * Focuses the viewBox on a certain point in the graph
	 * @param x the x coordinate of the central point in graph coordinates
	 * @param y the y coordinate of the central point in graph coordinates
	 */
	private void focus(int x, int y) {
		// System.out.println("Center at " + x + " " + y);
		int posx = x - (viewBox.width/2);
		int posy = y - (viewBox.height/2);
//		System.out.println("Focussing on " + posx + " " + posy);
		vbm.setPos(posx, posy);
	}
	

	
	private class ViewBoxManipulator {
		private EconomicViewBoxEvent vbe;
		
		/**
		 * Translates the current values of the viewBox to a string 
		 * as used in the SVG document
		 * @return a string of the form "x y w h" where the variables x, y, w and h are 
		 * filled by the position and dimension of the viewBox
		 */
		private String toSVGAttribute(Rectangle r) {
			StringBuilder sb = new StringBuilder();
			sb.append(String.valueOf(r.x) + " ");
			sb.append(String.valueOf(r.y) + " ");
			sb.append(String.valueOf(r.width) + " ");
			sb.append(String.valueOf(r.height));
			return sb.toString();
		}
		
		public void invalidate() {
			setAttributes(viewBox.x, viewBox.y, viewBox.width, viewBox.height);
		}
		
		public void setPos(int x, int y) {
			setAttributes(x, y, viewBox.width, viewBox.height);
		}
		
		public void setSize(int w, int h) {
			setAttributes(viewBox.x, viewBox.y, w, h);
		}
		
		public void setAttributes(int x, int y, int w, int h) {
//			System.out.println("entering: " + x +" "+ y +" "+ w +" "+ h);
			viewBox.width = w; // boundLimiterW(w);
			viewBox.height = h; // boundLimiterH(h);
			viewBox.x = boundLimiterX(x, viewBox.width);
			viewBox.y = boundLimiterY(y, viewBox.height);
//			System.out.println("leaving: " + viewBox.x +" "+ viewBox.y +" "+ viewBox.width +" "+ viewBox.height);
			try{
				String s = toSVGAttribute(viewBox);
				if(vbe == null || !vbe.setAttributes(s)) {
					vbe = new EconomicViewBoxEvent(s);
//					System.out.println("[ViewBoxController " +this+"] sent viewBox update");
					dc.addToQueue(vbe);
				}
			} catch (NullPointerException e) {
				System.err.println("[ViewBoxController] Severe: Update manager not present on attribute change");
			} 
		}
		
		private int boundLimiterH(int h) {
			if(h < MINHEIGHT) return MINHEIGHT;
			else if(h > MAXHEIGHT) return MAXHEIGHT;
			else return h;
		}

		private int boundLimiterW(int w) {
			if(w < MINWIDTH) return MINWIDTH;
			else if(w > MAXWIDTH) return MAXWIDTH;
			else return w;
		}

		private int boundLimiterY(int y, int h) {
			if(y < 0) return 0;
			else if(y + h > docH) 
				return boundLimiterY(docH - h, h);
			else return y;
		}

		private int boundLimiterX(int x, int w) {
			if(x < 0) return 0;
			else if(x + w > docW)
				return boundLimiterX(docW - w, w);
			else return x;
		}
		
		private class EconomicViewBoxEvent implements Runnable {
			private boolean inExecution;
			private String viewBox;
			
			public EconomicViewBoxEvent(String viewBox) {
				this.viewBox = viewBox;
				inExecution = false;
			}
			
			public boolean setAttributes(String viewBox) {
				if(!inExecution) {
					this.viewBox = viewBox;
					return true;
				} else {
					return false;
				}
			}
			
			@Override
			public void run() {
				inExecution = true;
				d.getRootElement().setAttribute("viewBox", viewBox);
			}
		}

	}

	public void onPanelResize() {
		recalibrate();
		vbm.invalidate();
	}

	
	
}

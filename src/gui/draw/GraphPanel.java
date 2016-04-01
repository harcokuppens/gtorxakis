package gui.draw;

import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.gvt.GVTTreeRendererListener;
import org.w3c.dom.svg.SVGDocument;

import model.Model;

public class GraphPanel extends JSVGCanvas {
	private Model model;

	// SVG size. Default: 80, 45
	public static final int width = 80 * DrawableGrid.GRID_SIZE;
	public static final int height = 45 * DrawableGrid.GRID_SIZE; 
	
	private GVTTreeRendererListener currentListener;
	
	public GraphPanel() {
		super();

		this.eventsEnabled = false;
		this.setEnableRotateInteractor(false);
		this.setEnableImageZoomInteractor(false);
		this.setEnableZoomInteractor(false);
		this.setEnablePanInteractor(false);
		this.setRecenterOnResize(true);
		setDocumentState(JSVGCanvas.ALWAYS_DYNAMIC);
		this.setInputMap(0, null);
		this.setDoubleBufferedRendering(true);
	}

	public void setModel(Model model) {
		this.model = model;
	}

	/**
	 * Specifies which document to show
	 * @param doc the document to be rendered
	 */
	public void setDocument(SVGDocument doc) {
		super.setSVGDocument(doc);
		setDocumentState(JSVGCanvas.ALWAYS_DYNAMIC);
	}

	public void setGVTTreeRendererListener(GVTTreeRendererListener trl) {
		if(currentListener != null) {
			System.out.println("[GraphPanel] Freeing " + currentListener);
			this.removeGVTTreeRendererListener(currentListener);
		}
		currentListener = trl;
		this.addGVTTreeRendererListener(currentListener);
		
	}
	
	
}

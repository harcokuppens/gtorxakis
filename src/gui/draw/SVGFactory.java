package gui.draw;


import java.io.IOException;
import java.io.StringReader;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.dom.svg.SVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

public class SVGFactory {
	private Element defs;
	private SVGDocument doc;
	private Element nodeGroup, edgeGroup, commentGroup;
	
	public SVGFactory() {
		
	}	
	
	public SVGDocument generateDoc() {
		SVGDocumentFactory f = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName());
		int width = GraphPanel.width;
		int height = GraphPanel.height;
		String blankSVG = "<svg width=\"" + width + "\" height=\""+ height +"\" viewBox=\"0 0 0 0\" xmlns=\"http://www.w3.org/2000/svg\"></svg>";
		// viewBox=\"0 0 1920 1080\" preserveAspectRatio=\"xMidYMid meet\"
		doc = null;
		try {
			doc = f.createSVGDocument(null, new StringReader(blankSVG));
		} catch (IOException e) {
			System.err.println("SEVERE: Unable to generate SVG document");
			e.printStackTrace();
		}
		//Definitions
		defs = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "defs");
		
		//Arrow Marker
		Element pf1 = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "marker");
		pf1.setAttribute("id", "pf1");
		pf1.setAttribute("viewBox", "0 0 " + GraphInterface.ARROW_MARKER_SIZE*2 + " " + GraphInterface.ARROW_MARKER_SIZE*2);
		pf1.setAttribute("refX", String.valueOf((double) GraphInterface.ARROW_MARKER_SIZE / 10d));
		pf1.setAttribute("refY", String.valueOf(GraphInterface.ARROW_MARKER_SIZE));
		pf1.setAttribute("markerUnits", "strokeWidth");
		pf1.setAttribute("markerWidth", String.valueOf(GraphInterface.ARROW_MARKER_SIZE));
		pf1.setAttribute("markerHeight", String.valueOf(GraphInterface.ARROW_MARKER_SIZE));
		pf1.setAttribute("orient", "auto");
		Element path = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "path");
		path.setAttribute("d", "M0,0 L" + GraphInterface.ARROW_MARKER_SIZE*2 + "," + GraphInterface.ARROW_MARKER_SIZE + " L0," + GraphInterface.ARROW_MARKER_SIZE*2 + " Z");
		pf1.appendChild(path);
		defs.appendChild(pf1);
		
		//Arrow Marker (red)
		Element pf1red = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "marker");
		pf1red.setAttribute("id", "pf1red");
		pf1red.setAttribute("viewBox", "0 0 " + GraphInterface.ARROW_MARKER_SIZE*2 + " " + GraphInterface.ARROW_MARKER_SIZE*2);
		pf1red.setAttribute("refX", String.valueOf((double) GraphInterface.ARROW_MARKER_SIZE / 10d));
		pf1red.setAttribute("refY", String.valueOf(GraphInterface.ARROW_MARKER_SIZE));
		pf1red.setAttribute("markerUnits", "strokeWidth");
		pf1red.setAttribute("markerWidth", String.valueOf(GraphInterface.ARROW_MARKER_SIZE));
		pf1red.setAttribute("markerHeight", String.valueOf(GraphInterface.ARROW_MARKER_SIZE));
		pf1red.setAttribute("orient", "auto");
		Element pathred = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "path");
		pathred.setAttribute("d", "M0,0 L" + GraphInterface.ARROW_MARKER_SIZE*2 + "," + GraphInterface.ARROW_MARKER_SIZE + " L0," + GraphInterface.ARROW_MARKER_SIZE*2 + " Z");
		pathred.setAttribute("fill", "red");
		pf1red.appendChild(pathred);
		defs.appendChild(pf1red);
		
		//Drop Shadow Effect
		Element dropShadow = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "filter");
		dropShadow.setAttribute("id", "dropshadoweffect");
		dropShadow.setAttribute("x", "0");
		dropShadow.setAttribute("y", "0");
		dropShadow.setAttribute("width", "200%");
		dropShadow.setAttribute("height", "200%");
		Element dropShadowFeOffset = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "feOffset");
		dropShadowFeOffset.setAttribute("result", "offOut");
		dropShadowFeOffset.setAttribute("in", "SourceAlpha");
		dropShadowFeOffset.setAttribute("dx", "3");
		dropShadowFeOffset.setAttribute("dy", "3");
		Element dropShadowFeGaussianBlur = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "feGaussianBlur");
		dropShadowFeGaussianBlur.setAttribute("result", "blurOut");
		dropShadowFeGaussianBlur.setAttribute("in", "offOut");
		dropShadowFeGaussianBlur.setAttribute("stdDeviation", "2");
		Element dropShadowFeBlend = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "feBlend");
		dropShadowFeBlend.setAttribute("in", "SourceGraphic");
		dropShadowFeBlend.setAttribute("in2", "blurOut");
		dropShadowFeBlend.setAttribute("mode", "normal");
		dropShadow.appendChild(dropShadowFeOffset);
		dropShadow.appendChild(dropShadowFeGaussianBlur);
		dropShadow.appendChild(dropShadowFeBlend);
		defs.appendChild(dropShadow);

		doc.getRootElement().appendChild(defs);

		commentGroup = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "g");
		doc.getRootElement().appendChild(commentGroup);
		
		edgeGroup = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "g");
		doc.getRootElement().appendChild(edgeGroup);
		
		nodeGroup = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "g");
		doc.getRootElement().appendChild(nodeGroup);
		
		

		return doc;
	}
	
	public SVGDocument getDoc() {
		return doc;
	}
	
	public Element getDefs() {
		return defs;
	}
	
	public Element getNodeGroup(){
		return nodeGroup;
	}
	
	public Element getEdgeGroup(){
		return edgeGroup;
	}
	
	public Element getCommentGroup(){
		return commentGroup;
	}
	
	public static SVGDocument copyDocument(SVGDocument draft) {
		DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
		Document copy = impl.createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);
		
		copy.replaceChild(copy.importNode(draft.getDocumentElement(), true), copy.getDocumentElement());

		return (SVGDocument) copy;
	}

}

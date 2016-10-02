package io.file.graph.exporter;

import gui.draw.DrawableGraphEdge;
import gui.draw.SVGFactory;
import gui.control.Selectable;
import gui.draw.GraphInterface;
import io.file.Exporter;

import java.awt.Rectangle;

import model.graph.GraphComment;
import model.graph.GraphEdge;
import model.graph.GraphState;
import core.Session;

import org.w3c.dom.svg.SVGDocument;

public abstract class GraphExporter implements Exporter  {
	protected GraphInterface gi;
	protected boolean removedGrid;
	
	public abstract void createImage(String path, SVGDocument document, Rectangle areaOfInterest);
	
	public void setGraphInterface(GraphInterface gi) {
		this.gi = gi;
	}
	
	public void exportGraph(String path) {
		onExportStart();
		Rectangle aoi = calculateAOI();
		SVGDocument document = SVGFactory.copyDocument(gi.getDrawableGraph().getDocument());
		String viewBox = aoi.x + " " + aoi.y + " " + aoi.width + " " + aoi.height;
		document.getRootElement().setAttribute("viewBox", viewBox);
		createImage(path, document, aoi);		
		onExportFinish();
	}
	
	protected void onExportStart() {
		for(Selectable s : gi.getSelection()) {
			s.setSelected(false);
		}
	}
	
	protected void onExportFinish() {
		if(removedGrid) {
			gi.getDrawableGraph().getDocument().getRootElement().insertBefore(gi.getDrawableGraph().getGrid().getElement(), gi.getDefsElement().getNextSibling());
		}
		for(Selectable s : gi.getSelection()) {
			s.setSelected(true);
		}
	}
		
	protected Rectangle calculateAOI() {
		Rectangle aoi = gi.getGraph().getStates().get(0).getDrawable().getBoundingBox(true);
		for(GraphState state : gi.getGraph().getStates()) {
			Rectangle rect = state.getDrawable().getBoundingBox(true);
			aoi.add(rect);
			for(GraphEdge edge : state.getOutgoingEdges()){
				DrawableGraphEdge dge = edge.getDrawable();
				aoi.add(dge.getBoundingBox());
				if(dge.getComment() != null){
					aoi.add(dge.getComment().getBoundingBox());
				}
			}
		}
		for(GraphComment comment : gi.getGraph().getComments()){
			Rectangle rect = comment.getDrawable().getBoundingBox();
			aoi.add(rect);
		}
		aoi.grow(50, 50);
		return aoi;
	}
}

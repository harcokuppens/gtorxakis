package io.file.graph.exporter;

import gui.draw.GraphInterface;
import io.file.FileType;

public class GraphExport {
	private GraphInterface gi;
	private String path;
	private FileType exportType;
	
	public GraphExport(GraphInterface gi, String path, FileType filetype) {
		this.gi = gi;
		this.path = path;
		this.exportType = filetype;
	}
	
	public void exportGraph() {
		GraphExporter exporter = (GraphExporter) exportType.getExporter();
		exporter.setGraphInterface(gi);
		exporter.exportGraph(path);
	}

}

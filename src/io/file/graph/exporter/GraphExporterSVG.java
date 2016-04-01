package io.file.graph.exporter;

import gui.draw.GraphInterface;

import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.input.DOMBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.w3c.dom.svg.SVGDocument;

public class GraphExporterSVG extends GraphExporter {
	@Override
	public void createImage(String path, SVGDocument document, Rectangle areaOfInterest) {
		File file = new File(path);
		DOMBuilder domBuilder = new DOMBuilder();
		Document graphExport = domBuilder.build(document);
		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
		try {
			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			outputter.output(graphExport, output);
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

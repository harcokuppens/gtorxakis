package io.file.project.exporter;

import gui.draw.DrawableComment;
import gui.draw.DrawableGraphState;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.bind.DatatypeConverter;

import model.Definition;
import model.Gates.Gate;
import model.Gates;
import model.Model;
import model.Project;
import model.TextualDefinition;
import model.Transition;
import model.Variables;
import model.Variables.Variable;
import model.graph.GraphComment;
import model.graph.GraphEdge;
import model.graph.GraphState;

import org.jdom2.DocType;
import org.jdom2.Element;
import org.jdom2.Text;
import org.jdom2.output.Format;
import org.jdom2.output.StAXStreamOutputter;

import util.MemDiag;
import core.Session;

public class ProjectExporterGTorX implements ProjectExporter {

	public static final String ENCODING = "UTF-8";
	
	private class IntermediateStream extends OutputStream {
		private final StAXStreamOutputter wrappedOutputter;
		private final XMLStreamWriter wrappedWriter;

		public IntermediateStream(StAXStreamOutputter wrappedOutputter, XMLStreamWriter wrappedWriter) {
			this.wrappedOutputter = wrappedOutputter;
			this.wrappedWriter = wrappedWriter;
		}

		@Override
		public void close() throws IOException {
			flush();
		}

		@Override
		public void flush() throws IOException {
			try {
				wrappedWriter.flush();
			} catch (XMLStreamException e) {
				throw new IOException(e);
			}
		}

		// Writes b.length bytes from the specified byte array to this output stream.
		@Override
		public void write(byte[] b) throws IOException {
			try {
				wrappedOutputter.output(new Text(DatatypeConverter.printBase64Binary(b)), wrappedWriter);
			} catch (XMLStreamException e) {
				throw new IOException(e);
			}
		}

		// Writes len bytes from the specified byte array starting at offset off to this output stream.
		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			byte[] dest = new byte[len];
			System.arraycopy(b, off, dest, 0, len);
			try {
				wrappedOutputter.output(new Text(DatatypeConverter.printBase64Binary(dest)), wrappedWriter);
			} catch (XMLStreamException e) {
				throw new IOException(e);
			}
		}

		// Writes the specified byte to this output stream.
		@Override
		public void write(int b) throws IOException {
			try {
				wrappedOutputter.output(new Text(DatatypeConverter.printBase64Binary(new byte[]{(byte)b})), wrappedWriter);
			} catch (XMLStreamException e) {
				throw new IOException(e);
			}
		}
	}
	
	@Override
	public void exportProject(Project project, String path) throws XMLStreamException, FactoryConfigurationError, IOException {
		MemDiag.diagnose();
		
		StAXStreamOutputter streamOutputter = 
				new StAXStreamOutputter(
						Format.getRawFormat().
						setExpandEmptyElements(true).
						setLineSeparator("\n").
						setTextMode(Format.TextMode.TRIM_FULL_WHITE)
						);
		
		BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(new File(path)));
		XMLStreamWriter streamWriter = XMLOutputFactory.newFactory().createXMLStreamWriter(bout, ENCODING);

		IntermediateStream is = new IntermediateStream(streamOutputter, streamWriter);
		BufferedOutputStream bis = new BufferedOutputStream(is);
		ZipOutputStream zout = new ZipOutputStream(bis);
		BufferedOutputStream bzout = new BufferedOutputStream(zout);

		XMLStreamWriter zipStreamWriter = XMLOutputFactory.newFactory().createXMLStreamWriter(bzout, ENCODING);
		
		streamWriter.writeStartDocument();
		
			streamOutputter.output(new DocType("xml"), streamWriter);
			
			streamWriter.writeStartElement("project");
				//Start project
				streamOutputter.output(new Element("name").addContent(project.getName()), streamWriter);
				streamOutputter.output(new Element("version").addContent(Session.PROGRAM_VERSION.toString()), streamWriter);
				
				createModels(streamOutputter, streamWriter, project);
				//End project
			streamWriter.writeEndElement();
			
		streamWriter.writeEndDocument();
		
		//Close streams
		streamWriter.close();
		bout.close();
		
	}
	
	
	private void createComments(StAXStreamOutputter streamOutputter, XMLStreamWriter streamWriter, Model model) throws XMLStreamException{
		streamWriter.writeStartElement("comments");
		for(GraphComment c : model.getGraph().getComments()){
			streamOutputter.output(new Element("comment")
			.setAttribute("type", String.valueOf(c.getDrawable().getCommentType().getName()))
			.setAttribute("text", c.getDrawable().getAttribute(DrawableComment.TEXT))
			.setAttribute("posX", String.valueOf(c.getDrawable().getPosition().x))
			.setAttribute("posY", String.valueOf(c.getDrawable().getPosition().y))
			.setAttribute("width", String.valueOf(c.getDrawable().getWidth())), streamWriter);
		}
		streamWriter.writeEndElement();
	}
	
	private void createEdges(StAXStreamOutputter streamOutputter, XMLStreamWriter streamWriter, Model model) throws XMLStreamException{
		streamWriter.writeStartElement("outgoingEdges");
			for(GraphState s : model.getGraphInterface().getGraph().getStates()){
				for(GraphEdge edge : s.getOutgoingEdges()){
					streamWriter.writeStartElement("edge");
						streamWriter.writeAttribute("from", String.valueOf(edge.getFrom().getAttribute("name")));
						streamWriter.writeAttribute("to", String.valueOf(edge.getTo().getAttribute("name")));
						streamWriter.writeAttribute("cPosX", edge.getDrawable().getComment().getPosition().x+"");
						streamWriter.writeAttribute("cPosY", edge.getDrawable().getComment().getPosition().y+"");
						streamWriter.writeAttribute("cWidth", edge.getDrawable().getComment().getWidth()+"");
						streamWriter.writeAttribute("aPosX", edge.getDrawable().getAnchorPoint().x+"");
						streamWriter.writeAttribute("aPosY", edge.getDrawable().getAnchorPoint().y+"");
						
						for(Transition t : edge.getTransitions()){
							streamOutputter.output(new Element("transition")
							.setAttribute("channel", t.getChannel())
							.setAttribute("condition", t.getCondition())
							.setAttribute("action", t.getAction()),streamWriter);
						}
					streamWriter.writeEndElement();
				}			
			}
		streamWriter.writeEndElement();
	}
	
	private void createGates(StAXStreamOutputter streamOutputter, XMLStreamWriter streamWriter, Model model) throws XMLStreamException{
		streamWriter.writeStartElement("gates");
			for(Gate g: model.getGates().getGates()){
				streamWriter.writeStartElement("gate");
				streamWriter.writeAttribute(Gates.ATTRIBUTE_NAME, String.valueOf(g.getName()));
				streamWriter.writeAttribute(Gates.ATTRIBUTE_TYPE, String.valueOf(g.getType()));
				streamWriter.writeEndElement();
			}
		streamWriter.writeEndElement();
	}
	
	private void createVariables(StAXStreamOutputter streamOutputter, XMLStreamWriter streamWriter, Model model) throws XMLStreamException{
		streamWriter.writeStartElement("variables");
			for(Variable v: model.getVariables().getVariables()){
				streamWriter.writeStartElement("variable");
				streamWriter.writeAttribute(Variables.ATTRIBUTE_NAME, String.valueOf(v.getName()));
				streamWriter.writeAttribute(Variables.ATTRIBUTE_TYPE, String.valueOf(v.getType()));
				streamWriter.writeAttribute(Variables.ATTRIBUTE_INIT, String.valueOf(v.getInitValue()));
				streamWriter.writeEndElement();
			}
		streamWriter.writeEndElement();
	}
	
	private void createModel(StAXStreamOutputter streamOutputter, XMLStreamWriter streamWriter, Model model) throws XMLStreamException {
		streamWriter.writeStartElement("model");
			//Start model
			if(model.getGraph().getStartState() != null){
				streamOutputter.output(new Element("name").addContent(model.getName()).setAttribute("startState", model.getGraph().getStartState().getName()), streamWriter);
			}else{
				streamOutputter.output(new Element("name").addContent(model.getName()).setAttribute("startState", ""), streamWriter);
			}

			for (GraphState s : model.getGraphInterface().getGraph().getStates()) {
				//Create nodeElement
				streamWriter.writeStartElement("node");
					//Start node
					streamWriter.writeAttribute("positionX",
							s.getDrawable().getAttribute(DrawableGraphState.ATTRIBUTE_POSITION_X));
					streamWriter.writeAttribute("positionY",
							s.getDrawable().getAttribute(DrawableGraphState.ATTRIBUTE_POSITION_Y));
			
					//Add name
					streamOutputter.output(new Element(GraphState.ATTRIBUTE_NAME).addContent(s.getName()), streamWriter);
								
					//End node
				streamWriter.writeEndElement();
			}
			createEdges(streamOutputter, streamWriter, model);
			createComments(streamOutputter, streamWriter, model);
			createGates(streamOutputter, streamWriter, model);
			createVariables(streamOutputter, streamWriter, model);
			//End model
		streamWriter.writeEndElement();
	}

	private void createTextualDefinition(StAXStreamOutputter streamOutputter, XMLStreamWriter streamWriter,
			TextualDefinition t) throws XMLStreamException {
		streamWriter.writeStartElement("textual");
		//Start model
			System.err.println(t.getTitle());
			streamWriter.writeAttribute("title", t.getTitle());
			streamWriter.writeAttribute("type", t.getType().name());
			streamOutputter.output(new Text(t.getDefinitionText()), streamWriter);
		//End model
		streamWriter.writeEndElement();
	}
	
	private void createModels(StAXStreamOutputter streamOutputter, XMLStreamWriter streamWriter, Project project) throws XMLStreamException{
		streamWriter.writeStartElement("definitions");
			//Start definitions
			for(Definition d: project.getDefinitions()) {
				if(d instanceof Model){
					Model m = (Model) d;
					createModel(streamOutputter, streamWriter, m);
				}else{
					TextualDefinition t = (TextualDefinition) d;
					createTextualDefinition(streamOutputter, streamWriter, t);
				}
			}
			//End definitions
		streamWriter.writeEndElement();
	}
	
}

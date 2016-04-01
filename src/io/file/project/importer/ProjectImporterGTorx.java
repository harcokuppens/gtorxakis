package io.file.project.importer;

import gui.draw.DrawableComment;
import gui.draw.DrawableComment.CommentType;
import gui.draw.DrawableGraph;
import gui.draw.DrawableGraphEdge;
import gui.draw.DrawableGraphState;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;


import model.Model;
import model.Project;
import model.graph.Graph;
import model.graph.GraphComment;
import model.graph.GraphEdge;
import model.graph.GraphState;
import core.Session;
import core.Version;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.DataConversionException;
import org.w3c.dom.svg.SVGDocument;

public class ProjectImporterGTorx extends ProjectImporter {
	public static final String ENCODING = "UTF-8";

	@Override
	public Project importProject(File file) throws IOException {
		SAXBuilder SAXBuilder = new SAXBuilder();
		Version version = null;
		try {
			//workaround - subject to change
			Document document = (Document) SAXBuilder.build(file);
			Element projectElement = document.getRootElement();
			String name = projectElement.getChild("name").getText();
			Element versionElement = projectElement.getChild("version");
			if(versionElement == null) {
				version = new Version(1, 0, 2); // Last version without version tag in project files
			} else {
				version = new Version(versionElement.getText());
			}

			Project project = new Project(name);
			project.setPath(file.getPath());

			// import multiple models
			Element modelsElement = projectElement.getChild("models");
			for(Element modelElement: modelsElement.getChildren("model")) {
				Model m = importModel(modelElement, project, version);
				project.addModel(m);
			}

			project.setSaved();
			return project;
		} catch (JDOMException e) {
				throw new IOException(e.getMessage());
		} catch (NumberFormatException e){
				throw new IOException(NUMBER_FORMAT_EXCEPTION);
		} catch (Exception e){
			if(version != null && version.isNewerThan(Session.getSession().PROGRAM_VERSION)){
				throw new IOException(VERSION_TOO_OLD);
			}else{
				throw new IOException(e.getMessage());
			}
		}
	}

	private Model importModel(Element modelElement, Project project, Version version) throws DataConversionException {
		System.out.println("Import model");
		Graph graph = new Graph();
		DrawableGraph drawableGraph = new DrawableGraph();
		SVGDocument svgDoc = drawableGraph.getDocument();

		String name = modelElement.getChild("name").getText();

		//Get comments
		Element commentsElement = modelElement.getChild("comments");
		if(commentsElement != null){
			List<Element> comments = commentsElement.getChildren("comment");
			ArrayList<GraphComment> commentList = new ArrayList<GraphComment>();
			for(Element c : comments){
				int posX = Integer.valueOf(c.getAttributeValue("posX"));
				int posY = Integer.valueOf(c.getAttributeValue("posY"));
				String text = c.getAttributeValue("text");
				CommentType type = CommentType.getType(c.getAttributeValue("type"));
				int width = Integer.valueOf(c.getAttributeValue("width"));
				DrawableComment dc = new DrawableComment(svgDoc,posX,posY, width, type, text.split("\n"));
				GraphComment gc = new GraphComment(dc);
				commentList.add(gc);
			}
			graph.addComments(commentList);
		}

			
		List<Element> nodes = modelElement.getChildren("node");
		for(Element n : nodes){
			int posX = Integer.valueOf(n.getAttributeValue("positionX"));
			int posY = Integer.valueOf(n.getAttributeValue("positionY"));
			//Name
			String nodeName = n.getChild(GraphState.ATTRIBUTE_NAME).getText();
			//GraphState
			DrawableGraphState dgn = new DrawableGraphState(svgDoc, posX, posY, nodeName);
			graph.addState(dgn.getNode());
		}

		//OutgoingEdges / IncomingEdges
		for(Element t : modelElement.getChild("outgoingEdges").getChildren()){
			String from = t.getAttributeValue("from");
			String to = t.getAttributeValue("to");
			String edgeName = t.getAttributeValue("name");
			DrawableGraphState fromNode = graph.getStateWithName(from).getDrawable();
			DrawableGraphState toNode = graph.getStateWithName(to).getDrawable();
			DrawableGraphEdge e = new DrawableGraphEdge(svgDoc, fromNode, toNode);
			e.getEdge().setAttribute(GraphEdge.ATTRIBUTE_NAME, edgeName);
			fromNode.getNode().addOutgoingEdge(e.getEdge());
			toNode.getNode().addIncomingEdge(e.getEdge());
		}
		return new Model(project, name, graph, drawableGraph);
	}

}

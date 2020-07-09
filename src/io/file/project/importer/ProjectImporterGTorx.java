package io.file.project.importer;

import gui.draw.DrawableComment;
import gui.draw.DrawableComment.CommentType;
import gui.draw.DrawableGraph;
import gui.draw.DrawableGraphEdge;
import gui.draw.DrawableGraphState;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import model.Gates;
import model.Gates.Gate;
import model.Model;
import model.Project;
import model.TextualDefinition;
import model.Transition;
import model.Variables;
import model.Variables.Variable;
import model.graph.Graph;
import model.graph.GraphComment;
import model.graph.GraphState;

import org.jdom2.DataConversionException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.w3c.dom.svg.SVGDocument;

import core.Session;
import core.Version;

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
			version = new Version(versionElement.getText());

			Project project = new Project(name);
			project.setPath(file.getPath());

			// import multiple models
			Element modelsElement = projectElement.getChild("definitions");
			for(Element textualElement : modelsElement.getChildren("textual")){
				TextualDefinition t = importTextualDefinition(textualElement, project, version);
				project.addDefinition(t);
			}
			
			
			for(Element modelElement: modelsElement.getChildren("model")) {
				Model m = importModel(modelElement, project, version);
				project.addDefinition(m);
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
				e.printStackTrace();
				throw new IOException(e.getMessage());
			}
		}
	}

	private TextualDefinition importTextualDefinition(Element textualElement, Project project, Version version) {
		System.out.println("Import textual definition");
		String title = textualElement.getAttributeValue("title");
		String type = textualElement.getAttributeValue("type");
		String definition = textualElement.getText();
		return new TextualDefinition(definition, title, TextualDefinition.DefType.valueOf(type));
	}

	private Model importModel(Element modelElement, Project project, Version version) throws DataConversionException {
		System.out.println("Import model");
		Graph graph = new Graph();
		DrawableGraph drawableGraph = new DrawableGraph();
		SVGDocument svgDoc = drawableGraph.getDocument();

		Element nameElement = modelElement.getChild("name");
		String name = nameElement.getText();
		String startState = nameElement.getAttributeValue("startState");

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
				DrawableComment dc = new DrawableComment(svgDoc,posX,posY, width, type, text.split("\n"),null);
				GraphComment gc = new GraphComment(dc, null);
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
			if((!startState.equals("")) && nodeName.equals(startState)){
				System.out.println("Found start state");
				dgn.getState().setAttribute(GraphState.ATTRIBUTE_START_STATE, true);
			}
			graph.addState(dgn.getState());
		}

		//OutgoingEdges / IncomingEdges
		for(Element t : modelElement.getChild("outgoingEdges").getChildren()){
			String from = t.getAttributeValue("from");
			String to = t.getAttributeValue("to");
			ArrayList<Transition> transitions = new ArrayList<Transition>();
			for(Element transition : t.getChildren("transition")){
				transitions.add(new Transition(transition.getAttribute("channel").getValue(), transition.getAttribute("condition").getValue(), transition.getAttribute("action").getValue()));
			}
			Point commentPosition = new Point(Integer.valueOf(t.getAttributeValue("cPosX")), Integer.valueOf(t.getAttributeValue("cPosY")));
			
			Point anchorPoint = new Point (Integer.valueOf(t.getAttributeValue("aPosX")), Integer.valueOf(t.getAttributeValue("aPosY")));
			
			int commentWidth = (Integer.valueOf(t.getAttributeValue("cWidth")));
			DrawableGraphState fromNode = graph.getStateWithName(from).getDrawable();
			DrawableGraphState toNode = graph.getStateWithName(to).getDrawable();
			DrawableGraphEdge e = new DrawableGraphEdge(svgDoc, fromNode, toNode, anchorPoint, transitions, commentPosition, commentWidth);
			fromNode.getState().addOutgoingEdge(e.getEdge());
			toNode.getState().addIncomingEdge(e.getEdge());
		}
		
		ArrayList<Gate> gates = new ArrayList<Gate>();
		ArrayList<Variable> variables = new ArrayList<Variable>();
		if(modelElement.getChild("gates") != null || modelElement.getChild("variables")!=null){
			for(Element e : modelElement.getChild("gates").getChildren()){
				String gateName = e.getAttributeValue(Gates.ATTRIBUTE_NAME);
				String gateType = e.getAttributeValue(Gates.ATTRIBUTE_TYPE);
				gates.add(new Gate(gateName, gateType));
			}
			for(Element e : modelElement.getChild("variables").getChildren()){
				String variableName = e.getAttributeValue(Variables.ATTRIBUTE_NAME);
				String variableType = e.getAttributeValue(Variables.ATTRIBUTE_TYPE);
				String initValue = e.getAttributeValue(Variables.ATTRIBUTE_INIT);
				System.out.println("[Importer] initValue of "+ variableName + " = " +initValue);
				variables.add(new Variable(variableName, variableType, initValue));
			}
		}
		
		return new Model(project, name, graph, drawableGraph, gates, variables);
	}

}

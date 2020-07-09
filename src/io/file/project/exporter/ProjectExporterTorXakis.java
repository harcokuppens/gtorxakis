package io.file.project.exporter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import model.Definition;
import model.Project;

public class ProjectExporterTorXakis implements ProjectExporter{

	@Override
	public void exportProject(Project project, String path) throws FileNotFoundException, XMLStreamException, FactoryConfigurationError, IOException {
		PrintWriter writer = new PrintWriter(path, "UTF-8");
		for(Definition d : project.getDefinitions()){
			writer.println(d.getDefinitionAsText());
			writer.println("");
		}
		writer.close();
	}

}

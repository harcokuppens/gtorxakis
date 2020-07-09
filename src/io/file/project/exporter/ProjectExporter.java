package io.file.project.exporter;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import io.file.Exporter;
import model.Project;

public interface ProjectExporter extends Exporter {
	public void exportProject(Project project, String path) throws FileNotFoundException, XMLStreamException, FactoryConfigurationError, IOException;
}

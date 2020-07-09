package io.file.project.exporter;


import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import io.file.FileType;
import model.Project;

public class ProjectExport {
	private Project project;
	private String path;
	private FileType exportType;
	
	public ProjectExport(Project project, String path, FileType filetype) {
		this.project = project;
		this.path = path;
		this.exportType = filetype;
	}
	
	public void exportProject() {
		ProjectExporter exporter = (ProjectExporter) exportType.getExporter();
		try {
			exporter.exportProject(project, path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

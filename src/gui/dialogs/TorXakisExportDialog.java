package gui.dialogs;

import io.file.FileType;
import io.file.FileTypeAssociation;
import io.file.project.exporter.ProjectExporterTorXakis;

import java.io.IOException;

import javax.swing.JFrame;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import model.Project;

public class TorXakisExportDialog extends AdvancedFileChooserSave{

	private Project project;

	public TorXakisExportDialog(JFrame parent, FileTypeAssociation association, Project project) {
		super(parent, association);
		this.project = project;
		setFile(project.getName() + "." + association.getDefaultFileType().getFileExtensions()[0]);
		
		showSaveDialog();
	}

	@Override
	protected void success(String path, FileType filetype) {
		ProjectExporterTorXakis exporter = new ProjectExporterTorXakis();
		try {
			exporter.exportProject(project, path);
		} catch (XMLStreamException | FactoryConfigurationError | IOException e) {
			e.printStackTrace();
		}
	}
}

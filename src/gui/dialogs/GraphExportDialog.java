package gui.dialogs;

import io.file.FileType;
import io.file.FileTypeAssociation;
import io.file.graph.exporter.GraphExport;

import javax.swing.JFrame;

import model.Model;

public class GraphExportDialog extends AdvancedFileChooserSave {
	private Model model;

	public GraphExportDialog(JFrame parent, FileTypeAssociation association, Model model) {
		super(parent, association);
		this.model = model;
		setFile(model.getProject().getName() + "." + association.getDefaultFileType().getFileExtensions()[0]);
		
		showSaveDialog();
	}

	@Override
	protected void success(String path, FileType filetype) {
		GraphExport exporter = new GraphExport(model.getDrawController().getGraphInterface(), path, filetype);
		exporter.exportGraph();		
	}
}

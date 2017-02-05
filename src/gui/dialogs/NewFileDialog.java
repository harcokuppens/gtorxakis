package gui.dialogs;

import io.file.FileType;
import io.file.FileTypeAssociation;

import java.awt.Frame;

import model.Project;
import core.Session;

public class NewFileDialog extends AdvancedFileChooserSave{
	private static final FileTypeAssociation association = FileTypeAssociation.ProjectExport;

	public NewFileDialog(Frame parent){
		super(parent, association);
		init();
	}


	private void init() {
		setFile("Unnamed." + association.getDefaultFileType().getFileExtensions()[0]);
		showSaveDialog();
	}

	@Override
	protected void success(String path, FileType filetype) {
		Project project = Project.newProject();
		project.saveAs(path, filetype);
		Session.getSession().setProject(project);
		Session.getSession().getWindow().invalidate();
	}

}

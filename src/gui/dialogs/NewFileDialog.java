package gui.dialogs;

import java.awt.Frame;
import java.io.File;
import java.awt.Dialog;

import javax.swing.JFrame;
import javax.swing.filechooser.FileView;

import model.Project;
import core.Session;
import io.file.FileType;
import io.file.FileTypeAssociation;

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
		System.out.println("[NewFileDialog] Attemp to close startscreen!");
		Session.getSession().getWindow().invalidate();
	}

}

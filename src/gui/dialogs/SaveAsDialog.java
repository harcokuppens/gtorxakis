package gui.dialogs;

import io.file.FileType;
import io.file.FileTypeAssociation;

import java.io.File;

import java.awt.Frame;
import java.awt.Dialog;

import core.Session;

import model.Project;

public class SaveAsDialog extends AdvancedFileChooserSave {
	private static final FileTypeAssociation association = FileTypeAssociation.ProjectExport;

	public SaveAsDialog(Frame parent) {
		super(parent, association);
		init();
	}
	
	public SaveAsDialog(Dialog parent) {
		super(parent, association);
		init();
	}

	private void init() {
		setFile(Session.getSession().getProject().getName() + "." + association.getDefaultFileType().getFileExtensions()[0]);
		
		if(Session.getSession().getProject().getPath() != null) {
			showSaveDialog(Session.getSession().getProject().getPath());
		} else {
			showSaveDialog();
		} 
	}
	
	@Override
	protected void success(String path, FileType filetype) {
		Project p = Session.getSession().getProject();
		p.saveAs(path, filetype);
		Session.getSession().getWindow().setTitle(p);
	}
}

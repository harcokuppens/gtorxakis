package gui.dialogs;

import io.file.FileType;
import io.file.FileTypeAssociation;
import io.file.project.importer.ProjectImporter;

import java.awt.Dialog;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;

import core.Session;

public class OpenDialog extends AdvancedFileChooserOpen {
	private static FileTypeAssociation association = FileTypeAssociation.ProjectImport;
	
	public OpenDialog(Frame parent, boolean startscreen) {
		super(parent, association);
		setDirectory(Session.getSession().getDefaultPath());		
		
		showOpenDialog();
	}

	public OpenDialog(Dialog parent, boolean startscreen) {
		super(parent, association);
		setDirectory(Session.getSession().DEFAULT_PATH);		
		showOpenDialog();
	}

	@Override
	protected void success(File file, FileType filetype) {
		ProjectImporter importer = (ProjectImporter) filetype.getImporter();
		try {
			System.out.println("[Project] attempt to load project");
			Session.getSession().setProject(importer.importProject(file));
		} catch(IOException e) {
			importer.showErrorMessage(e.getMessage(), file.getAbsolutePath());
		}
	}

}

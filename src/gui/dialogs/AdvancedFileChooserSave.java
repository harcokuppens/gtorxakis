package gui.dialogs;

import io.file.FileType;
import io.file.FileTypeAssociation;

import java.awt.Dialog;
import java.awt.Frame;
import java.io.File;

import core.Session;

public abstract class AdvancedFileChooserSave extends AdvFileChooser {
	
	protected abstract void success(String path, FileType filetype);
	
	public AdvancedFileChooserSave(Frame parent, FileTypeAssociation association) {
		super(parent, association, "Save", AdvFileChooser.SAVE);
	}

	public AdvancedFileChooserSave(Dialog parent, FileTypeAssociation association) {
		super(parent, association, "Save", AdvFileChooser.SAVE);
	}

	protected void showSaveDialog() {
		this.showSaveDialog(Session.getSession().getDefaultPath() + File.separator);
	}

	protected void showSaveDialog(String directory) {
		setDirectory(directory);
		super.setVisible(true);
		String filename = super.getFullFile();
		if(filename == null) {
			//Cancel
			return;
		}
		FileType filetype = association.getByFilename(filename);
		if(filetype == null) {
			// File type was not specified by user
			filetype = association.getDefaultFileType();
			filename += "." + filetype.getFileExtensions()[0];
		}
		final String filenameF = filename;
		final FileType filetypeF = filetype;
		(new Thread() {
			@Override
			public void run() {
				WaitingDialog wd = new WaitingDialog("Saving", "Saving file...");
				success(filenameF, filetypeF);
				wd.dispose();
			}
		}).start();
	}
	
}

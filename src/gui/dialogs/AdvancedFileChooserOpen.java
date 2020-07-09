package gui.dialogs;

import io.file.FileType;
import io.file.FileTypeAssociation;

import java.awt.Dialog;
import java.awt.Frame;
import java.io.File;

public abstract class AdvancedFileChooserOpen extends AdvFileChooser {

	protected abstract void success(File file, FileType filetype);
	
	public AdvancedFileChooserOpen(Frame parent, FileTypeAssociation association) {
		super(parent, association, "Open", AdvFileChooser.LOAD);
	}
	
	public AdvancedFileChooserOpen(Dialog parent, FileTypeAssociation association) {
		super(parent, association, "Open", AdvFileChooser.LOAD);
	}
	
	protected void showOpenDialog() {
		System.out.println("[AdvancedFileChooser] Open OpenDialog");
		setVisible(true);
		String filename = getFullFile();
		if(filename != null) {
			final File file = new File(filename);
			final FileType filetype = association.getByFilename(filename);
			(new Thread() {
				@Override
				public void run() {
					WaitingDialog wd = new WaitingDialog("Opening", "Opening file...");
					success(file, filetype);
					wd.dispose();
					System.out.println("[AdvancedFileChooser] After success!");
				}
			}).start();
		}
	}
}

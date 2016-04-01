package io.file.project.importer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JOptionPane;

import core.Session;
import gui.dialogs.OpenDialog;
import io.file.Importer;
import model.Project;

public abstract class ProjectImporter implements Importer {

	public static final String NUMBER_FORMAT_EXCEPTION = "Failed to cast numbers";
	public static final String VERSION_TOO_OLD = "Version too old";

	public abstract Project importProject(File file) throws IOException;

	public Project importProject(String path) throws IOException {
		File f = new File(path);
		if (!f.exists()) {
			throw new FileNotFoundException();
		}
		return importProject(f);
	}

	public void showErrorMessage(String m, String path) {
		String message = "";
		String title = "";
		if(m == null) m = "";
		switch (m) {
		case NUMBER_FORMAT_EXCEPTION:
			message = "The entered project file contains invalid input data. Please check the project file!";
			title = ProjectImporter.NUMBER_FORMAT_EXCEPTION;
			break;
		case VERSION_TOO_OLD:
			message = "The entered project file has been created with a newer version of "
					+ Session.PROGRAM_NAME + ".\n" + " Please download and install the newest version of "
					+ Session.PROGRAM_NAME + " and try again to load this project";
			title = ProjectImporter.VERSION_TOO_OLD;
			break;
		default:
			message = "An error occured while trying to load the project located at\n" + "\t" + path;
			title = "Unable to load project";
		}
		JOptionPane.showMessageDialog(Session.getSession().getWindow(), message, title, JOptionPane.ERROR_MESSAGE);
		System.err.println("[ProjectImporter]: " + m + ", Path: " + path);
	}
}

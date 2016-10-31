package core;

import gui.control.DrawController;
import gui.draw.GraphInterface;
import gui.window.Window;
import io.file.settings.SettingsExporterXML;
import io.file.settings.SettingsImporterXML;

import java.io.IOException;
import java.util.Locale;
import java.util.Observable;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jdom2.JDOMException;

import model.Project;
import util.Environment;

public class Session extends Observable {
	public static final String PROGRAM_NAME = "TorXakis";
	public static final Version PROGRAM_VERSION = new Version(1, 0, 0);
	public static boolean DEVMODE = true;
	public static final String CONFIG_FILENAME = Environment.getApplicationDataFolder() + Environment.fileSeparator + "config.xml";
	public static final String DEFAULT_PATH = Environment.getApplicationDataFolder() + Environment.fileSeparator + "data" + Environment.fileSeparator;
	public static final String TEMP_TXS = Environment.getTempDataFolder() + Environment.fileSeparator + "temp.txs";
	
	private Project currentProject = null;
	private Window window;
	private Locale locale = Locale.ENGLISH;
	
	private SessionSettings settings;
	
	private static Session instance;

	public static void main(String[] args) {
		System.out.println("Operating system:\t" + Environment.OperatingSystem + "\napplication dir:\t" + Environment.applicationDataDir + "\nComputer ID:\t" + Environment.getComputerID());
		new Session(args);
	}

	public Session(String... args) {
		instance = this;
		
		int firstPathIndex = 0;
		if(args.length > 0 && args[0].trim().equals("--dev")) {
			DEVMODE = true;
			firstPathIndex = 1;
		}
		
		startupProcedure();
		this.setProject(Project.newProject());

		window.setVisible(true);
		window.invalidate();
	}

	public static Session getSession() {
		return instance;
	}

	public Project getProject() {
		return currentProject;
	}

	public void setProject(Project project) {
		if(closeProject()) {
			window.setProject(currentProject, project);			
			this.currentProject = project;
		}
	}

	/**
	 * Attempts to close the current project. If there are unsaved changes, the user sees a dialog
	 * asking whether they want to change the changes.
	 * 
	 * The function returns false if the user presses cancel in that dialog, true otherwise.
	 */
	private boolean closeProject() {
		if (currentProject == null || currentProject.isSaved()) {
			return true;
		} else {
			System.out.println("[Session] unsaved changes, opening dialog");
			return window.confirmClose();
		}
	}

	public Window getWindow() {
		return window;
	}

	public void startupProcedure() {
		if(Environment.OperatingSystem == Environment.OS.Mac){
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", Session.PROGRAM_NAME);
			System.setProperty("apple.awt.fakefullscreen", "true");
		}	
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			Locale.setDefault(locale);
			JComponent.setDefaultLocale(locale);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		settings = SettingsImporterXML.importSettings(CONFIG_FILENAME);
		window = new Window(this);
		addObserver(window);
	}
	
	public void invalidate() {
		setChanged();
		notifyObservers();
	}
	
	public void shutdownProcedure() {
		if(currentProject != null){
			if(!currentProject.isSaved()) {
				if(!window.confirmClose()) return;
			}
		}
		SettingsExporterXML.exportSettings(settings, CONFIG_FILENAME);
		System.exit(0);
	}

	public String getDefaultPath() {
		return DEFAULT_PATH;
	}
	
	public SessionSettings getSettings(){
		return settings;
	}
	
	public void setSettings(SessionSettings settings){
		this.settings = settings;
		SettingsExporterXML.exportSettings(settings, CONFIG_FILENAME);
	}

}

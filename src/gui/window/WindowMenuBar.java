package gui.window;

import gui.control.InputListener;
import gui.control.WindowActionListener;
import model.Project;
import model.Model;

import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import util.Environment;
import core.Session;

public class WindowMenuBar extends JMenuBar{

	private WindowActionListener wat;
	private InputListener inputListener;
	private JMenuItem clearData, 
					  importData, 
					  addModel, 
					  addProc,
					  undo, 
					  redo, 
					  showResults, 
					  exportHTML, 
					  exportEXCEL,
					  exportGraphPng, 
					  exportGraphSvg, 
					  exportGraphJpg, 
					  exportTorXakis;
	private JMenu projectMenu;
	private JCheckBoxMenuItem showGrid;
	
	public WindowMenuBar(WindowActionListener wat, InputListener inputListener){
		this.wat = wat;
		this.inputListener = inputListener;
		this.initialize();
	}

	private void initialize(){
		//File menu
		JMenu file = addMenu("File");
		addMenuItem("New", file, true, KeyEvent.VK_N, false, "new", "/icons/page_white_add.png", wat);
		addMenuItem("Open...", file,true, KeyEvent.VK_O, false, "open","/icons/folder.png" ,wat);
		addMenuItem("Save", file, true, KeyEvent.VK_S, false, "save", "/icons/disk.png", wat);
		addMenuItem("Save as..", file, true, KeyEvent.VK_S, true, "saveas", "/icons/disk.png", wat);
		file.addSeparator();
		exportTorXakis = addMenuItem("Export to TorXakis(.txs)", file, true, -1, false, WindowActionListener.EXPORT_TORXAKIS, null, wat);
		file.addSeparator();
		addMenuItem("Exit", file, true, KeyEvent.VK_W, false, "exit", "/icons/door_in.png", wat);
		
		//Project menu
		projectMenu = addMenu("Project");
		addProc = generateMenuItem("New proc definition...", false, -1, false, WindowActionListener.ADD_PROC, null, wat);
		addModel = generateMenuItem("New Model...", false, -1, false, WindowActionListener.ADD_MODEL, null, wat);
		fillProjectMenu(projectMenu, null);
		
		//Edit menu
		JMenu edit = addMenu("Edit");
		undo = addMenuItem("Undo", edit, true, KeyEvent.VK_Z, false, WindowActionListener.UNDO, "/icons/arrow_undo.png", inputListener);
		redo = addMenuItem("Redo", edit, true, KeyEvent.VK_Y, false, WindowActionListener.REDO, "/icons/arrow_redo.png", inputListener);
		edit.addSeparator();
		addMenuItem("Cut",edit, true, KeyEvent.VK_X, false, WindowActionListener.CUT, "/icons/cut.png", inputListener);
		addMenuItem("Copy", edit, true, KeyEvent.VK_C, false, WindowActionListener.COPY, "/icons/page_copy.png", inputListener);
		addMenuItem("Paste", edit, true, KeyEvent.VK_V, false, WindowActionListener.PASTE, "/icons/page_paste.png", inputListener);
		edit.addSeparator();
		JMenuItem delete = addMenuItem("Delete", edit, true, -1, false, WindowActionListener.DELETE, "/icons/cross.png", inputListener);
		
//		String vers = System.getProperty("os.name").toLowerCase();
		delete.setAccelerator(KeyStroke.getKeyStroke((Environment.OperatingSystem.equals(Environment.OS.Mac)) ? KeyEvent.VK_BACK_SPACE : KeyEvent.VK_DELETE, 0));
		addMenuItem("Select all", edit, true, KeyEvent.VK_A, false, WindowActionListener.SELECT_ALL, null, inputListener);
		edit.addSeparator();
		
		//Run menu
		JMenu run = addMenu("Run");
		addMenuItem("Run", run, true, KeyEvent.VK_R, false, WindowActionListener.RUN, "/icons/application_go.png", wat);

		//Results menu 
		JMenu results = addMenu("Results");
//		showResults = addMenuItem("Show results...", results, false, -1, false, WindowActionListener.SHOW_RESULTS, null, wat);
//		results.addSeparator();
//		exportHTML = addMenuItem("Export to HTML...", results, false, -1, false, WindowActionListener.EXPORT_HTML,null, wat);
//		exportEXCEL = addMenuItem("Export to Excel...", results, false, -1, false, WindowActionListener.EXPORT_EXCEL, null, wat);
//		results.addSeparator();
		JMenu export = new JMenu("Export Model");
		results.add(export);
		exportGraphPng = addMenuItem("...to Portable Network Graphic (.png)", export, false, -1, false, WindowActionListener.EXPORT_GRAPH_PNG, null, wat);
		exportGraphSvg = addMenuItem("...to Scalable Vector Graphic (.svg)", export, false, -1, false, WindowActionListener.EXPORT_GRAPH_SVG, null, wat);
		exportGraphJpg = addMenuItem("...to JPEG (.jpeg)", export, false, -1, false, WindowActionListener.EXPORT_GRAPH_JPG, null, wat);
		
		
		//View menu
		JMenu view = addMenu("View");
		addMenuItem("Zoom In", view, true, KeyEvent.VK_ADD, false, WindowActionListener.ZOOM_IN, "/icons/magnifier_zoom_in.png", wat);
		addMenuItem("Reset Zoom", view, true, KeyEvent.VK_NUMPAD0, false, WindowActionListener.ZOOM_RESET, "/icons/magnifier.png", wat);
		addMenuItem("Zoom Out", view, true, KeyEvent.VK_MINUS, false, WindowActionListener.ZOOM_OUT, "/icons/magnifier_zoom_out.png", wat);
//		showGrid = addCheckBoxMenuItem("Show Grid", view, true,WindowActionListener.SHOW_GRID,wat);
	
		//Help menu
//		JMenu help = addMenu("Help");
//		addMenuItem("Check for Updates", help, true, -1, false, WindowActionListener.CHECK_UPDATES, null, wat);
//		help.addSeparator();
//		addMenuItem("Help", help, true, -1, false, WindowActionListener.HELP, "/icons/help.png", wat);
//		addMenuItem("Upgrade license...", help, true, -1, false, WindowActionListener.UPGRADE_LICENSE, null, wat);
//		addMenuItem("About", help, true, -1, false, WindowActionListener.ABOUT, "/icons/information.png", wat);
	}
	
	private JMenu addMenu(String name){
		JMenu temp = new JMenu(name);
		add(temp);
		return temp;
	}
	
	private JMenuItem addMenuItem(String name, JMenu menu, boolean enabled, int keyEvent, boolean shiftMask, String cmd, String iconPath, ActionListener al){
		JMenuItem menuItem = generateMenuItem(name, enabled, keyEvent, shiftMask, cmd, iconPath, al);
		menu.add(menuItem);	
		return menuItem;
	}

	private JMenuItem generateMenuItem(String name, boolean enabled, int keyEvent, boolean shiftMask, String cmd, String iconPath, ActionListener al) {
		JMenuItem menuItem = new JMenuItem(name);
		if(keyEvent != -1){
			if(shiftMask){
				menuItem.setAccelerator(KeyStroke.getKeyStroke(keyEvent, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | InputEvent.SHIFT_MASK));
			}else{
				menuItem.setAccelerator(KeyStroke.getKeyStroke(keyEvent,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			}		
		}
		if(iconPath != null)menuItem.setIcon(new ImageIcon(Window.class.getResource(iconPath)));
		menuItem.setActionCommand(cmd);
		menuItem.addActionListener(al);
		menuItem.setEnabled(enabled);
		return menuItem;
	}
	
	private JCheckBoxMenuItem addCheckBoxMenuItem(String name, JMenu menu, boolean state, String cmd, ActionListener al){
		JCheckBoxMenuItem temp = new JCheckBoxMenuItem(name);
		temp.setState(true);
		temp.setActionCommand("showgrid");
		temp.addActionListener(al);
		menu.add(temp);
		return temp;
	}

	private void fillProjectMenu(JMenu projectMenu, Project project) {
		projectMenu.removeAll();
		
		if(project != null) {
			projectMenu.add(addModel);
//			projectMenu.add(addProc);
			projectMenu.addSeparator();

			System.err.println("Size:"+project.getModels().size());
			for(Model m: project.getModels()) {
				JMenu modelMenu = new JMenu();
				modelMenu.setText(m.getName());

				JMenuItem showModelItem = new JMenuItem();
				modelMenu.add(showModelItem);
				showModelItem.setAction(wat.getShowModelAction(m));
				showModelItem.setText("Show");
				JMenuItem renameModelItem = new JMenuItem();
				modelMenu.add(renameModelItem);
				renameModelItem.setAction(wat.getRenameModelAction(m));
				renameModelItem.setText("Rename");
				JMenuItem deleteModelItem = new JMenuItem();
				modelMenu.add(deleteModelItem);
				deleteModelItem.setAction(wat.getDeleteModelAction(m));
				deleteModelItem.setText("Delete");
//				JMenuItem duplicateModelItem = new JMenuItem();
//				modelMenu.add(duplicateModelItem);
//				duplicateModelItem.setAction(wat.getDuplicateModelAction(m));
//				duplicateModelItem.setText("Duplicate");
				projectMenu.add(modelMenu);
			}

		}	
	
	}

	
	public void setItemEnabled(String name,boolean b) {
		switch(name){
		case WindowActionListener.CLEAR_DATA:
			clearData.setEnabled(b);
			break;
		case WindowActionListener.IMPORT_DATA:
			importData.setEnabled(b);
			break;
		case WindowActionListener.ADD_MODEL:
			addModel.setEnabled(b);
			break;
		case WindowActionListener.UNDO:
			undo.setEnabled(b);
			break;
		case WindowActionListener.REDO:
			redo.setEnabled(b);
			break;
		case WindowActionListener.SHOW_GRID:
//			showGrid.setState(true);
			break;
		case WindowActionListener.EXPORT_TORXAKIS:
			exportTorXakis.setEnabled(b);
			break;
		case WindowActionListener.EXPORT_GRAPH_PNG: // intended fall-through
		case WindowActionListener.EXPORT_GRAPH_SVG: // intended fall-through
		case WindowActionListener.EXPORT_GRAPH_JPG:
			exportGraphPng.setEnabled(b);
			exportGraphSvg.setEnabled(b);
			exportGraphJpg.setEnabled(b);
			break;
		}
	}

	public void updateDefinitions(Project p) {
		fillProjectMenu(projectMenu, p);
	}
	
}

package gui.control;

import gui.dialogs.GraphExportDialog;
import gui.dialogs.NewFileDialog;
import gui.dialogs.OpenDialog;
import gui.dialogs.RunDialog;
import gui.dialogs.SaveAsDialog;
import gui.dialogs.TorXakisExportDialog;
import gui.draw.GraphInterface;
import gui.window.Window;
import io.file.FileType;
import io.file.FileTypeAssociation;
import io.file.project.importer.ProjectImporter;
import model.Model;
import model.Project;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;




import java.io.IOException;

import javax.swing.AbstractAction;

import core.Session;

public class WindowActionListener implements ActionListener, ComponentListener {
	private DrawController dc;
	private GraphInterface gi;
	private Window w;
	
	public static final String 
			NEW = "new",
			OPEN = "open",
			SAVE = "save",
			SAVE_AS = "saveas",
			IMPORT_DATA = "importdata",
			CLEAR_DATA = "cleardata",
			EXIT = "exit",
			ZOOM_IN = "zoomin",
			ZOOM_OUT = "zoomout",
			ZOOM_RESET = "zoomreset",
			SHOW_GRID = "showgrid",
			UNDO = "undo",
			REDO = "redo",
			CUT = "cut",
			COPY = "copy",
			PASTE = "paste",
			DELETE = "delete",
			SELECT_ALL = "selectall",
			ADD_MODEL = "addModel",
			ADD_PROC = "addProc",
			RELOAD = "reload",
			RUN = "run",
			EXPORT_GRAPH_PNG = "exportgraphpng",
			EXPORT_GRAPH_SVG = "exportgraphsvg",
			EXPORT_GRAPH_JPG = "exportgraphjpg",
			EXPORT_TORXAKIS = "exporttorxakis",
			HELP = "help",
			ABOUT = "about";
	
	public WindowActionListener(Window w) {
		this.w = w;
	}
	
	public void setDrawController(DrawController dc) {
		this.dc = dc;
		this.gi = dc.getGraphInterface();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		final Window window = Session.getSession().getWindow();
		switch (cmd) {
		case NEW:
			new NewFileDialog(w);
			break;
		case OPEN:
			new OpenDialog(w,false);	
			break;
		case RELOAD:
			ProjectImporter importer = (ProjectImporter) FileTypeAssociation.ProjectImport.getDefaultFileType().getImporter();
			try {
				Session.getSession().setProject(importer.importProject(Session.getSession().getProject().getPath()));
			} catch (Exception e1) {
			}
			break;
		case SAVE:
			final Project p = Session.getSession().getProject();
			(new Thread() {
							@Override
							public void run() {
								p.save();	
								window.setTitle(p);
							}
						}).start();
			break;
		case SAVE_AS:
			new SaveAsDialog(window);
			break;
		case EXIT:
			(new Thread(new Runnable() {
				@Override
				public void run() {
					Session.getSession().shutdownProcedure();						
				}
			})).start();
			break;
		case RUN:
			RunDialog rd = new RunDialog(Session.getSession().getSettings());
			rd.setVisible(true);
			break;
		case ADD_MODEL:
			window.addModel();
			break;
		case EXPORT_GRAPH_PNG:
			new GraphExportDialog(window, FileTypeAssociation.PngExport, (Model) window.getCurrentDefinition());
			break;
		case EXPORT_GRAPH_SVG:
			new GraphExportDialog(window, FileTypeAssociation.SvgExport, (Model) window.getCurrentDefinition());
			break;
		case EXPORT_GRAPH_JPG:
			new GraphExportDialog(window, FileTypeAssociation.JpgExport, (Model) window.getCurrentDefinition());
			break;
		case EXPORT_TORXAKIS:
			new TorXakisExportDialog(window, FileTypeAssociation.TorXakisExport, window.getCurrentProject());
		case ZOOM_IN:
			dc.zoomIn();
			w.getStatusBar().setZoomFactor(dc.getZoom());
			break;
		case ZOOM_RESET:
			dc.zoomReset();
			w.getStatusBar().setZoomFactor(dc.getZoom());
			break;
		case ZOOM_OUT:
			dc.zoomOut();
			w.getStatusBar().setZoomFactor(dc.getZoom());
			break;
		case SHOW_GRID:
//			JCheckBoxMenuItem i = (JCheckBoxMenuItem) e.getSource();
//			Session.getSession().getSettings().setBooleanSetting(SettingsValue.view_grid, i.getState());
//			dc.setViewGrid(i.getState());
			break;
		default:
			System.err.println("ActionCommand that we don't use!");
		}

	}
	
	public AbstractAction getShowModelAction(final Model m) {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				w.showDefinition(m);
			}
		};
	}

	public AbstractAction getRenameModelAction(final Model m) {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				w.renameModel(m);
			}
		};
	}

	public AbstractAction getDuplicateModelAction(final Model m) {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				w.duplicateModel(m);
			}
		};
	}

	public AbstractAction getDeleteModelAction(final Model m) {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				w.deleteModel(m);
			}
		};
	}

	@Override
	public void componentResized(ComponentEvent e) {
		if(dc == null || dc.getViewBoxController() == null) return;
		dc.getViewBoxController().onPanelResize();
	}

	@Override
	public void componentMoved(ComponentEvent e) {
	}

	@Override
	public void componentShown(ComponentEvent e) {
	}

	@Override
	public void componentHidden(ComponentEvent e) {
	}
}

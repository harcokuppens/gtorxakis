package gui.control;

import gui.dialogs.GraphExportDialog;
import gui.dialogs.NewFileDialog;
import gui.dialogs.OpenDialog;
import gui.dialogs.SaveAsDialog;
import gui.draw.GraphInterface;
import gui.window.Window;
import io.file.FileTypeAssociation;
import model.Model;
import model.Project;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;


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
			SETTINGS = "settings",
			ADD_MODEL = "addModel",
			RUN = "run",
			SHOW_RESULTS = "showresults",
			EXPORT_HTML = "exporthtml",
			EXPORT_EXCEL = "exportexcel",
			EXPORT_GRAPH_PNG = "exportgraphpng",
			EXPORT_GRAPH_SVG = "exportgraphsvg",
			EXPORT_GRAPH_JPG = "exportgraphjpg",
			CHECK_UPDATES = "checkUpdates",
			HELP = "help",
			UPGRADE_LICENSE = "upgradeLicense",
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
//		JComponent item = (JComponent) e.getSource();
		final Window window = Session.getSession().getWindow();
		switch (cmd) {
		case NEW:
			new NewFileDialog(w);
			break;
		case OPEN:
			new OpenDialog(w,false);		
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
		case SETTINGS: 
//			SettingsDialog sd = new SettingsDialog(window);
//			sd.setVisible(true);
			break;
		case RUN:
//			runAction(window, window.getCurrentProject());
			break;
		case ADD_MODEL:
//			window.addModel();
			break;
		case SHOW_RESULTS:
//			showResultsAction(window.getCurrentModel());
			break;
		case EXPORT_HTML:
//			ResultExportDialogHTML redh = new ResultExportDialogHTML(window, window.getCurrentModel());
			break;
		case EXPORT_EXCEL:
//			ResultExportDialogExcel rede = new ResultExportDialogExcel(window, window.getCurrentModel());
			break;
		case EXPORT_GRAPH_PNG:
			new GraphExportDialog(window, FileTypeAssociation.PngExport, window.getCurrentModel());
			break;
		case EXPORT_GRAPH_SVG:
			new GraphExportDialog(window, FileTypeAssociation.SvgExport, window.getCurrentModel());
			break;
		case EXPORT_GRAPH_JPG:
			new GraphExportDialog(window, FileTypeAssociation.JpgExport, window.getCurrentModel());
			break;
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
		case ABOUT:
//			AboutDialog ad = new AboutDialog(window);
//			ad.setVisible(true);
			break;
		default:
			System.err.println("ActionCommand that we don't use!");
		}

	}
	
	public AbstractAction getShowModelAction(final Model m) {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				w.showModel(m);
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

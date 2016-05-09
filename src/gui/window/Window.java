package gui.window;

import gui.control.DrawController;
import gui.control.InputListener;
import gui.control.WindowActionListener;
import gui.draw.GraphPanel;
import gui.draw.GUITextualDefinition;
import gui.draw.DrawableGraph;

import gui.window.StatusBarMessageHandler;


import java.util.HashMap;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import util.Environment;
import util.CalculationDispatcher;
import model.Definition;
import model.Model;
import model.Project;
import model.graph.Graph;
import model.TextualDefinition;
import core.Session;

import javax.swing.SwingUtilities;

public class Window extends JFrame implements Observer {
	private Window window;
	
	// Bi-directional map
	private HashMap<Model, Object> panels;
	private HashMap<Object, Model> definitions;
	// The model that is currently visible
	private Definition currentDefinition;
	private Project currentProject;
	
	private WindowMenuBar menuBar;
	private StatusBar statusBar;
	private final StatusBarMessageHandler statusBarMessageHandler;
	private WindowToolBar toolBar;
	
	private JTabbedPane definitionPane;

	private final WindowActionListener wat;
	private final InputListener inputListener;
	private DrawController dc;

	private final Session session;

	private final CalculationDispatcher calculationDispatcher;
	
	public Window(Session session) {
		window = this;
		this.session = session;
		this.panels = new HashMap<Model, Object>();
		this.definitions = new HashMap<Object, Model>();
		window.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				Window.this.session.shutdownProcedure();
			}
		});
		
		wat = new WindowActionListener(this);
		
		inputListener = new InputListener(this);
		
		//set close operation to exit
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		//force use of preferred layouts (for example screenmenubar on macs)
		pack();
		
		//set window size & toggle full screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int w = 800, h = 600;
		this.setSize(w, h);
		Point screenCenter = new Point(screenSize.width / 2, screenSize.height / 2);
		setLocation(screenCenter.x - getSize().width / 2, screenCenter.y - getSize().height / 2);
		setExtendedState(Frame.MAXIMIZED_BOTH);
		
		//set titlebar
//		setIconImage(Toolkit.getDefaultToolkit().getImage(Window.class.getResource("/logo/adanco-icon.gif")));
		setTitle(Session.PROGRAM_NAME);
				
		//set menubar
		menuBar = new WindowMenuBar(wat, inputListener);
		setJMenuBar(menuBar);

		//set toolbar
		toolBar = new WindowToolBar(wat, inputListener);
		toolBar.setFloatable(false);
//		getContentPane().add(toolBar, BorderLayout.NORTH);
		
		definitionPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		definitionPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JTabbedPane pane = (JTabbedPane) e.getSource();
				Definition newDefinition = definitions.get(pane.getSelectedComponent());
				Window.this.updateDefinition(currentDefinition, newDefinition);
			}
		});
		this.getContentPane().add(definitionPane, BorderLayout.CENTER);


		//StatusBar
		statusBar = new StatusBar(this);
		getContentPane().add(statusBar, BorderLayout.SOUTH);
		statusBar.setMessage(StatusBar.DEFAULT_MESSAGE);	

		//Initialize StatusbarMessage-Handler
		this.statusBarMessageHandler = new StatusBarMessageHandler(statusBar);
		
		this.setMinimumSize(new Dimension(600,600));

		calculationDispatcher = new CalculationDispatcher();
		calculationDispatcher.start();
		
	}
	
	public WindowMenuBar getWindowMenuBar(){
		return menuBar;
	}

	public StatusBarMessageHandler getStatusBarMessageHandler() {
		return this.statusBarMessageHandler;
	}

	public void addModel(){
		Graph g = new Graph();
		DrawableGraph dg = new DrawableGraph();
		String modelBaseName = "Model "; // Note the included space.
		int count = 1;
		ArrayList<String> otherModelNames = new ArrayList<String>();
		for(Model m: currentProject.getModels()) {
			String n = m.getName().trim();
			if(n.startsWith(modelBaseName)) {
				try {
					// cut off the modelBaseName at the beginning.
					String rem = n.substring(modelBaseName.length()).trim();
					int modelIndex = Integer.valueOf(rem);
					if(modelIndex >= count) {
						count = modelIndex + 1;
					}
				} catch(NumberFormatException e) {
					// This happens when we try to cast something like
					// "Model A" or "Model 1_new". But we don't care about
					// these anyway. -- Mo
					continue;
				}
			}
		}
		String newModelName = modelBaseName + count;
		Model m = new Model(currentProject, newModelName, g, dg);
		currentProject.addDefinition(m);
		setTitle(currentProject);
		showModel(m);
	}

	public void showDefinition(Definition d){
		if(d instanceof Model){
			showModel((Model) d);
		}else{
			showTextualDefinition(d);
		}
		
	}
	
	private void showTextualDefinition(Definition d){
		GUITextualDefinition gui = new GUITextualDefinition(d);
		((TextualDefinition) d).setDrawable(gui);
		definitionPane.add(d.getTitle(), gui);
	}
	
	private void showModel(Model m) {
		if(panels.containsKey(m)) {
			// then the model is currently being displayed
			GraphPanel panel = (GraphPanel) panels.get(m);
			definitionPane.setSelectedComponent(panel);
			updateDefinition(currentDefinition, m);
		} else {
			GraphPanel g = new GraphPanel();
			g.setAlignmentX(CENTER_ALIGNMENT);
			g.setAlignmentY(CENTER_ALIGNMENT);

			panels.put(m, g);
			definitions.put(g,  m);
			g.setModel(m);
			m.getDrawController().attach(g);

			this.definitionPane.addTab(m.getName(), null, g, null);
			definitionPane.setTabComponentAt(this.definitionPane.getTabCount()-1, new ButtonTabComponent(definitionPane) {
				@Override
				public void onButtonPressed(JTabbedPane pane, int index) {
					hideModel(index);
				}
			});
			definitionPane.setSelectedIndex(this.definitionPane.getTabCount()-1);
			updateDefinition(currentDefinition, m);
			menuBar.updateModels(currentProject);
		}
	}

	public void renameModel(Model m) {
		String oldName = m.getName();
		boolean retry;
		do {
			retry = false;
			String s = (String) JOptionPane.showInputDialog(
				this, // parent component
				"Rename \"" + oldName + "\":", // message
				"", // title
				JOptionPane.PLAIN_MESSAGE, // message type
				null, // icon
				null, // set of options
				oldName); // default value
			System.out.println("New name: " + s);
			if(s == null || s.trim().isEmpty() || s.trim().equals(oldName)) {
				return;
			} else {
				s = s.trim();
				for (Model model : m.getProject().getModels()) {
					if(model.getName().equals(s)) {
						JOptionPane.showMessageDialog(
							this, // parent component
							"This project already contains a model with this name. Please choose a different name.", // message
							"", // title
							JOptionPane.PLAIN_MESSAGE, // messageType
							null); // icon
						retry = true;
					}
				}
				if(!retry) {
					m.setName(s);
					// Check if the tab title needs to be changed:
					GraphPanel panel = (GraphPanel) panels.get(m);
					final int index = definitionPane.indexOfComponent(panel);
					if(index != -1) {
						final String finalName = s;
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								definitionPane.setTitleAt(index, finalName);
								definitionPane.updateUI();
							}
						});
					}
				}
			}
		} while(retry);
		// update menu bar entries:
		menuBar.updateModels(currentProject);
	}

	public void deleteModel(Model m) {
		int choice = JOptionPane.showConfirmDialog(
			this, //parentComponent,
			"Are you sure that model \"" + m.getName() + "\" should be deleted?", //message
			"", //title
			JOptionPane.YES_NO_OPTION, //optionType
			JOptionPane.PLAIN_MESSAGE, //messageType
			null); //icon
		if(choice == JOptionPane.YES_OPTION) {
			currentProject.removeDefinition(m);
			// Check if deleted model is currently being displayed,
			// in which case the corresponding panel needs to be closed
			GraphPanel panel = (GraphPanel) panels.get(m);
			int index = definitionPane.indexOfComponent(panel);
			if(index != -1) {
				hideModel(index);
			}
			menuBar.updateModels(currentProject);
			setTitle(currentProject);
		}
	}

	public void duplicateModel(Model m) {
		Project p = this.currentProject;
		Graph g = new Graph();
		DrawableGraph dg = new DrawableGraph();
		Model mClone = m.clone(p, "Copy of " + m.getName(), g, dg);
		p.addDefinition(mClone);
		showModel(mClone);
		setTitle(currentProject);
	}

	public Definition getCurrentDefinition() {
		return currentDefinition;
	}

	public Project getCurrentProject() {
		return this.currentProject;
	}

	private void removeAllModels() {
		updateDefinition(currentDefinition, null);
		definitionPane.removeAll();
//		definitionPane.addTab("SUT definition", new TextualDefinition(null));
	}

	public void hideModel(int index) {
		boolean isVisible = this.definitionPane.getSelectedIndex() == index;
		Model oldModel = definitions.get((GraphPanel) definitionPane.getComponentAt(index));
		Model newModel = null;
		if(definitionPane.getTabCount() > 1 && isVisible) {
			int newIndex = index - 1 < 0? 1:index-1;
			newModel = definitions.get((GraphPanel) definitionPane.getComponentAt(newIndex));
		} 
		definitionPane.remove(index);
		updateDefinition(oldModel, newModel);
		GraphPanel oldPanel = (GraphPanel) panels.remove(oldModel);
		oldPanel.stopProcessing();
		oldModel.getDrawController().detach();
		definitions.remove(oldPanel);
	}

	public StatusBar getStatusBar() {
		return this.statusBar;
	}
	

	private boolean confirmClearData() {
		int returnValOptionPaneModifyModel = JOptionPane
				.showConfirmDialog(
						this,
						"This will vanish all currently used indicators and you will not be able to undo this. Do you want to proceed?",
						"Clear data warning",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE);
		return returnValOptionPaneModifyModel == JOptionPane.YES_OPTION;
	}
	
	private void updateGraphView(Definition oldDefinition, Definition newDefinition) {
		if(newDefinition != null && newDefinition instanceof Model) {
			Model newModel = (Model) newDefinition;
			DrawController dc = newModel.getDrawController();
			wat.setDrawController(dc);
			inputListener.setModel(newModel);
		}
		// unbind existing associations if appropriate
		if(oldDefinition != null) {
			GraphPanel oldPanel = (GraphPanel) panels.get(oldDefinition);
			oldPanel.removeComponentListener(wat);
			oldPanel.removeMouseListener(inputListener);
			oldPanel.removeMouseMotionListener(inputListener);
			oldPanel.removeKeyListener(inputListener);
			oldPanel.removeMouseWheelListener(inputListener);
			oldPanel.setFocusTraversalKeysEnabled(false);
			if(oldDefinition instanceof Model){
				((Model) oldDefinition).getDrawController().getGraphInterface().deleteObserver(inputListener);			
			}
		}
		// bind new Model if appropriate
		if(newDefinition != null) {
			GraphPanel newPanel = (GraphPanel) panels.get(newDefinition);
			newPanel.addComponentListener(wat);
			newPanel.addMouseListener(inputListener);
			newPanel.addMouseMotionListener(inputListener);
			newPanel.addKeyListener(inputListener);
			newPanel.addMouseWheelListener(inputListener);
			newPanel.setFocusTraversalKeysEnabled(false);
			newPanel.requestFocus();
			if(newDefinition instanceof Model){
				((Model)newDefinition).getDrawController().getGraphInterface().addObserver(inputListener);
				this.dc = dc;				
			}
		} else {
			this.dc = null;
		}
		
	}
	
	public void setProject(Project oldProject, Project newProject) {
		setTitle(newProject);
		removeAllModels();
		this.currentProject = newProject;
		if(newProject != null) {
			menuBar.setItemEnabled(WindowActionListener.ADD_MODEL, true);
			for(Definition d: newProject.getDefinitions()) {
				showDefinition(d);
			}
		} else {
			menuBar.setItemEnabled(WindowActionListener.ADD_MODEL, false);
		}
	}
	
	private void updateDefinition(Definition oldDefinition, Definition newDefinition) {
		updateGraphView(oldDefinition, newDefinition);
		updateButtons(newDefinition);
		if(newDefinition instanceof Model){
			Model newModel = (Model) newDefinition;
			boolean canExport = newModel != null && !(newModel.getGraph().getStates().isEmpty() && newModel.getGraph().getComments().isEmpty());
			menuBar.setItemEnabled(WindowActionListener.EXPORT_GRAPH_PNG, canExport);
			menuBar.setItemEnabled(WindowActionListener.EXPORT_GRAPH_SVG, canExport);
			menuBar.setItemEnabled(WindowActionListener.EXPORT_GRAPH_JPG, canExport);
		}
		this.currentDefinition = newDefinition;
	}
	
	public void setTitle(Project project) {
		String title = Session.PROGRAM_NAME + " - " + project.getPath();
		if(Environment.OperatingSystem == Environment.OS.Mac) {
			JRootPane root = this.getRootPane();
			root.putClientProperty("Window.documentModified", !project.isSaved());
		} else {
			title = title + (!project.isSaved()?" * ":"");
		}
		super.setTitle(title);
	}
	
	private void updateButtons(Definition d) {
		menuBar.setItemEnabled(WindowActionListener.SHOW_GRID, true);
			if(d != null) {
				if(d instanceof Model){
					Model m = (Model) d;
					boolean canExport = !(m.getGraph().getStates().isEmpty() && m.getGraph().getComments().isEmpty());
					menuBar.setItemEnabled(WindowActionListener.EXPORT_GRAPH_PNG, canExport);
					menuBar.setItemEnabled(WindowActionListener.EXPORT_GRAPH_SVG, canExport);
					menuBar.setItemEnabled(WindowActionListener.EXPORT_GRAPH_JPG, canExport);
				}
				menuBar.setItemEnabled(WindowActionListener.REDO, d.canRedo());
				menuBar.setItemEnabled(WindowActionListener.UNDO, d.canUndo());
				toolBar.setItemEnabled(WindowActionListener.UNDO, d.canUndo());
				toolBar.setItemEnabled(WindowActionListener.REDO, d.canRedo());
			} else {
				menuBar.setItemEnabled(WindowActionListener.EXPORT_GRAPH_PNG, false);
				menuBar.setItemEnabled(WindowActionListener.EXPORT_GRAPH_SVG, false);
				menuBar.setItemEnabled(WindowActionListener.EXPORT_GRAPH_JPG, false);
				menuBar.setItemEnabled(WindowActionListener.REDO, false);
				menuBar.setItemEnabled(WindowActionListener.UNDO, false);
				toolBar.setItemEnabled(WindowActionListener.UNDO, false);
				toolBar.setItemEnabled(WindowActionListener.REDO, false);
			}
	}
	
	public void hasResults(boolean hasResults) {
		menuBar.setItemEnabled(WindowActionListener.SHOW_RESULTS, hasResults);
		menuBar.setItemEnabled(WindowActionListener.EXPORT_HTML, hasResults);
		menuBar.setItemEnabled(WindowActionListener.EXPORT_EXCEL, hasResults);
		toolBar.setItemEnabled(WindowActionListener.SHOW_RESULTS, hasResults);
	}

	@Override
	public void update(Observable observable, Object obj) {
		if(observable instanceof Session) {
			if(obj instanceof Project) {
				System.out.println("[SEVERE] No update defined for projects");
//				 updateProject(((Session) observable).getProject());				
			} else if(obj == null) {
				setTitle(Session.getSession().getProject());
			}
		}
		updateButtons(currentDefinition);		
	}
	
	public Collection<Integer> castToCollection(int[] varNames){
		Collection<Integer> cs = new ArrayList<Integer>();
		for(int i = 0; i < varNames.length;i++){
			cs.add((Integer)varNames[i]);
		}
		return cs;
	}
	

	
	public boolean confirmClose() {
		if(session.getProject() == null) return true;
		if(session.getProject().isSaved()) return true;
		int returnValue = JOptionPane.showConfirmDialog(
				window,
				"There are unsaved changes in your project. Do you want to save the current project now?",
				"Warning",
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.WARNING_MESSAGE
		);
		if(returnValue == JOptionPane.YES_OPTION) {
//			Session.getSession().getProject().save();
		}
		return !(returnValue == JOptionPane.CANCEL_OPTION);
	}

	/**
	 * Shows a dialog that lets a user define how existing indicators should be
	 * replaced by new indicators.
	 * This dialog is shown when the user imports a new dataset, but not all
	 * existing indicators can be mapped to corresponding indicators in the new dataset.
	 * @param  oldIndicators The names of all existing indicators that cannot be mapped to new indicators
	 * @param  newIndicators The names of all indicators in the new dataset that have not yet been mapped to other existing indicators
	 * @return               A list {@code l} of integers that define the mapping between the two lists.
	 *                       If {@code l[3] = 2}, then the old indicator at index 3 will be replaced by the new indicator at index 2.
	 *                       If {@code l[2] = -1}, then the old indicator at index 2 will be deleted.
	 */
//	private int[] showIndicatorSubstitutionDialog(ArrayList<GraphIndicator> oldIndicators, ArrayList<GraphIndicator> newIndicators) {
//		JLabel infoLabel = new JLabel(
//			"<html><p>For some existing indicators, no corresponding data column could be found in the new data file. How would you like to proceed?</</html>");
//
//		IndicatorSubstitutionPanel indicatorSubstitutionPanel = new IndicatorSubstitutionPanel(oldIndicators, newIndicators, this);
// 
//        JOptionPane mappingOptionPane = new JOptionPane(
//			new Object[] {infoLabel, indicatorSubstitutionPanel},
//			JOptionPane.PLAIN_MESSAGE,
//			JOptionPane.OK_CANCEL_OPTION);
//		JDialog mappingDialog = mappingOptionPane.createDialog(this, "Replace Indicators");
//		infoLabel.setSize(new Dimension(
//			indicatorSubstitutionPanel.getPreferredSize().width,
//			30));
//		infoLabel.setPreferredSize(new Dimension(
//			indicatorSubstitutionPanel.getPreferredSize().width,
//			30));
//		mappingDialog.pack();
//		mappingDialog.show();
//		Object value = mappingOptionPane.getValue();
//		if(value == null || (int) value != JOptionPane.YES_OPTION) {
//			return null;
//		} else {
//			return indicatorSubstitutionPanel.getMapping();
//		}
//	}

	public Session getSession() {
		return this.session;
	}
}
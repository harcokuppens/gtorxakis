package gui.window;

import gui.control.Selectable;
import gui.dialogs.ChangeCommentParagraphsDialog;
import gui.dialogs.ChangeNameDialogEdge;
import gui.dialogs.ChangeNameDialogState;
import gui.draw.DrawableComment;
import gui.draw.DrawableComment.CommentType;
import gui.draw.DrawableGraphEdge;
import gui.draw.DrawableGraphState;
import gui.draw.GraphInterface;
import gui.draw.TransferableGraphElements;

import java.awt.Component;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import model.Model;
import model.graph.GraphEdge;
import model.graph.GraphState;
import action.Action;
import action.SetConfigAction;
import core.Session;

public class GraphContextMenu {
	private ArrayList<Selectable> selectable;
	private Point p;
	private ContextMenu contextMenu;
	
	private GraphInterface gi;
	private Model model;

	private enum MenuType{
		STATES,
		EDGES,
		COMMENTS,
		EMPTY,
		OTHER;
	}
	
	public GraphContextMenu(ArrayList<Selectable> selectable, Point p, Model model){
		this.selectable = new ArrayList<Selectable>(selectable);
		this.p = p;
		this.gi = model.getGraphInterface();
		this.model = model;
		contextMenu = getContextMenu();
	}
	
	private ContextMenu getContextMenu(){
		MenuType temp = getMenuType();
		switch(temp){
		case STATES:
			return new StatesContextMenu();
		case EDGES:
			return new EdgesContextMenu();
		case COMMENTS:
			return new CommentsContextMenu();
		case EMPTY:
		case OTHER:
		default:
			return new DefaultContextMenu();
		}
	}
	
	private MenuType getMenuType(){
		if(selectable.size() == 0){
			return MenuType.EMPTY;
		}
		if(containsOnly(DrawableGraphState.class)){
			return MenuType.STATES;
		}
		if(containsOnly(DrawableGraphEdge.class)){
			return MenuType.EDGES;
		}
		if(containsOnly(DrawableComment.class)){
			return MenuType.COMMENTS;
		}
		return MenuType.OTHER;
	}
	
	private boolean containsOnly(Class<?> c){
		for(Selectable s : selectable){
			if(!c.isInstance(s)){
				return false;
			}
		}
		return true;
	}

	public ArrayList<GraphState> castSelectableToGraphState(ArrayList<Selectable> selectables){
		ArrayList<GraphState> nodes = new ArrayList<GraphState>();
		for(Selectable s : selectables){
			nodes.add(((DrawableGraphState) s).getState());
		}
		return nodes;
	}
	
	public GraphEdge[] castSelectableToGraphEdge(ArrayList<Selectable> selectables){
		ArrayList<GraphEdge> edges = new ArrayList<GraphEdge>();
		for(Selectable s : selectables){
			edges.add(((DrawableGraphEdge) s).getEdge());
		}
		return edges.toArray(new GraphEdge[edges.size()]);
	}
	
	private abstract class ContextMenu extends JPopupMenu implements ActionListener{
		protected JMenuItem addComment, 
							addHeadline, 
							changeName, 
							cut, 
							copy, 
							paste, 
							delete;
		protected JCheckBoxMenuItem startState;
		
		public static final String ADD_COMMENT = "Add comment",
				   ADD_HEADLINE = "Add title",
				   RENAME = "Rename",
				   EDIT_TEXT = "Edit text",
				   EDIT_TRANSITIONS = "Edit transitions",
				   CUT = "Cut",
				   COPY = "Copy",
				   PASTE = "Paste",
				   DELETE = "Delete",
				   START_STATE = "Set start state";
		
		public ContextMenu(){
			init();
			initializeMenuEntries();
		}
		
		public abstract void init();
		public abstract void initializeMenuEntries();
		
		protected JMenuItem createMenuEntry(JMenuItem item, String name, String iconPath){
			return createMenuEntry(item, name, name, iconPath);
		}
		
		protected JMenuItem createMenuEntry(JMenuItem item, String name, String cmd, String iconPath){
			item = new JMenuItem(name);
			if(iconPath != null) item.setIcon(new ImageIcon(Window.class.getResource(iconPath)));
			item.setActionCommand(cmd);
			item.addActionListener(this);
			return item;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			ArrayList<Selectable> selectables = gi.getSelection();
			Selectable selectedItem;
			
			switch(cmd){
			case ADD_COMMENT:
				gi.addCommentEvent((int) p.getX(), (int) p.getY(), CommentType.COMMENT);
				break;
			case ADD_HEADLINE:
				gi.addCommentEvent((int) p.getX(), (int) p.getY(), CommentType.HEADLINE);
				break;
			case RENAME :
				selectedItem = selectables.get(0);
				if(selectables.size() == 1){
					if(selectedItem instanceof DrawableGraphState){
						System.out.println("[GraphContextMenu] Rename construct");
						ChangeNameDialogState cnd = new ChangeNameDialogState(model, selectedItem, Session.getSession().getWindow());
						cnd.setVisible(true);
					}else if(selectedItem instanceof DrawableGraphEdge){
						System.out.println("[GraphContextMenu] Rename edge");
						ChangeNameDialogEdge cnd = new ChangeNameDialogEdge(model, selectedItem, Session.getSession().getWindow());
						cnd.setVisible(true);
					}
				}
				break;
			case EDIT_TEXT:
				selectedItem = selectables.get(0);
				if(selectables.size() == 1){
					if(selectedItem instanceof DrawableComment){
						ChangeCommentParagraphsDialog ccpd = new ChangeCommentParagraphsDialog(model, (DrawableComment) selectedItem, Session.getSession().getWindow());
						ccpd.setVisible(true);
					}
				}
				break;
			case CUT :
				gi.cutEvent();
				break;
			case COPY :
				gi.copyEvent();
				break;
			case PASTE :
				gi.pasteEvent(p.x,p.y);
				break;
			case DELETE:
				gi.deleteEvent();
				break;
			case START_STATE:
				startStateAction();
				break;
			}
		}
	}
	
	private void startStateAction(){
		GraphState newState = ((DrawableGraphState) selectable.get(0)).getState();
		GraphState oldState = model.getGraph().getStartState();
		
		if(newState.equals(oldState)){
			return;
		}
		
		GraphState[] objects1 = new GraphState[]{newState, oldState};
		boolean[] values = new boolean[]{true, false};
		
		String[] command1 = new String[]{GraphState.ATTRIBUTE_START_STATE};
		Object[][] oldValues1 = new Object[1][objects1.length];
		for(int i = 0; i < objects1.length; i++) {
			if(objects1[i] == null) 
				oldValues1[0][i] = null;
			else{
				System.out.println(objects1[i].getAttribute(GraphState.ATTRIBUTE_START_STATE));
				oldValues1[0][i] = objects1[i].getAttribute(GraphState.ATTRIBUTE_START_STATE);
			}
		}
		Object[][] newValues1 = new Object[1][objects1.length];
		for(int i = 0; i < objects1.length; i++) {
			newValues1[0][i] = values[i];
		}
		Action a5 = new SetConfigAction(objects1, command1, oldValues1);
		Action a6 = new SetConfigAction(objects1, command1, newValues1);
		model.performAction(a6, a5);
	}
		
	public void show(Component component, int x, int y) {
		contextMenu.show(component, x, y);
	}
	
	private class StatesContextMenu extends ContextMenu {
		
		public StatesContextMenu(){
			super();
		}
		
		@Override
		public void init(){
			changeName = createMenuEntry(changeName, RENAME, "/icons/pencil.png");
			cut = createMenuEntry(cut, CUT, "/icons/cut.png");
			copy = createMenuEntry(copy, COPY, "/icons/page_copy.png");
			paste = createMenuEntry(paste, PASTE, "/icons/page_paste.png");
			delete = createMenuEntry(delete, DELETE, "/icons/cross.png");
			
			boolean isStartState = false;
			if(selectable.size() == 1){
				isStartState = (boolean) castSelectableToGraphState(selectable).get(0).getAttribute(GraphState.ATTRIBUTE_START_STATE); 
			}

			startState = new JCheckBoxMenuItem(START_STATE);
			startState.setActionCommand(START_STATE);
			startState.addActionListener(this);
			startState.setState(isStartState);
			
			add(changeName);
			addSeparator();
			add(startState);
			addSeparator();
			add(cut);
			add(copy);
			add(paste);
			add(delete);
		}

		@Override
		public void initializeMenuEntries() {
			changeName.setEnabled(selectable.size() == 1);
			startState.setEnabled(selectable.size() == 1);
			final DataFlavor df = TransferableGraphElements.df;
			final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			paste.setEnabled(clipboard.isDataFlavorAvailable(df));
		}
		
	}
	
	private class CommentsContextMenu extends ContextMenu{
		
		public CommentsContextMenu(){
			super();
		}
		
		@Override
		public void init(){
			changeName = createMenuEntry(changeName, EDIT_TEXT, "/icons/pencil.png");
			cut = createMenuEntry(cut, CUT, "/icons/cut.png");
			copy = createMenuEntry(copy, COPY, "/icons/page_copy.png");
			paste = createMenuEntry(paste, PASTE, "/icons/page_paste.png");
			delete = createMenuEntry(delete, DELETE, "/icons/cross.png");
			
			add(changeName);
			addSeparator();
			add(cut);
			add(copy);
			add(paste);
			add(delete);
		}

		@Override
		public void initializeMenuEntries() {
			DrawableComment c = null;
			if(selectable.size() > 0){
				c = (DrawableComment) selectable.get(0);
			}
			boolean hasEdge = c != null && c.getGraphComment().getEdge() != null;
			changeName.setEnabled(selectable.size() == 1 && !hasEdge);
			final DataFlavor df = TransferableGraphElements.df;
			final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			paste.setEnabled(clipboard.isDataFlavorAvailable(df));
		}
		
	}
	
	private class EdgesContextMenu extends ContextMenu{
		
		public EdgesContextMenu(){
			super();
		}
		
		@Override
		public void init(){
			changeName = createMenuEntry(changeName, EDIT_TRANSITIONS, RENAME, "/icons/pencil.png");
			delete = createMenuEntry(delete, DELETE, "/icons/cross.png");
			
			add(changeName);
			addSeparator();
			add(delete);
		}

		@Override
		public void initializeMenuEntries() {
			changeName.setEnabled(selectable.size() == 1);
		}
		
	}
	
	private class DefaultContextMenu extends ContextMenu{
		
		@Override
		public void init() {
			addHeadline = createMenuEntry(addHeadline, ADD_HEADLINE, null);
			addComment = createMenuEntry(addComment, ADD_COMMENT, null);
			cut = createMenuEntry(cut, CUT, "/icons/cut.png");
			copy = createMenuEntry(copy, COPY, "/icons/page_copy.png");
			paste = createMenuEntry(paste, PASTE, "/icons/page_paste.png");
			delete = createMenuEntry(delete, DELETE, "/icons/cross.png");
			
			add(addHeadline);
			add(addComment);
			addSeparator();
			add(cut);
			add(copy);
			add(paste);
			add(delete);
		}
		
		@Override
		public void initializeMenuEntries() {
			final DataFlavor df = TransferableGraphElements.df;
			final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			boolean copyEnabled = selectable.size() > 0 && (!containsOnly(DrawableGraphEdge.class));
			copy.setEnabled(copyEnabled);
			cut.setEnabled(copyEnabled);
			paste.setEnabled(clipboard.isDataFlavorAvailable(df));
		}
	}
}
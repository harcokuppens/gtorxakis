package gui.window;

import gui.control.Movable;
import gui.control.Selectable;
import gui.dialogs.ChangeCommentParagraphsDialog;
import gui.dialogs.ChangeNameDialogEdge;
import gui.dialogs.ChangeNameDialogState;
import gui.draw.DrawableComment.CommentType;
import gui.draw.DrawableGraphEdge;
import gui.draw.DrawableGraphState;
import gui.draw.DrawableComment;
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
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import model.graph.GraphEdge;
import model.graph.GraphState;
import model.Model;
import action.Action;
import action.DrawMoveAction;
import action.SetConfigAction;
import core.Session;

public class GraphContextMenu {
	private ArrayList<Selectable> selectable;
	private Point p;
	private ContextMenu contextMenu;
	
	private GraphInterface gi;
	private Model model;

	private enum MenuType{
		NODES,
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
		case NODES:
			return new NodesContextMenu();
		case EDGES:
			return new EdgesContextMenu();
		case COMMENTS:
			return new CommentsContextMenu();
		case EMPTY:
		case OTHER:
		default:
			return new DefaultContextMenu();
			//Do we have a difference between empty selection and a selection of multiple selectables? -tobi-
		}
	}
	
	private MenuType getMenuType(){
		if(selectable.size() == 0){
			return MenuType.EMPTY;
		}
		if(containsOnly(DrawableGraphState.class)){
			return MenuType.NODES;
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

	private DrawableGraphState[] castSelectableToDrawableGraphState(ArrayList<Selectable> selectables){
		ArrayList<DrawableGraphState> nodes = new ArrayList<DrawableGraphState>();
		for(Selectable s : selectables){
			nodes.add((DrawableGraphState) s);
		}
		return nodes.toArray(new DrawableGraphState[nodes.size()]);
	}
	
	public ArrayList<GraphState> castSelectableToGraphState(ArrayList<Selectable> selectables){
		ArrayList<GraphState> nodes = new ArrayList<GraphState>();
		for(Selectable s : selectables){
			nodes.add(((DrawableGraphState) s).getNode());
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
							removeName,
							changeName, 
							cut, 
							copy, 
							paste, 
							properties, 
							delete, 
							indicatorPositionLeft, 
							indicatorPositionRight;
		protected JCheckBoxMenuItem viewIndicators, dominantIndicator;
		protected JMenu indicatorMenu;
		
		public static final String ADD_COMMENT = "Add comment",
				   ADD_HEADLINE = "Add title",
				   RENAME = "Rename",
				   CHANGE_LABEL = "Change label",
				   EDIT_TEXT = "Edit text",
				   ADD_NAME = "Assign label",
				   REMOVE_NAME = "Remove label",
				   CUT = "Cut",
				   COPY = "Copy",
				   PASTE = "Paste",
				   DELETE = "Delete",
				   VIEW_INDICATORS = "Show indicators",
				   INDICATORPOSITION = "Indicator position",
				   INDICATORPOSITION_LEFT = "Left",
				   INDICATORPOSITION_RIGHT = "Right",
				   PROPERTIES = "Properties",
				   DOMINANT_INDICATOR = "Select as dominant indicator";
		
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
		
		private void removeEdgeNameAction(){
			GraphEdge[] objects1 = castSelectableToGraphEdge(selectable);
			
			String[] command1 = new String[]{GraphEdge.ATTRIBUTE_NAME};
			Object[][] oldValues1 = new String[1][objects1.length];
			for(int i = 0; i < objects1.length; i++) {
				oldValues1[0][i] = objects1[i].getAttribute(GraphEdge.ATTRIBUTE_NAME);
			}
			Object[][] newValues1 = new String[1][objects1.length];
			for(int i = 0; i < objects1.length; i++) {
				newValues1[0][i] = String.valueOf("");
			}
			Action a5 = new SetConfigAction(objects1, command1, oldValues1);
			Action a6 = new SetConfigAction(objects1, command1, newValues1);
			model.performAction(a6, a5);	
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			ArrayList<Selectable> selectables = gi.getSelection();
			Selectable selectedItem;
			
			ArrayList<GraphState> nodes;
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
			case REMOVE_NAME:
				removeEdgeNameAction();
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
			case PROPERTIES :
				nodes  = castSelectableToGraphState(selectable);
//				GraphStatePropertyDialog gnpd = new GraphStatePropertyDialog(nodes, Session.getSession().getWindow(), model);
//				gnpd.setVisible(true);
				break;
			case VIEW_INDICATORS:
				DrawableGraphState[] objects1 = castSelectableToDrawableGraphState(selectable);
				String[] command1 = new String[]{DrawableGraphState.ATTRIBUTE_VIEW_INDICATORS};
				Object[][] oldValues1 = new String[1][objects1.length];
				for(int i = 0; i < objects1.length; i++) {
					oldValues1[0][i] = objects1[i].getAttribute(DrawableGraphState.ATTRIBUTE_VIEW_INDICATORS);
				}
				Object[][] newValues1 = new String[1][objects1.length];
				for(int i = 0; i < objects1.length; i++) {
					newValues1[0][i] = String.valueOf(viewIndicators.getState());
				}
				Action a5 = new SetConfigAction(objects1, command1, oldValues1);
				Action a6 = new SetConfigAction(objects1, command1, newValues1);
				model.performAction(a6, a5);	
				break;
			}
		}
	}
		
	public void show(Component component, int x, int y) {
		contextMenu.show(component, x, y);
	}
	
	private class NodesContextMenu extends ContextMenu {
		
		public NodesContextMenu(){
			super();
		}
		
		@Override
		public void init(){
			changeName = createMenuEntry(changeName, RENAME, "/icons/pencil.png");
			cut = createMenuEntry(cut, CUT, "/icons/cut.png");
			copy = createMenuEntry(copy, COPY, "/icons/page_copy.png");
			paste = createMenuEntry(paste, PASTE, "/icons/page_paste.png");
			delete = createMenuEntry(delete, DELETE, "/icons/cross.png");
			
			boolean viewIndicatorsState = false;
			for(Selectable s : selectable) {
				if(s instanceof DrawableGraphState){
					DrawableGraphState n = (DrawableGraphState) s;
					if(Boolean.valueOf(n.getAttribute(DrawableGraphState.ATTRIBUTE_VIEW_INDICATORS))) {
						viewIndicatorsState = true;
						break;
					}
				}
			}
			viewIndicators = new JCheckBoxMenuItem(VIEW_INDICATORS);
			viewIndicators.setActionCommand(VIEW_INDICATORS);
			viewIndicators.addActionListener(this);
			viewIndicators.setState(viewIndicatorsState);
			
			indicatorPositionLeft = createMenuEntry(indicatorPositionLeft, INDICATORPOSITION_LEFT, "/icons/arrow_left.png");
			indicatorPositionRight = createMenuEntry(indicatorPositionRight, INDICATORPOSITION_RIGHT, "/icons/arrow_right.png");
			properties = createMenuEntry(properties, PROPERTIES, "/icons/cog_edit.png");

			indicatorMenu = new JMenu(INDICATORPOSITION);
			indicatorMenu.add(indicatorPositionLeft);
			indicatorMenu.add(indicatorPositionRight);
			
			add(changeName);
			addSeparator();
			add(viewIndicators);
			add(indicatorMenu);
			addSeparator();
			add(cut);
			add(copy);
			add(paste);
			add(delete);
			addSeparator();
			add(properties);
		}

		@Override
		public void initializeMenuEntries() {
			changeName.setEnabled(selectable.size() == 1);
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
			changeName.setEnabled(selectable.size() == 1);
			final DataFlavor df = TransferableGraphElements.df;
			final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			paste.setEnabled(clipboard.isDataFlavorAvailable(df));
		}
		
	}
	
	private class EdgesContextMenu extends ContextMenu{
		
		public EdgesContextMenu(){
			super();
		}
		
		private boolean edgeHasName(){
			return !((DrawableGraphEdge)selectable.get(0)).getEdge().getAttribute(GraphEdge.ATTRIBUTE_NAME).equals("");
		}
		
		@Override
		public void init(){
			if(edgeHasName()){
				changeName = createMenuEntry(changeName,CHANGE_LABEL, RENAME, "/icons/pencil.png");
			}else{
				changeName = createMenuEntry(changeName,ADD_NAME, RENAME, "/icons/pencil_add.png");
			}
			removeName = createMenuEntry(removeName, REMOVE_NAME, "/icons/pencil_delete.png");
			delete = createMenuEntry(delete, DELETE, "/icons/cross.png");
			
			add(changeName);
			add(removeName);
			addSeparator();
			add(delete);
		}

		@Override
		public void initializeMenuEntries() {
			boolean hasName = edgeHasName();
			changeName.setEnabled(selectable.size() == 1);
			removeName.setEnabled(hasName);
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
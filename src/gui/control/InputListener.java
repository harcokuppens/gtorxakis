package gui.control;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JComponent;

import core.Session;
import gui.dialogs.ChangeCommentParagraphsDialog;
import gui.dialogs.ChangeNameDialog;
import gui.dialogs.ChangeNameDialogEdge;
import gui.dialogs.ChangeNameDialogState;
import gui.draw.DrawableComment;
import gui.draw.DrawableGraphEdge;
import gui.draw.DrawableComment.ResizeType;
import gui.draw.DrawableGraphState;
import gui.draw.DrawableGrid;
import gui.draw.GraphInterface;
import gui.draw.GraphPanel;
import gui.window.GraphContextMenu;
import gui.window.Window;
import model.Model;

/**
 * This listener handles all the user input on the graph. This includes
 * mouse-inputs as well as keyboard-inputs. Since this class needs to know in
 * which state the current selection in the GraphInterface is, it implements the
 * Observer class and observes the GraphInterface to be notified when the
 * selection changes.
 * 
 * 
 */
public class InputListener implements MouseListener, MouseMotionListener, KeyListener, Observer, MouseWheelListener, ActionListener {

	/**
	 * This enumeration is used to save the current status of the program
	 * between the calls of the mouseEvent-functions. It contains states the
	 * program can be in and actions the user can be performing. These states
	 * and actions can only appear in special orders, eg. it is not possible to
	 * switch from one action to another without being in a state first.
	 * Furthermore, some of the actions can only be started in a special state.
	 * For a full overview of pre- and postconditions, please refer to the
	 * documentation.
	 * 
	 * 
	 */
	private enum ControlMethod {
		NONE, // State: Selection is empty, initial state
		SELECTED, // State: Selection is not empty
		PAN, // Action: Panning the visual area of the graph
		ARROW, // Action: Draw an edge, can only be started in state NONE
		RESIZECOMMENT, //Action: Change size of DrawableComment
		ARROW_ANCHOR,
		SELECTIONBOX, // Action: Draw selection box
		MOVE; // Action: Move edges, can only be started in state SELECTED
	};

	private boolean isDeployed = false;
	private ControlMethod controlMethod = ControlMethod.NONE;
	private DrawController dc;
	private Model model;
	private ControlMethod beginState;
	
	//Is used to check that a MouseEvent is finished with the right button.
	private boolean finished = true;
	private int firedMouseButton = 0;
	
	// The most recent mouse position
	private Point pos,
				  startPos;
	
	// The fired ResizeType
	private ResizeType resizeType = ResizeType.NONE;
	
	private Window window;
	
	private boolean left = false, right = false, up = false, down = false; 
	
	public InputListener(Window w) {
		this.window = w;
	}
	
	public void setModel(Model model) {
		this.model = model;
		this.dc = model.getDrawController();
		controlMethod = ControlMethod.NONE;
		isDeployed = true;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(!isDeployed || !dc.isDeployed()) return;
		GraphInterface gi = dc.getGraphInterface();

		int size = DrawableGrid.GRID_SIZE/2;
		int dx = 0 , dy = 0;
		switch (e.getKeyCode()) {
		case KeyEvent.VK_F2:
			ArrayList<Selectable> selectables = gi.getSelection();
			if(selectables.size() == 1 && keyActionsAllowed()){
				
				Selectable selectedItem = selectables.get(0);
				if(selectedItem instanceof DrawableGraphState){
					ChangeNameDialogState cnd = new ChangeNameDialogState(model, (DrawableGraphState)selectedItem, this.window);
					cnd.setVisible(true);
				}else if(selectedItem instanceof DrawableComment){
					DrawableComment c = (DrawableComment) selectedItem;
					if(c.getGraphComment().getEdge() != null){
						ChangeNameDialogEdge cnd = new ChangeNameDialogEdge(model, c.getGraphComment().getEdge().getDrawable(), this.window);
						cnd.setVisible(true);
					}else{
						ChangeCommentParagraphsDialog ccpd = new ChangeCommentParagraphsDialog(model, (DrawableComment) selectedItem, this.window);
						ccpd.setVisible(true);
					}
				}else if(selectedItem instanceof DrawableGraphEdge){
					ChangeNameDialogEdge cnd = new ChangeNameDialogEdge(model, (DrawableGraphEdge)selectedItem, this.window);
					cnd.setVisible(true);
				}
			}
			break;
		case KeyEvent.VK_TAB:
			if(keyActionsAllowed())gi.selectNextNode();
			break;
		case KeyEvent.VK_UP:
			up = true;
			break;
		case KeyEvent.VK_DOWN:
			down = true;
			break;
		case KeyEvent.VK_LEFT:
			left = true;
			break;
		case KeyEvent.VK_RIGHT:
			right = true;
			break;
		}
		
		if(up){
			dy = dy + size;
		}
		if(down){
			dy = dy - size;
		}
		if(left){
			dx = dx + size;
		}
		if(right){
			dx = dx - size;
		}
		dc.getViewBoxController().move(dx, dy);
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		if(!isDeployed || !dc.isDeployed()) return;
		switch(e.getKeyCode()){
		case KeyEvent.VK_UP:
			up = false;
			break;
		case KeyEvent.VK_DOWN:
			down = false;
			break;
		case KeyEvent.VK_LEFT:
			left = false;
			break;
		case KeyEvent.VK_RIGHT:
			right = false;
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if(!isDeployed || !dc.isDeployed()) return;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if(!isDeployed || !dc.isDeployed()) return;
		GraphInterface gi = dc.getGraphInterface();
		pos = e.getPoint();
		ViewBoxController vbc = dc.getViewBoxController();
		//Set PastePosition 
		dc.setPastePosition(vbc.getClickPosition(pos));
		Selectable element = gi.getElementAt(vbc.getClickPosition(pos));
		
		if(gi.getSelection().size() > 0){
			controlMethod = ControlMethod.SELECTED;
		}

		if (element != null) {
			switch (controlMethod) {
			case SELECTED:
				if(element.isSelected() && element instanceof DrawableComment){
					DrawableComment c = (DrawableComment) element;
					resizeType = c.getResizeType(vbc.getClickPosition(pos));
					if(!resizeType.equals(ResizeType.NONE)){
						dc.setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
					}else{
						dc.setCursor(new Cursor(Cursor.MOVE_CURSOR));
					}
				}else if (element.isSelected() && element instanceof Movable) {
					dc.setCursor(new Cursor(Cursor.MOVE_CURSOR));
				}else{
					dc.setCursor(new Cursor(Cursor.HAND_CURSOR));
				}
				break;
			default:
				dc.setCursor(new Cursor(Cursor.HAND_CURSOR));
				break;
			}
		} else {
			dc.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if(!isDeployed || !dc.isDeployed()) return;
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if(!isDeployed || !dc.isDeployed()) return;
		Point center = dc.getViewBoxController().getCenterPosition();
		dc.setPastePosition(center);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if(!isDeployed || !dc.isDeployed()) return;
		switch (controlMethod) {
		case PAN:
			dc.setCursor(new Cursor(Cursor.MOVE_CURSOR));
			dc.translatePanel(e.getPoint());
			break;
		case SELECTIONBOX:
			dc.translateSelectionBox(e.getPoint());
			break;
		case MOVE:
			dc.translateElement(e.getPoint());
			break;
		case ARROW:
			dc.translateTempEdge(e.getPoint());
			break;
		case RESIZECOMMENT:
			dc.translateComment(e.getPoint(), resizeType);
			break;
		default:
			dc.removeSelectionBox(true);
			dc.removeTempEdge(false);
			controlMethod = ControlMethod.NONE;
			throw new InputMismatchException("mouseDragged fired with controlMethod in state state. State was " + controlMethod.name());
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if(!isDeployed || !dc.isDeployed()) return;
		if (finished){
			Point p = dc.getViewBoxController().getClickPosition(e.getPoint());
			dc.setDragStart(e.getPoint());

			firedMouseButton = e.getButton();

			GraphInterface gi = dc.getGraphInterface();
			if(gi.getSelection().size() > 0){
				controlMethod = ControlMethod.SELECTED;
			}
			beginState = controlMethod;
			
			switch (e.getButton()) {
			case MouseEvent.BUTTON1:
				Selectable element = dc.getGraphInterface().getElementAt(p);
				switch (controlMethod) {
				case SELECTED:
					if (element != null) {                     
                        if(element.isSelected()){ 
                        		if(element instanceof DrawableComment){
                        			DrawableComment c = (DrawableComment) element;
                        			ResizeType resizeType = c.getResizeType(p);
                        			if(!resizeType.equals(ResizeType.NONE)){
                        				System.out.println("[Inputlistener] Resize comment");
            							controlMethod = ControlMethod.RESIZECOMMENT;
                        			}else{
                        				controlMethod = ControlMethod.MOVE;
                        			}
                        		}else{
                        			controlMethod = ControlMethod.MOVE;
                        		}
                        }                                  
					}                                          
					if(element == null || !element.isSelected()){
                        dc.setSelectionBox();              
                        controlMethod = ControlMethod.SELECTIONBOX;
					}
					break;
				case NONE:
                    if (element != null && element instanceof DrawableGraphState) {
                    	startPos = new Point(e.getPoint());
                    	dc.setTempEdge();                  
                    	controlMethod = ControlMethod.ARROW;
                    } else {                                   
                        dc.setSelectionBox();              
                        controlMethod = ControlMethod.SELECTIONBOX;
                    }
					break;
				default:
					ControlMethod temp = controlMethod;
					controlMethod = ControlMethod.NONE;
//					System.err.println(beginState.name());
					throw new InputMismatchException("mousePressed fired with controlMethod in action state. State was " + temp.name());
				}
				break;
			case MouseEvent.BUTTON3:
				controlMethod = ControlMethod.PAN;
				break;
			}
//			System.out.println("[InputListener] mousePressed finished with " + controlMethod.name());
			finished = false;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == firedMouseButton){
			if(!isDeployed || !dc.isDeployed()) return;
			
			dc.setDragEnd(e.getPoint());
			
			switch (controlMethod) {
			case SELECTIONBOX:
				dc.removeSelectionBox(!(beginState.equals(ControlMethod.SELECTED) && e.isControlDown()));
				break;
			case MOVE:
				dc.moveElements();
				break;
			case ARROW:
				dc.removeTempEdge(startPos.equals(e.getPoint()));
				break;
			case RESIZECOMMENT:
				dc.resizeElement(resizeType);
				resizeType = ResizeType.NONE;
				//Do nothing
			case PAN:
				// Do nothing
			case NONE:
				// Do nothing
				break;
			default:
				ControlMethod temp = controlMethod;
				throw new InputMismatchException("mouseReleased fired with controlMethod in state state. State was " + temp.name());
			}
			
			controlMethod = ControlMethod.NONE;
			finished = true;
//			System.out.println("[InputListener] mouseReleased finished with " + controlMethod.name());
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(finished){
			if(!isDeployed || !dc.isDeployed()) return;

			GraphInterface gi = dc.getGraphInterface();
			Point p = dc.getViewBoxController().getClickPosition(e.getPoint());
			Selectable element = gi.getElementAt(p);
			
			if(gi.getSelection().size() > 0){
				controlMethod = ControlMethod.SELECTED;
			}
			
			int clickCount = e.getClickCount();
			switch (e.getButton()) {
			case MouseEvent.BUTTON1:
				switch (controlMethod) {
				case NONE:
					if (element != null) {
						if(clickCount == 2){
							if(element instanceof DrawableComment){
								ChangeCommentParagraphsDialog ccpd = new ChangeCommentParagraphsDialog(model, (DrawableComment) element, Session.getSession().getWindow());
								ccpd.setVisible(true);
							}else if(element instanceof DrawableGraphState){
								ChangeNameDialogState cnd = new ChangeNameDialogState(model, (DrawableGraphState) element, Session.getSession().getWindow());
								cnd.setVisible(true);
							}
						}else{
							gi.setSelection(new Selectable[] { element });
							controlMethod = ControlMethod.SELECTED;							
						}
					} else if (beginState == ControlMethod.NONE && clickCount == 2){
						gi.addNode((int) p.getX(), (int) p.getY());
					}
					break;
				case SELECTED:
					if (element != null) {
						if (element.isSelected()) {
							if (e.isControlDown()) {
								gi.removeFromSelection(element);
							} else {
								gi.clearSelection();
							}
							if(clickCount == 2){
								if(element instanceof DrawableComment){
									ChangeCommentParagraphsDialog ccpd = new ChangeCommentParagraphsDialog(model, (DrawableComment) element, Session.getSession().getWindow());
									ccpd.setVisible(true);
								}else if(element instanceof DrawableGraphState){
									ChangeNameDialogState cnd = new ChangeNameDialogState(model, (DrawableGraphState) element, Session.getSession().getWindow());
									cnd.setVisible(true);
								}else if(element instanceof DrawableGraphEdge){
									ChangeNameDialogEdge cnd = new ChangeNameDialogEdge(model, (DrawableGraphEdge) element, Session.getSession().getWindow());
									cnd.setVisible(true);
								}
							}
							if (gi.getSelection().size() == 0) {
								controlMethod = ControlMethod.NONE;
							}
						} else {
							if (e.isControlDown()) {
								gi.addToSelection(element);
							} else {

								gi.setSelection(element);
							}
						}
					} else {
						gi.clearSelection();
						controlMethod = ControlMethod.NONE;
					}
					break;
				default:
					throw new InputMismatchException("mouseClicked fired with controlMethod in action state. State was " + controlMethod.name());
				}
				break;
			case MouseEvent.BUTTON3:
				if (element != null) {
					if (!element.isSelected()) {
						gi.setSelection(element);
						controlMethod = ControlMethod.SELECTED;
					}
				} else {
					gi.clearSelection();
					controlMethod = ControlMethod.NONE;
				}
				dc.setPastePosition(p);
				
				GraphPanel panel = (GraphPanel) e.getComponent();
				final GraphContextMenu gncm = new GraphContextMenu(gi.getSelection(), p, model);
				gncm.show(e.getComponent(), e.getX(), e.getY());

				break;
			}
//			System.out.println("[InputListener] mouseClicked finished with " + controlMethod.name());
		}
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if(!isDeployed || !dc.isDeployed()) return;
		int rot = e.getWheelRotation();
		if(rot < 0) {
			for(int i = 0; i < Math.abs(rot); i++) {
				
				dc.getViewBoxController().zoomIn();
			}
		} else if (rot > 0){
			for(int i = 0; i < Math.abs(rot); i++) {
				dc.getViewBoxController().zoomOut();
			}
		}
		
	}

	@Override
	public void update(Observable o, Object arg) {
		if(!isDeployed || !dc.isDeployed()) return;
		if (o instanceof GraphInterface && finished) {
			if (((GraphInterface) o).getSelection().size() != 0) {
				controlMethod = ControlMethod.SELECTED;
			} else {
				controlMethod = ControlMethod.NONE;
			}
		}
	}

	private boolean keyActionsAllowed(){
		return controlMethod.equals(ControlMethod.SELECTED)||controlMethod.equals(ControlMethod.NONE);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		GraphInterface gi = dc.getGraphInterface();
		JComponent item = (JComponent) e.getSource();
		if(keyActionsAllowed()){
			switch(e.getActionCommand()){
			case "redo":
				model.redoAction();
				item.setEnabled(model.canRedo());
				break;
			case "undo":
				model.undoAction();
				item.setEnabled(model.canUndo());
				break;
			case "delete":
				gi.deleteEvent();
				break;
			case "cut":
				gi.cutEvent();
				break;
			case "copy":
				gi.copyEvent();
				break;
			case "paste":
				Point p = dc.getPastePos();
				gi.pasteEvent(p.x, p.y);
				break;
			case "selectall":
				gi.selectAll();
				break;
			default:
				throw new InputMismatchException("mouseClicked fired with controlMethod in action state. State was " + controlMethod.name());
			}
		}
	}
}

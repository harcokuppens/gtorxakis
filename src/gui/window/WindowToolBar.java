package gui.window;


import java.awt.Component;
import java.awt.event.ActionListener;

import gui.control.InputListener;
import gui.control.WindowActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;


public class WindowToolBar extends JToolBar {
	
	private WindowActionListener wat;
	private InputListener inputListener;
	private JButton undo, redo, showResults;
	
	public WindowToolBar(WindowActionListener wat, InputListener inputListener){
		this.wat = wat;
		this.inputListener = inputListener;
		initialize();
	}

	private void initialize(){
		addButton("New", WindowActionListener.NEW, true, "/icons/page_white_add.png", wat);
		addButton("Open", WindowActionListener.OPEN, true, "/icons/folder.png" ,wat);
		addButton("Save", WindowActionListener.SAVE, true, "/icons/disk.png", wat);
		this.add(new JToolBar.Separator());
		undo = addButton("Undo", WindowActionListener.UNDO, false, "/icons/arrow_undo.png", inputListener);
		redo = addButton("Redo", WindowActionListener.REDO, false, "/icons/arrow_redo.png", inputListener);
		this.add(new JToolBar.Separator());
		addButton("Zoom Out", WindowActionListener.ZOOM_OUT, true, "/icons/magnifier_zoom_out.png", wat);
		addButton("Reset Zoom", WindowActionListener.ZOOM_RESET, true, "/icons/magnifier.png", wat);
		addButton("Zoom In", WindowActionListener.ZOOM_IN, true, "/icons/magnifier_zoom_in.png",wat);
		this.add(new JToolBar.Separator());
		addButton("Run", WindowActionListener.RUN, true, "/icons/application_go.png", wat);
		showResults = addButton("Show results", WindowActionListener.SHOW_RESULTS, false, "/icons/script.png", wat);
		
		for(Component c : this.getComponents()){
			c.setFocusable(false);
		}
	}
	
	private JButton addButton(String name, String cmd, boolean enabled, String iconPath, ActionListener al){
		JButton temp = new JButton("");
		temp.setIcon(new ImageIcon(Window.class.getResource(iconPath)));
		temp.setActionCommand(cmd);
		temp.addActionListener(al);
		temp.setEnabled(enabled);
		temp.setToolTipText(name);
		this.add(temp);
		return temp;
	}
	
	public void setItemEnabled(String name,boolean b) {
		switch(name){
		case WindowActionListener.UNDO:
			undo.setEnabled(b);
			break;
		case WindowActionListener.REDO:
			redo.setEnabled(b);
			break;
		case WindowActionListener.SHOW_RESULTS:
			showResults.setEnabled(b);
			break;
		}
	}
	
}

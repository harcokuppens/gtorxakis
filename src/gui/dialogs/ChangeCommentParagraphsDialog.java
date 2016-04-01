package gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import gui.draw.DrawableComment;
import gui.draw.GraphInterface;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import model.graph.GraphState;
import model.Model;
import action.Action;
import action.SetConfigAction;
import core.Session;

public class ChangeCommentParagraphsDialog extends Dialog{
	
	private DrawableComment dc;
	private AbstractAction okAction, cancelAction;
	private JTextArea textArea;
	private JButton closeButton, okButton;
	private JPanel  bottomPanel, centerPanel;
	private Model model;
	
	public ChangeCommentParagraphsDialog(Model model, DrawableComment dc, JFrame parent){
		super(parent);
		this.model = model;
		this.dc = dc;
		setTitle("Edit text");
		centerOnScreen();	
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setResizable(false);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		initActions();
		init();
		this.pack();
	}
	
	public void init(){
		centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());
		
		textArea = new JTextArea();
//		textArea.setPreferredSize(new Dimension(250, 80));
		textArea.setText(dc.getAttribute(DrawableComment.TEXT));
		textArea.selectAll();
		textArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escapeKey");
		textArea.getActionMap().put("escapeKey", cancelAction);
		textArea.setLineWrap(true);
		
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		final JScrollPane scroll = new JScrollPane(textArea);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setPreferredSize(new Dimension(250, 80));
		SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					scroll.getViewport().setViewPosition(new Point(0, 0));
				}
		});

		this.getContentPane().add(scroll);
		
		getContentPane().add(centerPanel);
		
		bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());
		
		closeButton = new JButton();
		closeButton.setAction(cancelAction);
		closeButton.setText("Cancel");
		bottomPanel.add(closeButton, BorderLayout.WEST);
		
		okButton = new JButton();
		okButton.setAction(okAction);
		okButton.setText("Ok");
		bottomPanel.add(okButton, BorderLayout.EAST);
		
		getContentPane().add(bottomPanel);
	}
	
	
	private String checkText(String oldValue, String newValue){
		while(newValue.endsWith("\n")){
			newValue = newValue.substring(0, newValue.length()-1);
		}
		if(oldValue.equals(newValue) || newValue.equals(""))return null;
		else{
			return newValue;
		}
	}
	
	private void initActions(){
		okAction = new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//checkText
				String oldValue = dc.getAttribute(DrawableComment.TEXT);
				String newValue = checkText(oldValue, textArea.getText());
				if(newValue != null){
					DrawableComment[] objects = new DrawableComment[]{dc};
					String[] cmd = new String[]{DrawableComment.TEXT};
					Object[][] oldValues = new String[1][objects.length];
					for(int i = 0; i < objects.length; i++) {
						oldValues[0][i] = objects[i].getAttribute(DrawableComment.TEXT);
					}
					Object[][] newValues = new String[1][objects.length];
					for(int i = 0; i < objects.length; i++) {
						//CheckText
//						newValues[0][i] = textArea.getText();
						newValues[0][i] = newValue;
					}
					Action a1 = new SetConfigAction(objects, cmd, oldValues);
					Action a2 = new SetConfigAction(objects, cmd, newValues);
					model.performAction(a2, a1);
				}
				dispose();
			}
			
		};
		
		cancelAction = new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
			
		};
	}

}

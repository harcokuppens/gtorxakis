package gui.dialogs;

import gui.control.Selectable;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import model.Model;

public abstract class ChangeNameDialog extends Dialog {
	protected JTextField nameTXT;
	private JButton closeButton, okButton;
	private JPanel  bottomPanel, centerPanel;
	private AbstractAction okAction, cancelAction;
	
	public static final String EDGE = "path coefficient",
							   NODE = "construct";

	protected Model model;

	protected ChangeNameDialog(Model model, JFrame parent, String cmd) {
		super(parent);
		this.model = model;
		setTitle("Rename " + cmd);
		setSize(250,80);
		centerOnScreen();	
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setResizable(false);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		okAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				success();
			}
		};
		cancelAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		};
	}
	
	public abstract void success();
	
	public void init(String name){
		centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());
		
		nameTXT = new JTextField();
		nameTXT.setText(name);
		nameTXT.selectAll();
		nameTXT.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escapeKey");
		nameTXT.getActionMap().put("escapeKey", cancelAction);
		nameTXT.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enterKey");
		nameTXT.getActionMap().put("enterKey", okAction);
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		centerPanel.add(nameTXT,BorderLayout.CENTER);
		
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
	
}

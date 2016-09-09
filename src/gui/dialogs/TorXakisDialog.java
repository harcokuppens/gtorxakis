package gui.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class TorXakisDialog extends Dialog{
	
	private JTextArea textArea;
	private BufferedReader reader;
	private Dialog dialog;
	
	public TorXakisDialog(RunDialog runDialog, BufferedReader reader){
		this.reader = reader;
		dialog = this;
		setLayout(new BorderLayout());
		textArea = new JTextArea();
		textArea.setEditable(false);
		this.add(textArea, BorderLayout.CENTER);
		JButton close = new JButton("Close");
		close.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				runDialog.getSocketIO().close();
				dialog.dispose();
			}
		});
		JPanel dummy = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		dummy.add(close);
		add(dummy, BorderLayout.SOUTH);
		this.setSizeByScreenSize(0.5);
		this.centerOnScreen();
		this.setModal(true);
	}
	
	public void readLines(){
		String line;
		try {
			while((line = reader.readLine()) != null){
				textArea.append(line+"\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	

}

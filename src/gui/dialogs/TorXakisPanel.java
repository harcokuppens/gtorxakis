package gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class TorXakisPanel extends JPanel{
	
	private RunDialog dialog;
	
	private JTextPane textPane;
	private JScrollPane scrollPane;
	
	private boolean isReading = false;
	
	private enum AnswerType{
		PACK("black"),
		NACK("red"),
		FACK("green"),
		ERROR("red");
		
		private String color;
		
		private AnswerType(String color){
			this.color = color;
		}
		
		public String getColor(){
			return color;
		}
	}
	
	public TorXakisPanel(RunDialog dialog){
		this.dialog = dialog;
		setLayout(new BorderLayout());
		scrollPane = new JScrollPane();
		textPane = new JTextPane();
		StyledDocument doc = textPane.getStyledDocument();
        addStylesToDocument(doc);
		textPane.setEditable(false);
		scrollPane.setViewportView(textPane);
		this.add(scrollPane, BorderLayout.CENTER);
		this.add(createCommandPanel(), BorderLayout.SOUTH);
		this.setMinimumSize(new Dimension(600,300));
		this.setPreferredSize(new Dimension(600,300));
	}
	
	private JPanel createCommandPanel() {
		JPanel temp = new JPanel(new BorderLayout());
		JTextField commandLine = new JTextField("", 30);
		commandLine.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try{
					dialog.getSocketIO().sendCommand(commandLine.getText());
					commandLine.setText("");
				}catch(NullPointerException e){
					JOptionPane.showMessageDialog(dialog, "Communication error with TorXakis. Are you sure that TorXakis is running?");
				}
			}
			
		});
		JButton send = new JButton("Send");
		send.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try{
					dialog.getSocketIO().sendCommand(commandLine.getText());
					commandLine.setText("");
				}catch(NullPointerException e){
					JOptionPane.showMessageDialog(dialog, "Communication error with TorXakis. Are you sure that TorXakis is running?");

				}
			}
		});
		temp.add(commandLine, BorderLayout.CENTER);
		temp.add(send, BorderLayout.EAST);
		return temp;
	}

	public void readLines(BufferedReader reader) throws SocketException, IOException{
		String line;
		isReading = true;
		while((line = reader.readLine()) != null){
			addLine(line+"\n");
			JScrollBar vertical = scrollPane.getVerticalScrollBar();
			vertical.setValue( vertical.getMaximum() );
		}
	}
	
	private void addLine(String line){
		if(line.trim().equals("") || line.trim().equals("\n")) {
			return;
		}
		AnswerType type;
		String message;
		try{
			type = AnswerType.valueOf(line.substring(0,4));
			message = line.substring(5);
		}catch(Exception e){
			type = AnswerType.ERROR;
			message = line;
		}
		StyledDocument doc = textPane.getStyledDocument();
		try {
			doc.insertString(doc.getLength(), message, doc.getStyle(type.getColor()));
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	public void clear(){
		isReading = false;
		textPane.setText("");
		JScrollBar vertical = scrollPane.getVerticalScrollBar();
		vertical.setValue( vertical.getMinimum() );
	}
	
	public boolean isReading(){
		return isReading;
	}
	
	 
	protected void addStylesToDocument(StyledDocument doc) {
		Style def = StyleContext.getDefaultStyleContext(). getStyle(StyleContext.DEFAULT_STYLE);
		
		Style regular = doc.addStyle("regular", def);
		StyleConstants.setFontFamily(def, "SansSerif");
		
		Style s = doc.addStyle("italic", regular);
		StyleConstants.setItalic(s, true);
		
		
		s = doc.addStyle("small", regular);
		StyleConstants.setFontSize(s, 10);
		
		Style large = doc.addStyle("large", regular);
		StyleConstants.setFontSize(large, 16);

		Style bold = doc.addStyle("bold", large);
		StyleConstants.setBold(bold, true);
		
		s = doc.addStyle("red", bold);
		StyleConstants.setForeground(s, new Color(255,0,0));
		
		s = doc.addStyle("green", bold);
		StyleConstants.setForeground(s, new Color(0,138,4));
		
		s = doc.addStyle("black", bold);
		StyleConstants.setForeground(s, new Color(0,0,0));
	}


}

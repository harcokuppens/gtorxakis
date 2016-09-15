package gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class TorXakisPanel extends JPanel{
	
	private JTextPane textPane;
	private JScrollPane scrollPane;
	
	private boolean isReading = false;
	
	private enum AnswerType{
		PACK("black"),
		NACK("red"),
		FACK("green");
		
		private String color;
		
		private AnswerType(String color){
			this.color = color;
		}
		
		public String getColor(){
			return color;
		}
	}
	
	public TorXakisPanel(){
		setLayout(new BorderLayout());
		scrollPane = new JScrollPane();
		textPane = new JTextPane();
		StyledDocument doc = textPane.getStyledDocument();
        addStylesToDocument(doc);
		textPane.setEditable(false);
		scrollPane.setViewportView(textPane);
		this.add(scrollPane, BorderLayout.CENTER);
		this.setMinimumSize(new Dimension(600,300));
		this.setPreferredSize(new Dimension(600,300));
	}
	
	public void readLines(BufferedReader reader){
		String line;
		isReading = true;
		try {
			while((line = reader.readLine()) != null){
//				textArea.insertComponent(new JLabel("<html><strong>"+getLine(line)+"</strong></br></html>"));
//				textArea.setCaretPosition(textArea.getText().length());
//				textPane.append(getLine(line) + "\n");
				addLine(line+"\n");
				JScrollBar vertical = scrollPane.getVerticalScrollBar();
				vertical.setValue( vertical.getMaximum() );
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void addLine(String line){
		if(line.trim().equals("") || line.trim().equals("\n")) {
			return;
		}
		
		AnswerType type = AnswerType.valueOf(line.substring(0,4));
		StyledDocument doc = textPane.getStyledDocument();
		try {
			doc.insertString(doc.getLength(), line.substring(5), doc.getStyle(type.getColor()));
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
		//Initialize some styles.
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

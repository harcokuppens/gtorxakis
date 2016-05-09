package gui.draw;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.text.Segment;

import org.fife.ui.rsyntaxtextarea.AbstractTokenMaker;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.TokenMap;
import org.fife.ui.rtextarea.RTextScrollPane;

import model.Definition;
import util.MyTokenMaker;

public class GUITextualDefinition extends JPanel{

	private Definition definition;
	private RSyntaxTextArea textArea;
	
	public GUITextualDefinition(Definition definition){
		super(new BorderLayout());
		this.definition = definition;
		
		AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory)TokenMakerFactory.getDefaultInstance();
		atmf.putMapping("text/TorXakis", MyTokenMaker.class.getName());
		
		textArea = new RSyntaxTextArea(20, 60);
		textArea.setSyntaxEditingStyle("text/TorXakis");
	    textArea.setCodeFoldingEnabled(true);
	    RTextScrollPane sp = new RTextScrollPane(textArea);
	    this.add(sp);
	    
	}
	
	public boolean canRedo(){
		return textArea.canRedo();
	}
	
	public boolean canUndo(){
		return textArea.canUndo();
	}
	
}

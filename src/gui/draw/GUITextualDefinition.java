package gui.draw;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

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

import core.Session;
import model.Definition;
import model.TextualDefinition;
import util.MyTokenMaker;

public class GUITextualDefinition extends JPanel implements KeyListener{

	private RSyntaxTextArea textArea;
	
	public GUITextualDefinition(String definition){
		super(new BorderLayout());
		
		AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory)TokenMakerFactory.getDefaultInstance();
		atmf.putMapping("text/TorXakis", MyTokenMaker.class.getName());
		
		textArea = new RSyntaxTextArea(20, 60);
		textArea.setSyntaxEditingStyle("text/TorXakis");
	    textArea.setCodeFoldingEnabled(true);
	    textArea.setText(definition);
	    textArea.addKeyListener(this);
	    RTextScrollPane sp = new RTextScrollPane(textArea);
	    this.add(sp);
	    
	}
	
	public boolean canRedo(){
		return textArea.canRedo();
	}
	
	public boolean canUndo(){
		return textArea.canUndo();
	}
	
	public String getText(){
		return textArea.getText();
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
	
}

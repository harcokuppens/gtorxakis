package gui.draw;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;

import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rtextarea.RTextScrollPane;

import util.MyTokenMaker;

/**
 * Graphical representaiton of a textual definition.
 * @author Tobias
 *
 */
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
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
	
}

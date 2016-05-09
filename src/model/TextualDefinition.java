package model;

import action.Configurable;
import gui.control.DrawController;
import gui.draw.GUITextualDefinition;

public class TextualDefinition extends Definition implements Configurable{
	
	private final Project project;
	
	private String definition;
	
	private GUITextualDefinition drawable;
	
	private static final String DEFAULT_SUT_DEFINITION = "Default SUT Definition";
	public static final String ATTRIBUTE_DEFINITION = "definition";
	
	public TextualDefinition(Project project, String definition, String title){
		super(title);
		this.project = project;
		this.definition = definition;
	}
	
	public void setDrawable(GUITextualDefinition drawable){
		this.drawable = drawable;
	}

	@Override
	public void setSaved() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isSaved() {
		// TODO Auto-generated method stub
		return false;
	}


	public static TextualDefinition newDefinition(Project p, String title) {
		return new TextualDefinition(p, DEFAULT_SUT_DEFINITION, title);
	}


	@Override
	public void setAttribute(String cmd, Object value) {
		
	}


	@Override
	public Object getAttribute(String cmd) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void updateConfigs(DrawController dc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean canRedo() {
		drawable.canRedo();
		return false;
	}


	@Override
	public boolean canUndo() {
		drawable.canUndo();
		return false;
	}

}

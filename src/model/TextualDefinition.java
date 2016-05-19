package model;

import org.apache.xpath.functions.FuncUnparsedEntityURI;

import action.Configurable;
import gui.control.DrawController;
import gui.draw.GUITextualDefinition;

public class TextualDefinition extends Definition{
	
	private final Project project;
	
	private String definition;
	
	private GUITextualDefinition drawable;
	
	public enum DefType{
		TYPE("TYPE Definition", DEFAULT_TYPE_DEF),
		CONST("CONST Definition", DEFAULT_CONST_DEF),
		FUNC("FUNC Definition", DEFAULT_FUNC_DEF),
		SPEC("SPEC Definition", DEFAULT_SPEC_DEF),
		ADAP("ADAP Definition", DEFAULT_ADAP_DEF),
		SUT("SUT Definition", DEFAULT_SUT_DEF);
		
		private String name, defaultDef;
		
		private DefType(String name, String defaultDef){
			this.name = name;
			this.defaultDef = defaultDef;
		}
		
		public String getName(){
			return name;
		}
		
		public String getDefaultDef(){
			return defaultDef;
		}
	}
	
	public static final String DEFAULT_TYPE_DEF = "TYPEDEF Type1 \n\t::=\n\t\t\n\nENDDEF",
							   DEFAULT_CONST_DEF = "CONSTDEF Const1 \n\t::=\n\t\t\n\nENDDEF",
							   DEFAULT_FUNC_DEF = "FUNCDEF Func1 \n\t::=\n\t\t\n\nENDDEF",
							   DEFAULT_SPEC_DEF = "SPECDEF Spec1 \n\t::=\n\t\t\n\nENDDEF",
							   DEFAULT_ADAP_DEF = "ADAPDEF Adap1 \n\t::=\n\t\t\n\nENDDEF",
							   DEFAULT_SUT_DEF = "SUTDEF Sut1 \n\t::=\n\t\t\n\nENDDEF";
	
	public TextualDefinition(Project project, String definition, String title){
		super(title);
		this.project = project;
		this.definition = definition;
	}
	
	public TextualDefinition(Project project, DefType type){
		this(project, type.defaultDef, type.name);
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


//	public static TextualDefinition newDefinition(Project p, String title) {
//		return new TextualDefinition(p, DEFAULT_SUT_DEFINITION, title);
//	}

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
	
	public String getDefinitionText(boolean save){
		if(save){
			definition = drawable.getText();
		}
		return definition;
	}
	
	public GUITextualDefinition getDrawable(){
		return drawable;
	}
	
}

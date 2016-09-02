package model;

import org.apache.xpath.functions.FuncUnparsedEntityURI;

import action.Configurable;
import gui.control.DrawController;
import gui.draw.GUITextualDefinition;

public class TextualDefinition extends Definition{
	
	private final Project project;
	
	private String definition;
	private DefType type;
	
	private GUITextualDefinition drawable;
	
	public enum DefType{
		TYPE("TYPE Definition", DEFAULT_TYPE_DEF, "TYPEDEF"),
		CONST("CONST Definition", DEFAULT_CONST_DEF, "CONSTDEF"),
		FUNC("FUNC Definition", DEFAULT_FUNC_DEF, "FUNCDEF"),
		MODEL("MODEL Definition", DEFAULT_MODEL_DEF, "MODELDEF"),
		CNECTDEF("CNECTDEF Definition", DEFAULT_CNECT_DEF, "CNECTDEF"),
		PROC("PROC Definition", DEFAULT_PROC_DEF, "PROCDEF");
		
		private String name, 
					   defaultDef,
					   identifier;
		
		private DefType(String name, String defaultDef, String identifier){
			this.name = name;
			this.defaultDef = defaultDef;
			this.identifier = identifier;
		}
		
		public String getName(){
			return name;
		}
		
		public String getDefaultDef(){
			return defaultDef;
		}
		
		public String getIdentifier(){
			return identifier;
		}
	}
	
	public static final String DEFAULT_TYPE_DEF = "TYPEDEF Type1\n\t::=\n\t\t\n\nENDDEF",
							   DEFAULT_CONST_DEF = "CONSTDEF Const1\n\t::=\n\t\t\n\nENDDEF",
							   DEFAULT_FUNC_DEF = "FUNCDEF Func1\n\t::=\n\t\t\n\nENDDEF",
							   DEFAULT_MODEL_DEF = "MODELDEF Model1\n\t::=\n\t\t\n\nENDDEF",
							   DEFAULT_CNECT_DEF = "CNECTDEF Connect1\n\t::=\n\t\t\n\nENDDEF",
							   DEFAULT_PROC_DEF = "PROCDEF Proc1\n\t::=\n\t\t\n\nENDDEF";
	
	public TextualDefinition(Project project, String definition, String title, DefType type){
		super(title);
		this.project = project;
		this.definition = definition;
		this.type = type;
	}
	
	public TextualDefinition(Project project, DefType type){
		this(project, type.defaultDef, type.name, type);
	}
	
	public void setDrawable(GUITextualDefinition drawable){
		this.drawable = drawable;
	}

	@Override
	public void setSaved() {
	}

	@Override
	public boolean isSaved() {
		return false;
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
	
	public String getDefinitionText(){
		if(drawable != null){
			definition = drawable.getText();
		}
		return definition;
	}
	
	public GUITextualDefinition getDrawable(){
		return drawable;
	}

	@Override
	public String getDefinitionAsText() {
		return getDefinitionText();
	}
	
	public DefType getType(){
		return type;
	}
	
}

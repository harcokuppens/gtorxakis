package model;

import gui.draw.GUITextualDefinition;

/**
 * A textual definition, like PROCDEF, MODELDEF, ...
 * @author Tobias
 *
 */
public class TextualDefinition extends Definition{
	
	private String definition;
	private String savedText;
	private DefType type;
	
	private GUITextualDefinition drawable;
	
	/**
	 * An enumeration for all the textual definitions.
	 * @author Tobias
	 *
	 */
	public enum DefType{
		TYPE("TYPE Definition", DEFAULT_TYPE_DEF, "TYPEDEF"),
		CONST("CONST Definition", DEFAULT_CONST_DEF, "CONSTDEF"),
		FUNC("FUNC Definition", DEFAULT_FUNC_DEF, "FUNCDEF"),
		MODEL("MODEL Definition", DEFAULT_MODEL_DEF, "MODELDEF"),
		CNECTDEF("CNECT Definition", DEFAULT_CNECT_DEF, "CNECTDEF"),
		PROC("PROC Definition", DEFAULT_PROC_DEF, "PROCDEF");
		
		private String name, 
					   defaultDef,
					   identifier;
		
		private DefType(String name, String defaultDef, String identifier){
			this.name = name;
			this.defaultDef = "";
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
	
	public TextualDefinition(String definition, String title, DefType type){
		super(title);
		this.definition = definition;
		savedText = definition;
		this.type = type;
	}
	
	public TextualDefinition(DefType type){
		this(type.defaultDef, type.name, type);
	}
	
	public void setDrawable(GUITextualDefinition drawable){
		this.drawable = drawable;
	}

	@Override
	public void setSaved() {
		savedText = this.getDefinitionAsText();
	}

	@Override
	public boolean isSaved() {
		return savedText.equals(this.getDefinitionAsText());
	}

	@Override
	public boolean canRedo() {
		return drawable.canRedo();
	}

	@Override
	public boolean canUndo() {
		return drawable.canUndo();
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

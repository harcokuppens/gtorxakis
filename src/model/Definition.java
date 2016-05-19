package model;

public abstract class Definition {
	private String title;
	
	public Definition(String title){
		this.title = title;
	}
	
	public abstract void setSaved();
	public abstract boolean isSaved();
	
	public abstract boolean canRedo();
	public abstract boolean canUndo();
	
	public abstract String getDefinitionAsText();
	
	
	public String getTitle(){
		return title;
	}

}

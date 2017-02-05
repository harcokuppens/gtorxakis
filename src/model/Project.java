package model;

import java.io.File;
import java.util.ArrayList;

import core.Session;
import gui.dialogs.SaveAsDialog;
import io.file.FileType;
import io.file.FileTypeAssociation;
import io.file.project.exporter.ProjectExport;

/**
 * A project that holds all definitions.
 * @author Tobias
 *
 */
public class Project {
	private String name;
	
	private boolean modelsChanged = false;

	private String path;

	private ArrayList<Definition> definitions;
	
	public static final int NR_TEXTUAL_DEFINITIONS = 8;
	
	public Project(String name) {
		this.name = name;
		this.definitions = new ArrayList<Definition>();
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public void setSaved() {
		for (Definition d : definitions) {
			d.setSaved();
		}
		modelsChanged = false;
	}

	public boolean isSaved() {
		boolean isSaved = true;
		for(Definition d: definitions) {
			isSaved &= d.isSaved();
		}
		return isSaved && !modelsChanged;
	}

	public String getName() {
		return name;
	}
	
	public String getPath() {
		return path;
	}
	
	public void save() {
		if(path == null) {
			new SaveAsDialog(Session.getSession().getWindow());
		} else {
			save(path, FileTypeAssociation.ProjectExport.getDefaultFileType());
		}
		setSaved();
	}
		
	public void saveAs(String path, FileType filetype) {
		if(filetype.equals(FileTypeAssociation.ProjectExport.getDefaultFileType())) {
			this.name = path.substring(path.lastIndexOf(File.separator)+1, path.lastIndexOf("."));
			this.path = path;
		}
		this.save(path, filetype);
		setSaved();
	}
	
	private void save(String path, FileType filetype) {
		String fileExtension = "." + filetype.getFileExtensions()[0];
		if(!path.endsWith(fileExtension)) {
			path += fileExtension;
		}
		ProjectExport exporter = new ProjectExport(this, path, filetype);
		exporter.exportProject();
	}

	public static Project newProject() {
		Project p = new Project("Unnamed");
		Model m = Model.newModel(p, "Model1");
		TextualDefinition connect = new TextualDefinition(TextualDefinition.DefType.CNECTDEF);
		TextualDefinition model = new TextualDefinition(TextualDefinition.DefType.MODEL);
		TextualDefinition type = new TextualDefinition(TextualDefinition.DefType.TYPE);
		TextualDefinition constDef = new TextualDefinition(TextualDefinition.DefType.CONST);
		TextualDefinition func = new TextualDefinition(TextualDefinition.DefType.FUNC);
		TextualDefinition proc = new TextualDefinition(TextualDefinition.DefType.PROC);
		
		p.addDefinition(type);
		p.addDefinition(constDef);
		p.addDefinition(func);
		p.addDefinition(model);
		p.addDefinition(connect);
		p.addDefinition(proc);
		p.addDefinition(m);
		return p;
	}
	
	public void addDefinition(Definition d) {
		definitions.add(d);
		modelsChanged = true;
	}

	public void removeDefinition(Definition d) {
		definitions.remove(d);
		modelsChanged = true;
	}
	
	public ArrayList<Definition> getDefinitions(){
		return definitions;
	}

	public ArrayList<Model> getModels(){
		ArrayList<Model> models = new ArrayList<Model>();
		for(Definition d : getDefinitionsByClass(Model.class)){
			models.add((Model) d);
		}
		return models;
	}
	
	public ArrayList<Definition> getDefinitionsByClass(Class<?> c) {
		ArrayList<Definition> def = new ArrayList<Definition>();
		for(Definition d : definitions){
			if(c.isInstance(d)){
				def.add(d);
			}
		}
		return def;
	}
	
	private TextualDefinition getTextualDefinition(TextualDefinition.DefType defType){
		for(Definition d : definitions){
			if(d instanceof TextualDefinition){
				TextualDefinition td = (TextualDefinition) d;
				if(td.getType().equals(defType)){
					return td;
				}
			}
		}
		return null;
	}
	
	public ArrayList<String> getDefinitionsByTypeDef(TextualDefinition.DefType defType){
		ArrayList<String> defs = new ArrayList<String>();
		TextualDefinition model = this.getTextualDefinition(defType);
		String defString = "";
		if(model != null){
			defString = model.getDefinitionAsText();
		}
		
		String defLines[] = defString.split("\n");
		System.err.println("sutLines size : "+defLines.length);
		for(String s : defLines){
			if(s.contains(defType.getIdentifier())){
				defs.add(s.substring(defType.getIdentifier().length(), s.length()).trim());
			}
		}
		return defs;
	}

}

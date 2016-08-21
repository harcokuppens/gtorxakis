package model;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import action.SetConfigAction;
import core.Session;
import gui.dialogs.SaveAsDialog;
import io.file.FileType;
import io.file.FileTypeAssociation;
import io.file.project.exporter.ProjectExport;


public class Project {
	private String name;
	
	private boolean settingsChanged = false;
	private boolean modelsChanged = false;

	private String path;

	private ArrayList<Definition> definitions;
	
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
		settingsChanged = false;
		modelsChanged = false;
	}

	public boolean isSaved() {
		boolean isSaved = true;
		for(Definition d: definitions) {
			isSaved &= d.isSaved();
		}
		return isSaved && !settingsChanged && !modelsChanged;
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
		TextualDefinition sut = new TextualDefinition(p, TextualDefinition.DefType.SUT);
		TextualDefinition adap = new TextualDefinition(p, TextualDefinition.DefType.ADAP);
		TextualDefinition spec = new TextualDefinition(p, TextualDefinition.DefType.SPEC);
		TextualDefinition type = new TextualDefinition(p, TextualDefinition.DefType.TYPE);
		TextualDefinition constDef = new TextualDefinition(p, TextualDefinition.DefType.CONST);
		TextualDefinition func = new TextualDefinition(p, TextualDefinition.DefType.FUNC);
		TextualDefinition proc = new TextualDefinition(p, TextualDefinition.DefType.PROC);
		
		p.addDefinition(type);
		p.addDefinition(constDef);
		p.addDefinition(func);
		p.addDefinition(spec);
		p.addDefinition(adap);
		p.addDefinition(sut);
		p.addDefinition(proc);
		p.addDefinition(m);
		return p;
	}
	
	private void setConfiguration(SetConfigAction a) {
		for(int c = 0; c < a.objects.length; c++) {
			for(int i = 0; i < a.attributes.length; i++){
				a.objects[c].setAttribute(a.attributes[i], a.values[i][c]);
			}
		}
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

}

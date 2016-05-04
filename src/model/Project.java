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
		Model m = Model.newModel(p, "Model 1");
		p.addModel(m);
		return p;
	}
	
	private void setConfiguration(SetConfigAction a) {
		for(int c = 0; c < a.objects.length; c++) {
			for(int i = 0; i < a.attributes.length; i++){
				a.objects[c].setAttribute(a.attributes[i], a.values[i][c]);
			}
		}
	}

	public void addModel(Model m) {
		definitions.add(m);
		modelsChanged = true;
	}

	public void removeModel(Model m) {
		definitions.remove(m);
		modelsChanged = true;
	}

	public ArrayList<Model> getModels() {
		ArrayList<Model> models = new ArrayList<Model>();
		for(Definition d : definitions){
			if( d instanceof Model){
				models.add((Model)d);
			}
		}
		return models;
	}

}

package io.file;


public class FileType {
	private String description;
	private String[] extensions;
	private Class<? extends Importer> importer;
	private Class<? extends Exporter> exporter;
	
	public FileType(String description, String[] extensions, Class<?> porter) {
		this.description = description;
		this.extensions = extensions;
		if(Importer.class.isAssignableFrom(porter)) {
			this.importer = (Class<? extends Importer>) porter;
			this.exporter = null;
		} else if(Exporter.class.isAssignableFrom(porter)) {
			this.importer = null;
			this.exporter = (Class<? extends Exporter>) porter;
		} else {
		}
		this.description = description;
		this.extensions = extensions;
	}
	
	public FileType(Class<?> porter) {
		this("", new String[] {}, porter);
	}

	public String getDescription() {
		return description;
	}
	public String[] getFileExtensions() {
		return extensions;
	}
	
	public Exporter getExporter() {
		try {
			return (Exporter) exporter.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Importer getImporter() {
		try {
			return (Importer) importer.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}

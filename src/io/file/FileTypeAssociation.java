package io.file;

import io.file.graph.exporter.GraphExporterJPEG;
import io.file.graph.exporter.GraphExporterPNG;
import io.file.graph.exporter.GraphExporterSVG;
import io.file.project.exporter.ProjectExporterGTorX;
import io.file.project.importer.ProjectImporterGTorx;

public enum FileTypeAssociation {
	ProjectImport(new FileType[] {
			new FileType("TorXakis Project (.gtxs)", new String[] {"gtxs"}, ProjectImporterGTorx.class)
		}), 
	ProjectExport(new FileType[] {
			new FileType("TorXakis Project (.gtxs)", new String[] {"gtxs"}, ProjectExporterGTorX.class)
		}), 
	PngExport(new FileType[] {
			new FileType("Portable Network Graphics (.png)", new String[] {"png"}, GraphExporterPNG.class),
		}),
	JpgExport(new FileType[] {
			new FileType("JPEG (.jpeg)", new String[] {"jpeg"}, GraphExporterJPEG.class),
		}),
	SvgExport(new FileType[] {
			new FileType("Scalable Vector Graphics (.svg)", new String[] {"svg"}, GraphExporterSVG.class)
		});
	
	private static final int DEFAULT_INDEX = 0;
	public final FileType[] fileTypes;
	private FileTypeAssociation(FileType[] fileTypes) {
		this.fileTypes = fileTypes;
	}
	
	public FileType getDefaultFileType() {
		return fileTypes[DEFAULT_INDEX];
	}
	
	public FileType getByFileExtension(String extension) {
		for(FileType fileType : fileTypes) {
			for(String fileExtension : fileType.getFileExtensions()) {
				if(fileExtension.equals(extension)) {
					return fileType;
				}
			}
		}
		return null;
	}

	public String getExtension(String filename) {
		String[] segments = filename.split("\\.");
		if(segments.length == 0) {
			return null;
		}
		return segments[segments.length-1];
	}

	public FileType getByFilename(String filename) {
		return getByFileExtension(getExtension(filename));
	}
	
	public FileType getByDescription(String description) {
		for(FileType fileType : fileTypes) {
			if(fileType.getDescription().equals(description)) {
				return fileType;
			}
		}
		return null;
	}

	/**
	 * Determine whether a given filename matches any file type of this association
	 */
	public boolean matches(String filename) {
		return getByFilename(filename) != null;
	}
	
	
}

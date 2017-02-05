package util;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import core.Session;

public class Environment {
	public enum OS {
		Windows, Mac, Linux;
		
		public static OS determine() {
			String OperatingSystem = System.getProperty("os.name").toLowerCase();
		    if (OperatingSystem.contains("win"))
		        return Windows;
		    else if (OperatingSystem.contains("mac"))
		        return Mac;
		    else if (OperatingSystem.contains("nux"))
		        return Linux;
		    return null;
		}
	}
	
	public static final OS OperatingSystem = OS.determine();
	public static final String fileSeparator = System.getProperty("file.separator");
	public static final String applicationDataDir = getApplicationDataFolder();
	public static final String tempDataDir = getTempDataFolder();
	public static final String helpDataDir = getHelpDataFolder();
	
	public static String getApplicationDataFolder() {
		String directory;
		switch(OperatingSystem) {
			case Windows:
				directory = System.getenv("AppData") + fileSeparator + Session.PROGRAM_NAME;
				break;
			case Mac:
				directory = System.getProperty("user.home") + fileSeparator + "Library" + fileSeparator + "Application Support" + fileSeparator + Session.PROGRAM_NAME;
				break;
			case Linux:
				directory = System.getProperty("user.home") + fileSeparator + "." + Session.PROGRAM_NAME;
				break;
			default:
				directory = System.getProperty("user.dir") + fileSeparator + Session.PROGRAM_NAME;
				break;
		}
		createDir(directory);
		return directory;
	}
	
	public static String getTempDataFolder() {
		String directory = applicationDataDir + fileSeparator + "temp";
		createDir(directory);
		return directory;
	}
	
	public static String getHelpDataFolder() {
		String directory = applicationDataDir + fileSeparator + "help";
		createDir(directory);
		return directory;
	}
	
	private static void createDir(String directory) {
		FileSystem fs = FileSystems.getDefault();
		Path path = fs.getPath(directory);
		if(Files.notExists(path)) {
			try {
				Files.createDirectory(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}


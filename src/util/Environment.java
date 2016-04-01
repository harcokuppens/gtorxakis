package util;

import java.net.InetAddress;
import java.net.UnknownHostException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.File;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import core.Session;
import gui.window.Window;

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
	private static String computerID;
	
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

	public static String getWinRegEntry(String location, String key) {
		String value = null;
		try {
			if(OperatingSystem != OperatingSystem.Windows) {
				System.out.println("Error: Trying to read registry value for non-windows OS.");
				return null;
			}
			BufferedReader br = exec("reg",
				"query",
				"\""+location+"\"",
				"/v",
				"\""+key+"\"");
			value = parseWinRegistryKey(br, key);
		} catch(IOException e) {
			System.out.println("Error occurred while trying to obtain windows registry entry.");
			e.printStackTrace();
		}
		return value;
	}
	
	private static void createDir(String directory) {
		FileSystem fs = FileSystems.getDefault();
		Path path = fs.getPath(directory);
		if(Files.notExists(path)) {
			try {
				Files.createDirectory(path);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static String getComputerID() {
		if(computerID == null) {
			computerID = obtainComputerID();
		}
		return computerID;
	}

	private static String obtainComputerID() {
		String computerID = null;
		BufferedReader br;
		try {
			switch(OperatingSystem) {
				case Windows:
					br = exec("mountvol", "C:/", "/L");
					computerID = parseWinResult(br);
					break;
				case Linux:
					br = exec("cat", "/etc/fstab");
					computerID = parseLinuxResult(br);
					break;
				case Mac:
					br = exec("/usr/sbin/system_profiler", "SPHardwareDataType");
					computerID = parseMacResult(br);
					break;
				default: 
					// unknown OS. Will use fallback id.
					System.err.println("[Environment] Running on an unknown OS. Will use fallback id.");
			}
		} catch(IOException e) {
			e.printStackTrace();
			// failed to determine strong uuid.
			// will use fallback id.
		}
		if(computerID == null) {
			computerID = getFallbackID();
		}
		return computerID;
	}

	/**
	 * Executes a platform-dependent command and returns a reader from which the output of
	 * the issued command can be obtained.
	 * @param  command The command to be executed
	 * @return         The buffered reader that gives access to the returned results
	 */
	private static BufferedReader exec(String... commands) throws IOException {
		Runtime runtime = Runtime.getRuntime();
		Process process = runtime.exec(commands);
		OutputStream os = process.getOutputStream();
		InputStream is = process.getInputStream();
		os.close();
		return new BufferedReader(new InputStreamReader(is));
	}

	private static String parseMacResult(BufferedReader br) throws IOException {
		String pattern = "Hardware UUID: ";
		String uuid = null;
		String line = br.readLine();
		while(line != null) {
			if(line.contains(pattern)) {
				uuid = line.substring(line.indexOf(pattern) + pattern.length());
				break;
			}
			line = br.readLine();
		}
		br.close();
		return uuid;
	}

	private static String parseWinResult(BufferedReader br) throws IOException {
		String pattern = "Volume{";
		String uuid = null;
		String line = br.readLine();
		System.out.println("Parsing output...");
		while(line != null) {
			System.out.println(line);
			if(line.contains(pattern)) {
				// parsing line that looks something like:
				//  \\?\Volume{<disk uuid>}\
				line = line.trim();
				int start = line.indexOf(pattern);
				int end = line.indexOf('}', start);
				if(start != -1 && end != -1) {
					uuid = line.substring(start + pattern.length(), end);
				}
			}
			line = br.readLine();
		}
		br.close();
		return uuid;
	}

	private static String parseWinRegistryKey(BufferedReader br, String key) throws IOException {
		String value = null;
		Pattern p = Pattern.compile(
				"\\s*"+key+"\\s+"+
				// Match on any valid registry data type
				// (valid according to https://msdn.microsoft.com/en-us/library/windows/desktop/bb773476(v=vs.85).aspx )
				"(REG_BINARY|REG_DWORD|REG_DWORD_BIG_ENDIAN|REG_QWORD|REG_QWORD_LITTLE_ENDIAN|"+
					"REG_EXPAND_SZ|REG_LINK|REG_MULTI_SZ|REG_NONE|REG_RESOURCE_LIST|REG_SZ)"+
				"\\s+(\\S+)"
			);
		String line = br.readLine();
		System.out.println("Parsing output...");
		while(line != null) {
			Matcher m = p.matcher(line);
			if(m.matches()) {
				value = m.group(2);
				if(value.equalsIgnoreCase("NULL")) {
					value = null;
				}
			}
			line = br.readLine();
		}
		br.close();

		return value;
	}

	private static String parseLinuxResult(BufferedReader br) throws IOException {
		// Goal: find the disk with mounting point / (the root dir) and fetch 
		// its UUID.
		 
		String uuid = null;
		String line = br.readLine();
		while(line != null) {
			line = line.trim();
			if(line.length() > 0 && line.charAt(0) != '#') {
				// make sure that line is not empty and is not a comment.

				// Split on whitespaces
				String[] attributes = line.split("\\s");
				if(attributes.length >= 2) {
					// we need at least a UUID and a mounting point
					if(attributes[1].equals("/")) {
						uuid = attributes[0];
						if(uuid.startsWith("UUID=")) {
							uuid = uuid.substring(5);
						}
						// No need to check subsequent lines
						break;
					}
				}
			}
			line = br.readLine();
		}
		br.close();
		return uuid;
	}

	/**
	 * In case the primary hardware ID cannot be obtained for some reason,
	 * the name of some network device will be used as hardware ID.
	 * @return The name of some network device, or null if no network device could be found.
	 */
	private static String getFallbackID() {
		String computerID;
		try {
			System.err.println("[Environment] Error while trying to obtain Hardware ID. Falling back to network hostname.");
			computerID = InetAddress.getLocalHost().getHostName();
		} catch(UnknownHostException e) {
			System.err.println("[Environment] Error while trying to obtain network hostname.");
			computerID = null;
			throw new Error("Your computer hardware could not be identified.\n"
				+"Please repeat the activation process while you are connected to the Internet.");
		}
		return computerID;
	}
}


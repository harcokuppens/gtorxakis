package core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class gives the possibility to log certain events in a log-file. To do
 * so first a new instance of this class has to be created. After that a static
 * reference to this instance can be used by using Logger.getInstance() to
 * append an entry to the log-file.
 * 
 * @author Lars Bade
 * 
 */
public class Logger {
	/**
	 * The path to the log-file to be used
	 */
	private static final String LOGFILE_PATH = "logger.log";

	/**
	 * A static reference to an instance of this class
	 */
	private static Logger instance;

	/**
	 * Constructor. Sets the static reference to this instance.
	 */
	public Logger() {
		Logger.instance = this;
	}

	/**
	 * 
	 * @return the static reference to an instance of this class
	 */
	public static Logger getInstance() {
		return Logger.instance;
	}

	/**
	 * Appends a new entry to the log file. This entry is of the form
	 * "DD-MM-YYYY HH:II:SS: message", where DD-MM-YYYY HH:II:SS is replaced by
	 * the current date and time and message is replaced by the specified
	 * String.
	 * 
	 * @param message
	 *            the message to be logged
	 */
	public void appendLog(String message) {
		File file = new File(Logger.LOGFILE_PATH);
		try {
			BufferedWriter output = new BufferedWriter(new FileWriter(file,
					true));
			DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			String log = df.format(new Date()) + ": " + message
					+ System.lineSeparator();
			output.write(log);
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

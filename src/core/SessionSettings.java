package core;

import io.net.SocketIO.TorXakisType;

import java.util.InputMismatchException;

public class SessionSettings {
	
	private int port,
				iterations;
	private String host,
				   model,
				   connection,
				   torxakisDirectory;
	private TorXakisType torxakisType;
	
	public static final String HOST = "host",
							   PORT = "port",
							   ITERATIONS = "iterations",
							   MODEL = "model",
							   CONNECTION = "connection",
							   TORXAKIS_TYPE = "torxakis",
							   TORXAKIS_DIRECTORY = "directory";
	
	public SessionSettings (int port, int iterations, String host, String model, String connection, TorXakisType torxakisType, String directory){
		this.port = port;
		this.iterations = iterations;
		this.host = host;
		this.model = model;
		this.connection = connection;
		this.torxakisType = torxakisType;
		this.torxakisDirectory = directory;
	}
	
	public static SessionSettings getDefaultSettings(){
		return new SessionSettings(7220, 100, "localhost", "", "", TorXakisType.TESTER, Session.DEFAULT_PATH);
	}
	
	public String getAttribute(String cmd){
		switch(cmd){
		case HOST:
			return host;
		case PORT:
			return port +"";
		case MODEL:
			return model;
		case CONNECTION:
			return connection;
		case ITERATIONS:
			return iterations+"";
		case TORXAKIS_TYPE:
			return torxakisType.name();
		case TORXAKIS_DIRECTORY:
			return torxakisDirectory;
			default:
				throw new InputMismatchException("[SessionSettings] No such attribute!");
		}
	}
	
	public void setAttribute(String cmd, Object value){
		switch(cmd){
		case HOST:
			host = String.valueOf(value);
		case PORT:
			port = (int) value;
		case MODEL:
			model = String.valueOf(value);
		case CONNECTION:
			connection = String.valueOf(value);
		case ITERATIONS:
			iterations = (int) value;
		case TORXAKIS_TYPE:
			torxakisType = TorXakisType.valueOf(String.valueOf(value));
		case TORXAKIS_DIRECTORY:
			torxakisDirectory = String.valueOf(value);
			default:
				throw new InputMismatchException("[SessionSettings] No such attribute!");
		}
	}

}

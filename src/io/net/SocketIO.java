package io.net;

import gui.dialogs.RunDialog;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JOptionPane;

import core.Session;

public class SocketIO {

	private int port;
	private String host;
	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;
	private RunDialog runDialog;
	
	private boolean started = false;
	private TorXakisType currentType;
	
	public static enum TorXakisType{
		TESTER("TESTER", "TEST"),
		SIMULATOR("SIMULATOR", "SIM"),
		STEPPER("STEPPER", "STEP");
		
		private String cmd, runCMD;
		
		private TorXakisType(String cmd, String runCMD){
			this.cmd = cmd;
			this.runCMD = runCMD;
		}
		
		public String getInitCommand(String model, String connection){
			switch(this){
			case TESTER:
			case SIMULATOR:
				return cmd + " " + model + " " + connection;
			default:
				return cmd + " " + model;
			}
		}
		
		public String getRunCommand(int iterations){
			switch(this){
			case SIMULATOR:
				return runCMD;
			default:
				return runCMD + " " + String.valueOf(iterations);
			}
		}
	}
	
	public SocketIO(RunDialog runDialog,int port, String host) throws Exception{
		this.host = host;
		this.port = port;
		this.runDialog = runDialog;
		listenSocket();
	}
	

	public void listenSocket() throws Exception{
	//Create socket connection
	   try{
	     socket = new Socket(host, port);
	     writer = new PrintWriter(socket.getOutputStream(), true);
	     reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	   } catch(Exception e) {
		 JOptionPane.showMessageDialog(null, "Can not connect to TorXakis. Are you sure that you pick the right directory?");
		 runDialog.shutdown();
	   }
	}
	
	public BufferedReader getReader(){
		return reader;
	}

	public boolean isStarted(){
		return started;
	}
	
	public boolean typeChanged(TorXakisType type){
		return !type.equals(currentType);
	}
	
	public void changeTorXakisType(TorXakisType type, String model, String connection){
		if(currentType != null) writer.println("STOP");
		currentType = type;
		writer.println(type.getInitCommand(model, connection));
		writer.flush();
	}
	
	public void run(int iterations){
		writer.println(currentType.getRunCommand(iterations));
		writer.flush();
	}
	
	public void startTorXakis(String filename){
		writer.println("START");
		writer.println("INIT " + filename);
		started = true;
	}
	
	public void quitTorXakis(){
		writer.println("QUIT");
		writer.flush();
		started = false;
	}
	
	public void sendCommand(String cmd){
		writer.println(cmd);
		writer.flush();
	}
	
	public void close(){
		try {
			System.out.println("socket closed");
			writer.close();
			reader.close();
			socket.close();
		} catch (Exception e) {
//			e.printStackTrace();
			System.err.println("Socket closed with errors");
		}
	}
	
	public void startTorXakis(TorXakisType type, String model, String connection){
		try{
			if(!isStarted()){
				System.err.println("Try to start socketIO");
				startTorXakis(Session.TEMP_TXS);
			}					
		}catch(Exception exception){
			runDialog.shutdown();
			return;
		}
		if(typeChanged(type)){
			changeTorXakisType(type, model, connection);
		}
	}
}

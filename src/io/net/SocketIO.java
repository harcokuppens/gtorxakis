package io.net;

import gui.dialogs.RunDialog;
import gui.dialogs.RunDialog.TorXakisType;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

public class SocketIO {

	private int port;
	private String host;
	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;
	private RunDialog runDialog;
	
	public SocketIO(RunDialog runDialog,int port, String host){
		this.host = host;
		this.port = port;
		this.runDialog = runDialog;
		listenSocket();
	}
	

	public void listenSocket(){
	//Create socket connection
	   try{
	     socket = new Socket(host, port);
	     writer = new PrintWriter(socket.getOutputStream(), true);
	     reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	   } catch(Exception e) {
		 runDialog.destroyCMD();
	     JOptionPane.showMessageDialog(null, "Can not connect to TorXakis. Are you sure that you pick the right directory?");
	   }
	}
	
	public BufferedReader getReader(){
		return reader;
	}

	public void startTorXakis(String filename, String model, String connection, int iterations, TorXakisType type){
			writer.println("INIT " + filename);
			System.out.println("INIT "+ filename);
			writer.println(type.getInitCommand(model, connection));
			writer.println(type.getRunCommand(iterations));
			writer.println("QUIT");
			writer.flush();
	}
	
	public void close(){
		try {
			writer.close();
			reader.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

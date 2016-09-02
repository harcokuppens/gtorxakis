package io.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketIO {

	private int port;
	private String host;
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	
	public SocketIO(int port, String host){
		this.host = host;
		this.port = port;
		listenSocket();
	}
	

	public void listenSocket(){
	//Create socket connection
	   try{
	     socket = new Socket(host, port);
	     out = new PrintWriter(socket.getOutputStream(), 
	                 true);
	     in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	   } catch (UnknownHostException e) {
	     System.out.println("Unknown host: " + host);
	   } catch  (IOException e) {
	     System.out.println("No I/O");
	   }
	}
	

	public void startTorXakis(String filename){
		File f = new File(filename);
		
//		out.println(text);
		try{
			String line = in.readLine();
			System.out.println("Text received: " + line);
		} catch (IOException e){
			System.out.println("Read failed");
			System.exit(1);
		}
	}
	
	public void close(){
		try {
			in.close();
			out.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
}

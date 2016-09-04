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
	private PrintWriter writer;
	private BufferedReader reader;
	
	public SocketIO(int port, String host){
		this.host = host;
		this.port = port;
		listenSocket();
	}
	

	public void listenSocket(){
	//Create socket connection
	   try{
	     socket = new Socket(host, port);
	     writer = new PrintWriter(socket.getOutputStream(), true);
	     reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	     writer.println("INIT");
	   } catch (UnknownHostException e) {
	     System.out.println("Unknown host: " + host);
	   } catch  (IOException e) {
	     System.out.println("No I/O");
	   }
	}
	

	public void startTorXakis(String filename, String model, String connection, int iterations){
		//TODO send path of file,
		//send model, connect name
		//send iterations
		//writer.println(text);
//		try{
//			String line = reader.readLine();
//			System.out.println("Text received: " + line);
//		} catch (IOException e){
//			System.out.println("Read failed");
//			System.exit(1);
//		}
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

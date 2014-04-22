package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.HashMap;

public class ServerNode {
    private int port;
	private ServerSocket serverSocket = null;
	private HashMap<String, ServerClient> allClients = null;
	
	public ServerNode( int p ) { port = p; }
	
	public void setServerPort() throws IOException {
		serverSocket = new ServerSocket(port);
	}
	
	public void setClientSocket() throws IOException {
		Socket clientSocket = serverSocket.accept();
        ServerClient client = new ServerClient(clientSocket);
        Thread clientThread = new Thread(client);
        
        clientThread.start();
        //allClients.put(client.getEmail(), client);
	}
	
	public void close() throws Exception {
		serverSocket.close();
	}
	
	public void run() {
		try {
			setServerPort();
			setClientSocket();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public class ServerClient implements Runnable {
		private Socket client;
		private ObjectOutputStream out;
		private ObjectInputStream in;
		
		public ServerClient(Socket sock) {
			client = sock;
			try {
				out = new ObjectOutputStream( client.getOutputStream() );
				in = new ObjectInputStream( client.getInputStream() );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
		
		public void close() {
			try {
				in.close();
				out.close();
				client.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		public String getEmail() {
			String email = "test";
			
			try {
				email = (String) in.readObject();
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("email is " + email);
			return email;
		}
		
		public void sendMsg() {
			try {
				out.writeObject("I hear!");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void run() {
			String tmp = null;
			try {
				tmp = (String) in.readObject();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(tmp);
		}
	}
}

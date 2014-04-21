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
        allClients.put(client.getEmail(), client);
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
		
		public ServerClient(Socket sock) {
			client = sock;
		}
		
		public String getEmail() {
			return "";
		}
		
		public void run() {
			
		}
	}
}

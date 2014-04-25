package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.HashMap;

import dbLayout.TidepoolDatabase;
import entities.User;

public class ServerNode {
    private int port;
	private ServerSocket serverSocket = null;
	private HashMap<String, ServerClient> allClients = null;
	
	private TidepoolDatabase db = new TidepoolDatabase();
	
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
		
		private User user = new User();
		private String email=null;
		
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
		
		public void getEmail() {
			try {
				email = (String) in.readObject();
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("email is " + email);
		}
		
		public void sendMsg() {
			try {
				out.writeObject("I hear!");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		private boolean getUser() {
			user = db.getUser(email);
			if(user!=null) {
				System.out.println(user.getId() + " " + user.getUsername());
				return true;
			}
			
			return false;
		}
		
		public void signin() {
			
		}
		
		public void register() {
			
		}
		
		public void run() {
			String tmp = null;
			
			getUser();
			try {
				System.out.println("Start connection!");
				while((tmp = (String) in.readObject())!=null){
					if(tmp.equalsIgnoreCase("signout")) break;
					
					if(tmp.equalsIgnoreCase("signin")) signin();
					if(tmp.equalsIgnoreCase("register")) register();
				}
				
				allClients.remove(email);
				close();
				System.out.println("Close connection!");
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

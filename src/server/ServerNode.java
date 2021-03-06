package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

import com.tidepool.entities.User;

import dbLayout.TidepoolDatabase;

public class ServerNode {
	private int port;
	private ServerSocket serverSocket = null;
	private static HashMap<String, ServerClient> allClients = new HashMap<String, ServerClient>();

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
	}

	public void close() throws Exception {
		db.close();
		serverSocket.close();
	}

	public void run() {
		try {
			setServerPort();
			while(true)
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
				if(!allClients.isEmpty() && allClients.get(email)!=null)
					allClients.remove(email);
				in.close();
				out.close();
				client.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		private void getEmail() {
			try {
				email = (String) in.readObject();
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("email is " + email);
		}

		private boolean getUser() {
			user = db.getUser(email);
			if(user.getUsername()!=null) {
				System.out.println(user.getId() + " " + user.getUsername());
				return true;
			}

			return false;
		}


		public void signin() {
			try {
				// Receive email and pwd
				out.writeObject("email");
				getEmail();
				if(email==null) {
					out.writeObject("Fail to receive email!");
					return;
				}

				// Find the user
				if(!getUser()) {
					out.writeObject("No such user!");
					return;
				}

				// Receive the pwd
				out.writeObject("pwd");
				String pwd = (String) in.readObject();
				if(!pwd.equals(user.getPassword())) {
					out.writeObject("Wrong Password!");
					return;
				}

				// Save and send back the User
				allClients.put(email, this);
				out.writeObject(user);
				System.out.println("signin success!");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void register() {
			try {
				// Receive the user
				out.writeObject("user");
				user = (User) in.readObject();

				// Add the user
				int id = db.addUser(user);
				if(id==-1) {
					out.writeObject("Duplicate User");
					return;
				}

				// Save and send back the user id
				user.setId(id);
				allClients.put(email, this);
				out.writeObject(String.valueOf(id));

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void update() {
			try {
				// Receive the user
				out.writeObject("update user");
				user = (User) in.readObject();

				// Add the user
				int res = db.updateUser(user);
				System.out.println("lat is " + user.getLocation_lat());
				System.out.println("lng is " + user.getLocation_lng());
				out.writeObject(res);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void sendData() {
			try {
				out.writeObject("userId");
				long id = (long) in.readObject();
				out.writeObject(db.getData(id));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void sendUser() {
			try {
				// Receive the user
				out.writeObject("get uid");
				long uid = (Long)in.readObject();

				// Add the user
				User user = db.getUser(uid); 
				out.writeObject(user);

			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		public void sendFriends() {
			try {
				out.writeObject(db.getFriends(user.getId()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void sendMsgProcess() {
			try {
				// Get friend email
				out.writeObject("friend email");
				String friendEmail = (String) in.readObject();

				// Maintain the friend
				if(friendEmail==null || allClients.get(friendEmail)==null) {
					out.writeObject("No such friend!");
					return;
				}

				// Receive message
				out.writeObject("msg");
				String msg = (String) in.readObject();

				// Send message to the friend
				allClients.get(friendEmail).sendMsg(msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void sendMsg(String msg) {
			try {
				out.writeObject(msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void sendRequest() {
			try {
				out.writeObject("friend email");
				long id1 = user.getId();
				String friendEmail = (String) in.readObject();

				//Find the friend in the database
				User friend = db.getUser(friendEmail);
				if(friendEmail==null || friend==null) {
					out.writeObject("No such user!");
					return;
				}

				// Add the request to contact
				int res = db.sendRequest(id1, friend.getId());
				out.writeObject(res);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void receiveRequest() {
			try {
				// Return the request list to receiver
				ArrayList<User> res = db.getRequest(user.getId());
				out.writeObject(res);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void sendRespond() {
			try {
				out.writeObject("sender id");
				long friendId = (long) in.readObject();

				//Get the respond, either "admit" or "refuse"
				out.writeObject("respond");
				String status = (String) in.readObject();
				if(status.equals("admit"))
					db.addFriend(user.getId(), friendId);
				else if(!status.equals("refuse")) // Error
					return;

				// Update the request to contact
				int res = db.setRespond(friendId, user.getId(), status);
				out.writeObject(res);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void receiveRespond() {
			try {
				// Get the respond from receivers
				ArrayList<User> res = db.getRespond(user.getId());
				out.writeObject(res);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void deleteFriend() {
			try {
				out.writeObject("friend email");
				long id1 = user.getId();
				String friendEmail = (String) in.readObject();

				//Find the friend in the database
				User friend = db.getUser(friendEmail);
				if(friendEmail==null || friend==null) {
					out.writeObject("No such friend!");
					return;
				}

				// Delete the friend relationship
				db.deleteFriends(id1, friend.getId());
				out.writeObject("success");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void run() {
			String tmp = null;

			try {
				System.out.println("Start connection!");
				while((tmp = (String) in.readObject())!=null){
					System.out.println("From Client-" + email + ": " + tmp);
					if(tmp.equalsIgnoreCase("signout")) break;

					if(tmp.equalsIgnoreCase("signin")) signin();
					if(tmp.equalsIgnoreCase("register")) register();
					if(tmp.equalsIgnoreCase("updateUser")) update();
					if(tmp.equalsIgnoreCase("sendData")) sendData();
					if(tmp.equalsIgnoreCase("sendUser")) sendUser();
					if(tmp.equalsIgnoreCase("sendFriends")) sendFriends();
					if(tmp.equalsIgnoreCase("chat")) sendMsgProcess();

					//For add friends
					if(tmp.equalsIgnoreCase("sendRequest")) sendRequest();
					if(tmp.equalsIgnoreCase("receiveRequest")) receiveRequest();
					if(tmp.equalsIgnoreCase("sendRespond")) sendRespond();
					if(tmp.equalsIgnoreCase("receiveRespond")) receiveRespond();
					if(tmp.equalsIgnoreCase("deleteFriend")) deleteFriend();

				}

				close();
				System.out.println("Close connection!");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}

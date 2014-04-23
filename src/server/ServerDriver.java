package server;

public class ServerDriver {
	public static void main(String[] args) {
		ServerNode server = new ServerNode(5555);
		server.run();
	}
}

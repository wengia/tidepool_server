package server;

public class ServerDriver {
	public static void main(String[] args) {
		ServerNode server = new ServerNode(1234);
		server.run();
	}
}

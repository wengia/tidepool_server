package dbLayout;

import java.sql.SQLException;
import java.util.ArrayList;

import entities.Data;
import entities.User;


public class TidepoolDatabase {
	private JDBCadapter adapter;
	private String URL;
	private String driver;
	private String user;
	private String passwd;

	public TidepoolDatabase() {
		URL = "jdbc:mysql://localhost:3306/tidepool";
		driver = "com.mysql.jdbc.Driver";
		user = "root";
		passwd = "1111";

		adapter = new JDBCadapter(URL, driver, user, passwd);
	}
	
	public User getUser(String email) {
		return adapter.selectUser(email);
	}
	
	/**
	 * Return the data of the patient or patients associated with the parent
	 * @param user
	 * @return data
	 */
	public ArrayList<Data> getData(User user) {
		if(user.getRole().equalsIgnoreCase("patient"))
			return adapter.selectData((int)user.getId());
		
		// Get the data of the patients associated with the parent
		ArrayList<User> friends = adapter.selectFriends((int)user.getId());
		ArrayList<Data> data = new ArrayList<Data>();
		for(User friend: friends) {
			data.addAll(adapter.selectData((int)friend.getId()));
		}
		return data;
	}
	
	/**
	 * Find all friends of one user
	 * @param uid
	 * @return friends
	 */
	public ArrayList<User> getFriends(int uid) {
		return adapter.selectFriends(uid);
	}
	
	public void close() throws SQLException {
		adapter.close();
	}
}

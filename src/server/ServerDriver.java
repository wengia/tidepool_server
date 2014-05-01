package server;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.tidepool.entities.User;

import dbLayout.TidepoolDatabase;

public class ServerDriver {
	public static void main(String[] args) {
		ServerNode server = new ServerNode(5555);
		server.run();
		
		// new ServerDriver().testDB();
	}
	
	public void testDB() {
		TidepoolDatabase db = new TidepoolDatabase();
		
		// Insert User
		/*User userDummy = new User();
		userDummy.setId(6);
		userDummy.setEmail("becky@gmail.com");
		userDummy.setGender("female");
		userDummy.setPassword("pwd");
		userDummy.setPhoneNo("123");
		userDummy.setRole("patient");
		userDummy.setUsername("becky");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = "1992-01-01";
		try {
			userDummy.setDateOfBirth(formatter.parse(dateStr));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		db.addUser(userDummy);
		
		// Update user;
		userDummy.setUsername("becky");
		int res = db.updateUser(userDummy);
		System.out.println(res);*/
		
		// Contact sending requests
		int tmp;
		tmp = db.sendRequest(2,1);
		System.out.println("Send 2 to 1: " + tmp);
		tmp = db.sendRequest(2,3);
		System.out.println("Send 2 to 3: " + tmp);
		
		ArrayList<User> r1 = db.getRequest(1);
		System.out.println("get " + r1.size());
		
		ArrayList<User> r2 = db.getRequest(2);
		System.out.println("get " + r2.size());
		
		db.setRespond(2, 1, "admit");
		db.setRespond(2, 3, "refuse");
		
		r2 = db.getRespond(2);
		System.out.println("get " + r2.size());
		
		
		// Delete Friends
		db.deleteFriends(1,2);
		
	}
}

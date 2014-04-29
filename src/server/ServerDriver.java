package server;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.tidepool.entities.User;

import dbLayout.TidepoolDatabase;

public class ServerDriver {
	public static void main(String[] args) {
		ServerNode server = new ServerNode(5555);
		server.run();
		
		//new ServerDriver().testDB();
	}
	
	public void testDB() {
		TidepoolDatabase db = new TidepoolDatabase();
		
		// Insert User
		User userDummy = new User();
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
		System.out.println(res);
	}
}

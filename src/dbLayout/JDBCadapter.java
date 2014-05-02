package dbLayout;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.tidepool.entities.Data;
import com.tidepool.entities.User;



public class JDBCadapter {
	/**
	 * The IO to database
	 */
	
	private Connection connection;
	private PreparedStatement preparedStatement = null;
	
	public JDBCadapter(String url, String driverName, String user, String passwd) {
		try {
			Class.forName(driverName);
			System.out.println("Opening db connection");
			
			connection = DriverManager.getConnection(url, user, passwd);
		}
		catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	public void close() throws SQLException {
        System.out.println("Closing db connection");
        preparedStatement.close();
        connection.close();
    }
	
	/**
	 * Select the user by the email
	 * @param email
	 * @return one user
	 * @throws SQLException 
	 */
	public User selectUser( String email ) {
		User user = new User();
		String query = "select * from myUser " + "where email = ?";
		
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, email);
			ResultSet rs = preparedStatement.executeQuery();
			
			if( rs.next() ) {
				user.setId(rs.getInt("id"));
				user.setEmail(rs.getString("email"));
				user.setUsername(rs.getString("username"));
				user.setPassword(rs.getString("pwd"));
				user.setPhoneNo(rs.getString("phone"));
				user.setDateOfBirth(rs.getDate("birth"));
				user.setGender(rs.getString("gender"));
				user.setRole(rs.getString("role"));
				
				System.out.print("Reading user " + user.getUsername() + " success!" );
				System.out.println(" Birthday is: " + user.getDateOfBirth());
			}
			
			rs.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		
		return user;
	}
	
	/**
	 * Select all the data of one user
	 * @param user_id
	 * @return data list
	 */
	public ArrayList<Data> selectData( long user_id ) {
		ArrayList<Data> dataList = new ArrayList<Data>();
		String query = "select * from myData " + "where uid = ?";
		
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setLong(1, user_id);
			ResultSet rs = preparedStatement.executeQuery();
			
			while( rs.next() ) {
				Data data = new Data();
				
				data.setId(rs.getInt("id"));
				data.setTime(rs.getTimestamp("theTime"));
				data.setBg(rs.getInt("BG"));
				data.setInsulin(rs.getInt("insulin"));
				data.setUserId(user_id);
				
				System.out.println("Time " + data.getTime() + " BG " + data.getBg() );
				dataList.add(data);
			}
			
			rs.close();
		}
		catch (SQLException ex) {
			ex.printStackTrace();
		}
		
		return dataList;
	}
	
	/**
	 * Select the friends of one user
	 * @param id
	 * @return friends
	 */
	public ArrayList<User> selectFriends( long id ) {
		ArrayList<User> friends = new ArrayList<User>();
		
		friends.addAll( selectFriends(id, true) );
		friends.addAll( selectFriends(id, false) );
		
		return friends;
	}
	
	private ArrayList<User> selectFriends( long user_id, boolean right ) {
		ArrayList<User> userList = new ArrayList<User>();
		String query1 = "select * from myUser a " +
				"inner join friends b on a.id=b.uid1 " + 
				"where b.uid2 = ?";
		String query2 = "select * from myUser a " +
				"inner join friends b on a.id=b.uid2 " + 
				"where b.uid1 = ?";
		
		try {
			if(right)
				preparedStatement = connection.prepareStatement(query1);
			else
				preparedStatement = connection.prepareStatement(query2);
			preparedStatement.setLong(1, user_id);
			ResultSet rs = preparedStatement.executeQuery();
			
			while( rs.next() ) {
				User user = new User();
				
				user.setId(rs.getInt("id"));
				user.setEmail(rs.getString("email"));
				user.setUsername(rs.getString("username"));
				user.setPassword(rs.getString("pwd"));
				user.setPhoneNo(rs.getString("phone"));
				user.setDateOfBirth(rs.getDate("birth"));
				user.setGender(rs.getString("gender"));
				user.setRole(rs.getString("role"));
				
				System.out.print("Reading friend " + user.getUsername() + " success!" );
				System.out.println(" Birthday is: " + user.getDateOfBirth());
				userList.add(user);
			}
			
			rs.close();
		}
		catch (SQLException ex) {
			ex.printStackTrace();
		}
		
		return userList;
	}
	
	/**
	 * Sender get receivers' respond
	 * If status = "admit" return the user with full columns.
	 * If status = "refuse" return the user only with username.
	 * @param sender_id
	 * @return receivers
	 */
	public ArrayList<User> selectReceiver( long sender_id ) {
		ArrayList<User> userList = new ArrayList<User>();
		String query = "select * from myUser a " +
				"inner join contact b on a.id=b.receiver " + 
				"where not b.theStatus=\'wait\' and b.sender = ?";
		
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setLong(1, sender_id);
			ResultSet rs = preparedStatement.executeQuery();
			
			while( rs.next() ) {
				User user = new User();
				String status = rs.getString("theStatus");
				
				// Handle different respond
				if(status.equals("admit") ) {
					user.setId(rs.getInt("id"));
					user.setEmail(rs.getString("email"));
					user.setUsername(rs.getString("username"));
					user.setPhoneNo(rs.getString("phone"));
					user.setDateOfBirth(rs.getDate("birth"));
					user.setGender(rs.getString("gender"));
					user.setRole(rs.getString("role"));
					
					// Add to friends table
					insertFriends(sender_id, user.getId());
				}
				else {
					user.setUsername(rs.getString("username"));
				}
				
				// Delete the handling request
				int contact_id = rs.getInt("contact_id");
				deleteContact(contact_id);
				
				// For debug
				System.out.print("Reading contact " + user.getUsername() + " success!" );
				System.out.println(" status is: " + status);
				userList.add(user);
			}
			
			rs.close();
		}
		catch (SQLException ex) {
			ex.printStackTrace();
		}
		
		return userList;
	}
	
	/**
	 * Receiver gets all senders' request
	 * @param receiver_id
	 * @return senders
	 */
	public ArrayList<User> selectSender( long receiver_id ) {
		ArrayList<User> userList = new ArrayList<User>();
		String query = "select * from myUser a " +
				"inner join contact b on a.id=b.sender " + 
				"where b.theStatus=\'wait\' and b.receiver = ?";
		
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setLong(1, receiver_id);
			ResultSet rs = preparedStatement.executeQuery();
			
			while( rs.next() ) {
				User user = new User();
				String status = rs.getString("theStatus");
				
				user.setId(rs.getInt("id"));
				user.setEmail(rs.getString("email"));
				user.setUsername(rs.getString("username"));
				user.setPhoneNo(rs.getString("phone"));
				user.setDateOfBirth(rs.getDate("birth"));
				user.setGender(rs.getString("gender"));
				user.setRole(rs.getString("role"));
			
				System.out.print("Reading contact " + user.getUsername() + " success!" );
				System.out.println(" status is: " + status);
				userList.add(user);
			}
			
			rs.close();
		}
		catch (SQLException ex) {
			ex.printStackTrace();
		}
		
		return userList;
	}
	
	/**
	 * Insert new User
	 * If the user has not been created, the return will be -1.
	 * @param user
	 * @return user_id
	 */
	public int insertUser(User user) {
		int id = -1;
		String query = "INSERT IGNORE INTO myUser"
				+ "(email, username, pwd, phone, birth, gender, role) VALUES"
				+ "(?,?,?,?,?,?,?)";

		try {
			preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setString(1, user.getEmail());
			preparedStatement.setString(2, user.getUsername());
			preparedStatement.setString(3, user.getPassword());
			preparedStatement.setString(4, user.getPhoneNo());
			preparedStatement.setDate(5, convertToSqlDate(user.getDateOfBirth()));
			preparedStatement.setString(6, user.getGender());
			preparedStatement.setString(7, user.getRole());

			// execute insert SQL statement
			preparedStatement.executeUpdate();
			
			// return the auto generated key
			ResultSet rs = preparedStatement.getGeneratedKeys();
            if(rs.next()){
                id = rs.getInt(1);
                System.out.println("Record is inserted into myUser table!");
            }
            else
            	System.out.println("Fail to inserted record into myUser table!");
            
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return id;
	}
	
	/**
	 * Insert new friends relationship.
	 * In the table, first id < second id
	 * Return -1 if not inserting successfully.
	 * @param id1
	 * @param id2
	 * @return friends_id
	 */
	public int insertFriends(long id1, long id2) {
		int id = -1;
		String query = "INSERT IGNORE INTO friends"
				+ "(uid1, uid2) VALUES"
				+ "(?,?)";

		try {
			preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setLong(1, (id1<id2? id1:id2));
			preparedStatement.setLong(2, (id1>id2? id1:id2));

			// execute insert SQL statement
			preparedStatement.executeUpdate();
			
			// return the auto generated key
			ResultSet rs = preparedStatement.getGeneratedKeys();
            if(rs.next())
                id = rs.getInt(1);
            System.out.println("Record is inserted into friends table!");
    		    
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return id;
	}

	/**
	 * Insert the add friend request to the contact table
	 * @param sId
	 * @param rId
	 * @return rows affected
	 */
	public int insertContact(long sId, long rId) {
		int count = -1;
		String query = "INSERT IGNORE INTO contact"
				+ "(sender, receiver, theStatus) VALUES"
				+ "(?,?,?)";

		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setLong(1, sId);
			preparedStatement.setLong(2, rId);
			preparedStatement.setString(3, "wait");
			
			// execute insert SQL statement
			count = preparedStatement.executeUpdate();
			
            System.out.println("Add friends request is inserted into contact table!");    
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return count;
	}
	
	/**
	 * Update the user
	 * Return 0 if no row has been changed
	 * @param user
	 * @return row count
	 */
	public int updateUser(User user) {
		int ps = -1;
		String query = "UPDATE myUser SET "
				+ "email = ?, username = ?, pwd = ?, phone = ?, birth = ?, gender = ?, role =? "
				+ "WHERE id = ?";

		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, user.getEmail());
			preparedStatement.setString(2, user.getUsername());
			preparedStatement.setString(3, user.getPassword());
			preparedStatement.setString(4, user.getPhoneNo());
			preparedStatement.setDate(5, convertToSqlDate(user.getDateOfBirth()));
			preparedStatement.setString(6, user.getGender());
			preparedStatement.setString(7, user.getRole());
			preparedStatement.setLong(8, user.getId());
			
			// execute insert SQL statement
			ps = preparedStatement.executeUpdate();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ps;	
	}
	
	/**
	 * Update the contact
	 * Receiver gives the responds
	 * @param sId
	 * @param rId
	 * @param status
	 * @return affected row count
	 */
	public int updateContact(long sId, long rId, String status) {
		int ps = -1;
		String query = "UPDATE contact SET "
				+ "theStatus = ? "
				+ "WHERE sender = ? and receiver = ?";

		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, status);
			preparedStatement.setLong(2, sId);
			preparedStatement.setLong(3, rId);
			
			// execute insert SQL statement
			ps = preparedStatement.executeUpdate();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ps;	
	}
	
	/**
	 * Delete friends relationship
	 * @param id1
	 * @param id2
	 */
	public void deleteFriends(long id1, long id2) {
		String query = "DELETE FROM friends WHERE uid1=? AND uid2=?";

		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setLong(1, (id1<id2? id1:id2));
			preparedStatement.setLong(2, (id1>id2? id1:id2));

			// execute insert SQL statement
			preparedStatement.executeUpdate();
			System.out.println("Delete one data in friends table!");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Delete contact request
	 * @param id1
	 * @param id2
	 */
	public void deleteContact(long id) {
		String query = "DELETE FROM contact WHERE contact_id=?";

		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setLong(1, id);

			// execute insert SQL statement
			preparedStatement.executeUpdate();
			System.out.println("Delete one data in contact table!");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Convert util.Date to sql.Date
	 * @param util.Date
	 * @return sql.Date
	 */
	private java.sql.Date convertToSqlDate(java.util.Date date) {
	    return new java.sql.Date(date.getTime());
	}
}

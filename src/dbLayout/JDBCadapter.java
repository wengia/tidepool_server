package dbLayout;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import entities.Data;
import entities.User;


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
	public ArrayList<Data> selectData( int user_id ) {
		ArrayList<Data> dataList = new ArrayList<Data>();
		String query = "select * from myData " + "where uid = ?";
		
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, user_id);
			ResultSet rs = preparedStatement.executeQuery();
			
			while( rs.next() ) {
				Data data = new Data();
				
				data.setId(rs.getInt("id"));
				data.setTime(rs.getDate("theTime"));
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
	 * @param user_id
	 * @return friends
	 */
	public ArrayList<User> selectFriends( int user_id ) {
		ArrayList<User> friends = new ArrayList<User>();
		
		friends.addAll( selectFriends(user_id, true) );
		friends.addAll( selectFriends(user_id, false) );
		
		return friends;
	}
	
	private ArrayList<User> selectFriends( int user_id, boolean right ) {
		ArrayList<User> userList = new ArrayList<User>();
		String query1 = "select * from myUser a" +
				"inner join friends b on a.id=b.uid1" + 
				"where b.uid2 = ?";
		String query2 = "select * from myUser a" +
				"inner join friends b on a.id=b.uid2" + 
				"where b.uid1 = ?";
		
		try {
			if(right)
				preparedStatement = connection.prepareStatement(query1);
			else
				preparedStatement = connection.prepareStatement(query2);
			preparedStatement.setInt(1, user_id);
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
	
	// Insert new User
	
	// Insert new friends relationship
	
	// Delete friends relationship

}

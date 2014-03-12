package fi.raka.everyconvo.api.entities;

import static fi.raka.everyconvo.api.sql.SQLUtils.getConnection;
import static fi.raka.everyconvo.api.sql.SQLUtils.selectFrom;
import static fi.raka.everyconvo.api.sql.SQLUtils.insertInto;
import static fi.raka.everyconvo.api.sql.SQLUtils.Values.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import fi.raka.everyconvo.api.utils.PasswordHash;
import fi.raka.everyconvo.api.entities.StatusMessage;

public class User {
	
	private String userName;
	private int userId;	
	
	public User() {
	}

	public String login(String userName, String password) {
		
		this.userName = userName;
		
		String out = "false";
		Connection conn = null;
		ResultSet rs = null;
		ResultSet lrs = null;
		
		try {
			
			conn = getConnection();
			rs = getUserInfoResultSet(conn);
			
			if( rs.first() ) {
			
				this.userId = rs.getInt( COL_USERID );
				
				lrs = selectFrom(conn, TABLE_LOGIN, 
						new String[] { COL_USERID, COL_PASSHASH },
						new String[] { COL_USERID + "='" + userId + "'" }
					);
				
				lrs.first();
				String passhash = lrs.getString( COL_PASSHASH );
				
				try {
					out = PasswordHash.validatePassword( password, passhash ) ? "true" : "false";
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (InvalidKeySpecException e) {
					e.printStackTrace();
				}
	
				lrs.close();
			}

		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		finally {
			if( conn != null ) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if( rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if( lrs != null) {
				try {
					lrs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return out;
	}
	
	public ResultSet getUserInfo(String userName) 
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {

		this.userName = userName;
		
		Connection conn = getConnection();
		ResultSet rs = getUserInfoResultSet(conn);
		
		return rs;
	}
	
	public static StatusMessage createUser(String userName, String description, String websiteUrl, String location, String visibility, String password) {
		
		StatusMessage statusMessage = null;
		Connection conn = null;
		long userId = 0;
		
		try {
			
			conn = getConnection();
			conn.setAutoCommit( false );
			
			ResultSet generatedKeys = insertInto(conn, TABLE_USERS, 
					new String[] { COL_USERNAME, COL_DESCRIPTION, COL_WEBSITEURL, COL_LOCATION, COL_VISIBILITY },
					new String[] { userName, description, websiteUrl, location, visibility }
				);
			
			// Get generated userid
			generatedKeys.first();
			userId = generatedKeys.getLong(1);
			
			String passhash = null;
			try {
				passhash = PasswordHash.createHash( password );
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (InvalidKeySpecException e) {
				e.printStackTrace();
			}
			if(passhash == null) statusMessage = new StatusMessage(StatusMessage.STATUS_ERROR, "Server error on creating user.");
			
			insertInto(conn, TABLE_LOGIN,
					new String[] { COL_USERID, COL_PASSHASH },
					new String[] { "" + userId, passhash }
				);
			
			conn.commit();
			
			statusMessage = new StatusMessage(StatusMessage.STATUS_OK, "User created with id " + userId);
			
		} catch (SQLException e1) {
			statusMessage = new StatusMessage(StatusMessage.STATUS_ERROR, e1.getMessage());
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		finally {
			if( conn != null ) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return statusMessage; 
	}
	
	private ResultSet getUserInfoResultSet(Connection conn) throws SQLException {
		return selectFrom(conn, TABLE_USERS, 
				new String[] { COL_USERID, COL_USERNAME, COL_DESCRIPTION, COL_WEBSITEURL, COL_LOCATION, COL_VISIBILITY },
				new String[] { COL_USERNAME + "='" + userName + "'" }
			);
	}

}

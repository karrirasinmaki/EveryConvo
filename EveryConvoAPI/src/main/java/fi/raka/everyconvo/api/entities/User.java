package fi.raka.everyconvo.api.entities;

import static fi.raka.everyconvo.api.sql.SQLUtils.Values.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.ResultSet;
import java.sql.SQLException;

import fi.raka.everyconvo.api.sql.SQLChain;
import fi.raka.everyconvo.api.utils.PasswordHash;
import fi.raka.everyconvo.api.entities.StatusMessage;

public class User {
	
	private String userName;
	private int userId;	
	
	public User() {
	}

	public String login(String userName, String password) {
		
		this.userName = userName;
		
		SQLChain chain = new SQLChain();
		String out = "false";
		ResultSet rs = null;
		ResultSet lrs = null;
		
		try {
			
			chain.open(DATABASE_URL);
			rs = getUserInfoResultSet(chain);
			
			if( rs.first() ) {
			
				this.userId = rs.getInt( COL_USERID );
				lrs = chain.cont()
					.select(COL_USERID, COL_PASSHASH)
					.from(TABLE_LOGIN)
					.whereIs(COL_USERID, ""+userId)
					.exec();
				
				lrs.first();
				String passhash = lrs.getString( COL_PASSHASH );
				
				try {
					out = PasswordHash.validatePassword( password, passhash ) ? "true" : "false";
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (InvalidKeySpecException e) {
					e.printStackTrace();
				}
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
		
		SQLChain chain = new SQLChain();
		chain.open(DATABASE_URL);
		ResultSet rs = getUserInfoResultSet( chain );
		
		return rs;
	}
	
	public static StatusMessage createUser(String userName, String description, String websiteUrl, String location, String visibility, String password) {

		SQLChain chain = new SQLChain();
		StatusMessage statusMessage = null;
		long userId = 0;
		
		try {
						
			ResultSet generatedKeys = chain
				.open(DATABASE_URL)
				.setAutoCommit(false)
				.insertInto(TABLE_USERS, COL_USERNAME, COL_DESCRIPTION, COL_WEBSITEURL, COL_LOCATION, COL_VISIBILITY)
				.values(userName, description, websiteUrl, location, visibility)
				.exec();
			
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
				
			chain.cont()
				.insertInto(TABLE_LOGIN, COL_USERID, COL_PASSHASH)
				.values("" + userId, passhash)
				.exec();
			
			chain.cont().commit().setAutoCommit( true );
			
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
			try {
				chain.cont().close();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return statusMessage; 
	}
	
	private ResultSet getUserInfoResultSet(SQLChain chain) throws SQLException, IllegalAccessException {
		return chain.cont()
			.select(COL_USERID, COL_USERNAME, COL_DESCRIPTION, COL_WEBSITEURL, COL_LOCATION, COL_VISIBILITY)
			.from(TABLE_USERS)
			.whereIs(COL_USERNAME, userName)
			.exec();
	}

}

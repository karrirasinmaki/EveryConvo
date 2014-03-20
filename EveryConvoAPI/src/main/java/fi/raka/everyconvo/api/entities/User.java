package fi.raka.everyconvo.api.entities;

import static fi.raka.everyconvo.api.sql.SQLUtils.Values.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import fi.raka.everyconvo.api.sql.SQLChain;
import fi.raka.everyconvo.api.utils.PasswordHash;
import fi.raka.everyconvo.api.entities.StatusMessage;

public class User {
	
	private static String HTTP_SESSION_ATTRIBUTE_NAME = "user";
	private String username;
	private int userid;
	
	public User() {
	}
	
	public String getUserName() {
		return username;
	}
	public int getUserId() {
		return userid;
	}

	public StatusMessage login(String userName, String password) {
		return login( userName, password, null );
	}
	public StatusMessage login(String userName, String password, HttpServletRequest req) {
		
		this.username = userName;
		
		StatusMessage statusMessage = null;
		SQLChain chain = new SQLChain();
		ResultSet rs = null;
		ResultSet lrs = null;
		
		try {
			
			chain.open(DATABASE_URL);
			rs = getUserInfoResultSet(chain);
			
			if( rs.first() ) {
			
				userid = rs.getInt( COL_USERID );
				lrs = chain.cont()
					.select(COL_USERID, COL_PASSHASH)
					.from(TABLE_LOGIN)
					.whereIs(COL_USERID, ""+userid)
					.exec();
				
				lrs.first();
				String passhash = lrs.getString( COL_PASSHASH );
				
				try {
					if( PasswordHash.validatePassword( password, passhash ) ) {
						statusMessage = new StatusMessage(StatusMessage.STATUS_OK, "Logged in");
						createHttpSession( req );
					}
					else {
						statusMessage = new StatusMessage(StatusMessage.STATUS_ERROR, "Error with authentication.");
					}
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
		
		return statusMessage;
	}
	
	public String getUserInfo() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
	public ResultSet getUserInfo(String userName) 
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {

		this.username = userName;
		
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
	
	public static User getSessionUser(HttpServletRequest req) {
		HttpSession session = req.getSession();
		if( session == null ) return null;
		return (User) session.getAttribute(HTTP_SESSION_ATTRIBUTE_NAME);
	}
	
	private ResultSet getUserInfoResultSet(SQLChain chain) throws SQLException, IllegalAccessException {
		return chain.cont()
			.select(COL_USERID, COL_USERNAME, COL_DESCRIPTION, COL_WEBSITEURL, COL_LOCATION, COL_VISIBILITY)
			.from(TABLE_USERS)
			.whereIs(COL_USERNAME, username)
			.exec();
	}
	
	private void createHttpSession(HttpServletRequest req) {
		HttpSession session = req.getSession( true );
		session.setAttribute(HTTP_SESSION_ATTRIBUTE_NAME, this);
	}
	
	private Object getHttpSession(HttpServletRequest req) {
		HttpSession session = req.getSession();
		return session.getAttribute(HTTP_SESSION_ATTRIBUTE_NAME);
	}

}

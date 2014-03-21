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
	private String description;
	private String websiteurl;
	private String location;
	private int visibility;
	private boolean me = false;
	
	public User() {
	}
	public User(String userName, HttpServletRequest req) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		username = userName;
		
		User user = getSessionUser(req);
		
		SQLChain chain = new SQLChain();
		chain.open(DATABASE_URL);
		ResultSet rs = getUserInfoResultSet( chain );
		
		if( rs.first() ) {
			username = rs.getString( COL_USERNAME );
			userid = rs.getInt( COL_USERID );
			description = rs.getString(COL_DESCRIPTION);
			websiteurl = rs.getString(COL_WEBSITEURL);
			location = rs.getString(COL_LOCATION);
			visibility = rs.getInt(COL_VISIBILITY);
		}
		
		if( user.userid == userid ) me = true;
	}
	
	public User setDescription(String description) {
		this.description = description;
		return this;
	}
	public User setWebsiteUrl(String websiteUrl) {
		this.websiteurl = websiteUrl;
		return this;
	}
	public User setLocation(String location) {
		this.location = location;
		return this;
	}
	
	public String getUserName() {
		return username;
	}
	public int getUserId() {
		return userid;
	}
	public boolean isMe() {
		return me;
	}

	public StatusMessage login(String userName, String password) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		return login( userName, password, null );
	}
	public StatusMessage login(String userName, String password, HttpServletRequest req) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		if( req == null ) return StatusMessage.authError();
		
		if( userName == null || password == null ) {
			User sessionUser = getSessionUser( req );
			if( sessionUser == null ) {
				return StatusMessage.authError();
			}
			else {
				return StatusMessage.authOk();
			}
		}
		
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
						statusMessage = StatusMessage.authOk();
						createHttpSession( req );
					}
					else {
						statusMessage = StatusMessage.authError();
					}
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (InvalidKeySpecException e) {
					e.printStackTrace();
				}
			}

		}
		finally {
			if( rs != null) {
				rs.close();
			}
			if( lrs != null) {
				lrs.close();
			}
		}
		
		return statusMessage;
	}
	
	public String getUserInfo() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
	public static StatusMessage createUser(String userName, String description, String websiteUrl, String location, String visibility, String password) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {

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
			
		}
		finally {
			chain.cont().close();
		}
		
		return statusMessage; 
	}
	
	public ResultSet save() 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		return new SQLChain()
			.open( DATABASE_URL )
			.update(TABLE_USERS)
			.set(COL_LOCATION, location)
			.set(COL_WEBSITEURL, websiteurl)
			.set(COL_DESCRIPTION, description)
			.doneSet()
			.whereIs(COL_USERID, ""+userid)
			.update();
	}
	
	public static ResultSet getAllUsers() 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		SQLChain chain = new SQLChain();
		return chain.open(DATABASE_URL)
			.select(COL_USERID, COL_USERNAME, COL_DESCRIPTION, COL_WEBSITEURL, COL_LOCATION, COL_VISIBILITY)
			.from(TABLE_USERS)
			.exec();
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

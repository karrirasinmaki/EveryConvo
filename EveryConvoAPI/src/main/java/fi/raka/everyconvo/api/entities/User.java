package fi.raka.everyconvo.api.entities;

import static fi.raka.everyconvo.api.sql.SQLUtils.Values.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import fi.raka.everyconvo.api.sql.SQLChain;
import fi.raka.everyconvo.api.sql.SQLChain.Chain;
import fi.raka.everyconvo.api.sql.SQLChain.UpdateChain;
import fi.raka.everyconvo.api.utils.PasswordHash;
import fi.raka.everyconvo.api.entities.StatusMessage;

public class User {
	
	private static String HTTP_SESSION_ATTRIBUTE_NAME = "user";
	private String username;
	private Integer userid;
	private String description;
	private String websiteurl;
	private String location;
	private String imageurl;
	private Integer visibility;
	private boolean me = false;
	
	public User() {
	}
	public User(String userName, HttpServletRequest req) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		load( userName, req );
	}
	
	public User setUserName(String userName) {
		this.username = userName;
		return this;
	}
	public User setUserId(int userId) {
		this.userid = userId;
		return this;
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
	public User setImageUrl(String imageUrl) {
		this.imageurl = imageUrl;
		return this;
	}
	public User setIsMe(boolean isMe) {
		this.me = isMe;
		return this;
	}
	
	public String getUserName() {
		return username;
	}
	public Integer getUserId() {
		return userid;
	}
	public boolean isMe() {
		return me;
	}

	/**
	 * Log in user and sets successfully logged user as current session user.
	 * @param userName
	 * @param password
	 * @return StatusMessage
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public StatusMessage login(String userName, String password) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		return login( userName, password, null );
	}
	/**
	 * Log in user and sets successfully logged user as current session user. If username nor password was given, checks current session user login status
	 * @param userName
	 * @param password
	 * @param req HttpServletRequest
	 * @return StatusMessage
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public StatusMessage login(String userName, String password, HttpServletRequest req) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		Chain chain = new SQLChain().open(DATABASE_URL);
		load( userName, req, chain );
		
		StatusMessage statusMessage = StatusMessage.authError();
		
		ResultSet rs = chain
			.select(COL_USERID, COL_PASSHASH)
			.from(TABLE_LOGIN)
			.whereIs(COL_USERID, ""+getUserId())
			.exec();
		
		rs.first();
		
		String passhash = rs.getString( COL_PASSHASH );
		
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
		
		rs.close();
		chain.close();
		
		return statusMessage;
	}
	
	public void load(String userName, HttpServletRequest req, Chain chain) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
				
		User user = getSessionUser(req);
		if( userName == null ) {
			if( user == null ) return;
			userName = user.getUserName();
		}
		
		ResultSet rs = chain
			.select(COL_USERID, COL_USERNAME, COL_DESCRIPTION, COL_WEBSITEURL, COL_LOCATION, COL_IMAGEURL, COL_VISIBILITY)
			.from(TABLE_USERS)
			.whereIs(COL_USERNAME, userName)
			.exec();
		
		if( rs.first() ) {
			username = rs.getString( COL_USERNAME );
			userid = rs.getInt( COL_USERID );
			description = rs.getString(COL_DESCRIPTION);
			websiteurl = rs.getString(COL_WEBSITEURL);
			location = rs.getString(COL_LOCATION);
			imageurl = rs.getString(COL_IMAGEURL);
			visibility = rs.getInt(COL_VISIBILITY);
		}
		
		rs.close();
		
		if( user != null && user.userid == userid ) me = true;
	}
	public void load(String userName, HttpServletRequest req) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		Chain chain = new SQLChain().open(DATABASE_URL);
		load( userName, req, chain );
		chain.close();
	}
	
	/**
	 * Update user data in database
	 * @return ResultSet of generated keys
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public void update() 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		UpdateChain chain = new SQLChain()
			.open( DATABASE_URL )
			.update(TABLE_USERS);
		
			if( location != null ) chain.set(COL_LOCATION, location);
			if( websiteurl != null ) chain.set(COL_WEBSITEURL, websiteurl);
			if( description != null ) chain.set(COL_DESCRIPTION, description);
			if( imageurl != null ) chain.set(COL_IMAGEURL, imageurl);
		
			chain
			.doneSet()
			.whereIs(COL_USERID, ""+userid)
			.update()
			.close();
	}
	
	/**
	 * Create new user to database and sets it to current session user
	 * @param userName
	 * @param description
	 * @param websiteUrl
	 * @param location
	 * @param visibility
	 * @param password
	 * @param req HttpServletRequest
	 * @return StatusMessage
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static StatusMessage createUser(
			String userName, String description, String websiteUrl, String location, String visibility, String password, HttpServletRequest req) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		String passhash = getPassHash( password );
		if(passhash == null) return new StatusMessage(StatusMessage.STATUS_ERROR, "Server error on creating user.");

		Chain chain = new SQLChain().open(DATABASE_URL);
		ResultSet generatedKeys = null;
		long userId = 0;
		
		generatedKeys = chain
			.setAutoCommit(false)
			.insertInto(TABLE_USERS, COL_USERNAME, COL_DESCRIPTION, COL_WEBSITEURL, COL_LOCATION, COL_VISIBILITY)
			.values(userName, description, websiteUrl, location, visibility)
			.exec();
		
		// Get generated userid
		generatedKeys.first();
		userId = generatedKeys.getLong(1);
			
		chain
			.insertInto(TABLE_LOGIN, COL_USERID, COL_PASSHASH)
			.values("" + userId, passhash)
			.exec();
		
		chain.commit().setAutoCommit( true );
		
		generatedKeys.close();
		chain.close();

		User user = new User( userName, req );
		user.createHttpSession( req );
		return new StatusMessage(StatusMessage.STATUS_OK, "User created with id " + userId);
	}
	
	private static String getPassHash(String password) {
		try {
			return PasswordHash.createHash( password );
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
			return null;
		}
	}
		
	/**
	 * Get all users as ArrayList<User>
	 * @return ArrayList<User>
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static ArrayList<User> loadAllUsers(HttpServletRequest req) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		User sessionUser = User.getSessionUser( req );
		ArrayList<User> users = new ArrayList<User>();
		Chain chain = new SQLChain().open(DATABASE_URL);
		ResultSet rs = loadAllUsersResultSet( chain );
		rs.beforeFirst();
		
		while( rs.next() ) {
			User user = new User();
			user.setUserId( rs.getInt(COL_USERID) )
				.setUserName( rs.getString(COL_USERNAME) )
				.setDescription( rs.getString(COL_DESCRIPTION) )
				.setWebsiteUrl( rs.getString(COL_WEBSITEURL) )
				.setLocation( rs.getString(COL_LOCATION) )
				.setImageUrl( rs.getString(COL_IMAGEURL) );
			if( sessionUser != null && sessionUser.getUserId() == user.getUserId() ) user.setIsMe( true );
			
			users.add( user );
		}
		
		rs.close();
		chain.close();
		return users;
	}
	
	/**
	 * Get current session user
	 * @param req HttpServletRequest
	 * @return Current session user, or null if not exists
	 */
	public static User getSessionUser(HttpServletRequest req) {
		HttpSession session = req.getSession();
		if( session == null ) return null;
		return (User) session.getAttribute(HTTP_SESSION_ATTRIBUTE_NAME);
	}
	
	/**
	 * Get all users as ResultSet
	 * @return ResultSet of all users, user per row, including columns [userid, username, description, websiteurl, location, visibility]
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private static ResultSet loadAllUsersResultSet(Chain chain) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {

		return chain
			.select(COL_USERID, COL_USERNAME, COL_DESCRIPTION, COL_WEBSITEURL, COL_LOCATION, COL_IMAGEURL, COL_VISIBILITY)
			.from(TABLE_USERS)
			.exec();
	}
	
	/**
	 * Create new HttpSession and attach this user as current session user
	 * @param req HttpServletRequest
	 */
	private void createHttpSession(HttpServletRequest req) {
		HttpSession session = req.getSession( true );
		session.setAttribute(HTTP_SESSION_ATTRIBUTE_NAME, this);
	}

}

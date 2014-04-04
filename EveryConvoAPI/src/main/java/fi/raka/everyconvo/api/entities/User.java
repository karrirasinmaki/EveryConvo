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
	private int visibility;
	private boolean me = false;
	
	public User() {
	}
	public User(String userName, String description, String websiteUrl, String location, Integer visibility) {
		setUserName(userName).setDescription(description).setWebsiteUrl(websiteUrl).setLocation(location).setVisibility(visibility);
	}
	public User(Integer userId, String userName, String description, String websiteUrl, String location, Integer visibility) {
		this(userName, description, websiteUrl, location, visibility);
		setUserId(userId);
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
	/**
	 * Set visibility integer. Accepted values are values -9 <= x <= 9. If given value breaks that rule,
	 * default value 0 is used instead
	 * @param visibility
	 * @return this for chaining
	 */
	public User setVisibility(Integer visibility) {
		if( visibility == null || Math.abs(visibility) > 9 ) {
			visibility = 0;
		}
		this.visibility = visibility;
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
	public static StatusMessage login(String userName, String password, HttpServletRequest req) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		Chain chain = new SQLChain().open(DATABASE_URL);
		User user = User.loadUser( userName, req, chain );
		
		StatusMessage statusMessage = StatusMessage.authError();
		
		ResultSet rs = chain
			.select(COL_USERID, COL_PASSHASH)
			.from(TABLE_LOGIN)
			.whereIs(COL_USERID, ""+user.getUserId())
			.exec();
		
		rs.first();
		
		String passhash = rs.getString( COL_PASSHASH );
		
		try {
			if( PasswordHash.validatePassword( password, passhash ) ) {
				statusMessage = StatusMessage.authOk();
				user.createHttpSession( req );
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
	
	public static User loadUser(String userName, HttpServletRequest req, Chain chain) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
				
		User sessionUser = getSessionUser(req);
		if( userName == null ) {
			if( sessionUser == null ) return null;
			userName = sessionUser.getUserName();
		}
		
		ResultSet rs = chain
			.select(COL_USERID, COL_USERNAME, COL_DESCRIPTION, COL_WEBSITEURL, COL_LOCATION, COL_IMAGEURL, COL_VISIBILITY)
			.from(TABLE_USERS)
			.whereIs(COL_USERNAME, userName)
			.exec();

		User user = null;
		if( rs.first() ) {
			 user = new User(
					rs.getInt( COL_USERID ), 
					rs.getString( COL_USERNAME ), 
					rs.getString(COL_DESCRIPTION), 
					rs.getString(COL_WEBSITEURL), 
					rs.getString(COL_LOCATION), 
					rs.getInt(COL_VISIBILITY)
					);
			if( sessionUser != null && user != null && sessionUser.userid == user.userid ) user.setIsMe( true );
		}
		rs.close();
		return user;
	}
	
	
	public static User loadUser(String userName, HttpServletRequest req) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		Chain chain = new SQLChain().open(DATABASE_URL);
		User user = User.loadUser( userName, req, chain );
		chain.close();
		return user;
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
	
	public void add() 
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		
		Chain chain = new SQLChain().open(DATABASE_URL);
		addToDB( chain );
		chain.close();
	}
	public void register(String password)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		
		String passhash = getPassHash( password );
		if(passhash != null) {
			Chain chain = new SQLChain().open(DATABASE_URL);
			addToDB( chain );
			
			chain
				.insertInto(TABLE_LOGIN, COL_USERID, COL_PASSHASH)
				.values("" + getUserId(), passhash)
				.exec();
			chain.commit().setAutoCommit( true );
			chain.close();
		}
	}
	
	private void addToDB(Chain chain) throws SQLException {
		ResultSet generatedKeys = chain
				.setAutoCommit(false)
				.insertInto(TABLE_USERS, COL_USERNAME, COL_DESCRIPTION, COL_WEBSITEURL, COL_LOCATION, COL_VISIBILITY)
				.values(getUserName(), description, websiteurl, location, ""+visibility)
				.exec();
			
		// Get generated userid
		generatedKeys.first();
		setUserId( generatedKeys.getInt(1) );
		generatedKeys.close();
	}
	
	private String getPassHash(String password) {
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

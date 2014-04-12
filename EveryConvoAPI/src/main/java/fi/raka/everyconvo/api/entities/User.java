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
import fi.raka.everyconvo.api.sql.SQLChain.SelectChain;
import fi.raka.everyconvo.api.sql.SQLChain.UpdateChain;
import fi.raka.everyconvo.api.utils.PasswordHash;
import fi.raka.everyconvo.api.entities.StatusMessage;

public class User {
	
	private static String HTTP_SESSION_ATTRIBUTE_NAME = "user";
	
	private static String a = TABLE_USERS+".";
	public static String FROM = TABLE_USERS;
	public static String PK_USERID = a+COL_USERID;
	public static String[] PROJECTION = {PK_USERID, a+COL_USERNAME, a+COL_DESCRIPTION, a+COL_WEBSITEURL, a+COL_LOCATION, a+COL_IMAGEURL, a+COL_VISIBILITY};
	
	private String 
		username,
		description,
		websiteurl,
		location,
		imageurl;
	private Integer userid;
	private int visibility;
	private boolean me = false;
	private Boolean follows;
	
	public User() {
	}
	public User(String userName, String description, String websiteUrl, String location, Integer visibility) {
		setUserName(userName).setDescription(description).setWebsiteUrl(websiteUrl).setLocation(location).setVisibility(visibility);
	}
	public User(Integer userId, String userName, String description, String websiteUrl, String location, Integer visibility) {
		this(userName, description, websiteUrl, location, visibility);
		setUserId(userId);
	}
	public User(String userName, String description, String websiteUrl, String location, Integer visibility, String imageUrl) {
		this(userName, description, websiteUrl, location, visibility);
		setImageUrl(imageUrl);
	}
	public User(String userName, String description, String websiteUrl, String location, Integer visibility, String imageUrl, Boolean follows) {
		this(userName, description, websiteUrl, location, visibility, imageUrl);
		setFollows(follows);
	}
	
	public User(Integer userId, String userName, String description, String websiteUrl, String location, Integer visibility, String imageUrl) {
		this(userId, userName, description, websiteUrl, location, visibility);
		setImageUrl(imageUrl);
	}
	public User(Integer userId, String userName, String description, String websiteUrl, String location, Integer visibility, String imageUrl, Boolean follows) {
		this(userId, userName, description, websiteUrl, location, visibility, imageUrl);
		setFollows(follows);
	}
	public User(ResultSet rs) throws SQLException {
		this(
			rs.getInt( COL_USERID ), 
			rs.getString( COL_USERNAME ), 
			rs.getString(COL_DESCRIPTION), 
			rs.getString(COL_WEBSITEURL), 
			rs.getString(COL_LOCATION), 
			rs.getInt(COL_VISIBILITY),
			rs.getString(COL_IMAGEURL),
			getFollowFromRS( rs )
			);
	}
	
	private static Boolean getFollowFromRS(ResultSet rs) {
		try {
			return rs.getBoolean("follows");
		} catch (SQLException e) {
			return null;
		}
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
	public User setFollows(Boolean follows) {
		this.follows = follows;
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
	public String getDescription() {
		return description;
	}
	public String getWebsiteUrl() {
		return websiteurl;
	}
	public String getLocation() {
		return location;
	}
	public String getImageUrl() {
		return imageurl;
	}
	public Integer getVisibility() {
		return visibility;
	}
	public Boolean getFollows() {
		return follows;
	}
	
	/**
	 * Add new user row to database
	 * @param chain Chain
	 * @throws SQLException
	 */
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
	
	/**
	 * Get password hash or null on error
	 * @param password
	 * @return password hash or null on error
	 */
	private String getPassHash(String password) {
		try {
			return PasswordHash.createHash( password );
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Create new HttpSession and attach this user as current session user
	 * @param req HttpServletRequest
	 */
	private void createHttpSession(HttpServletRequest req) {
		HttpSession session = req.getSession( true );
		session.setAttribute(HTTP_SESSION_ATTRIBUTE_NAME, this);
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
		
	/**
	 * Get all users as ArrayList<User>
	 * @return ArrayList<User>
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static ArrayList<User> loadAll(HttpServletRequest req, String userName) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		User sessionUser = User.getSessionUser( req );
		ArrayList<User> users = new ArrayList<User>();
		Chain chain = new SQLChain().open(DATABASE_URL);
		ResultSet rs = loadAllResultSet( chain, userName );
		rs.beforeFirst();
		
		while( rs.next() ) {
			User user = new User( rs );
			if( sessionUser != null && sessionUser.getUserId() == user.getUserId() ) user.setIsMe( true );
			users.add( user );
		}
		
		rs.close();
		chain.close();
		return users;
	}
	
	/**
	 * Get all users as ResultSet
	 * @return ResultSet of all users, user per row, including columns [userid, username, description, websiteurl, location, visibility]
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private static ResultSet loadAllResultSet(Chain chain, String userName) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {

		SelectChain sel = chain
			.select(PROJECTION)
			.from(FROM);
		
		if( userName != null ) {
			sel
			.whereLike(COL_USERNAME, userName)
			.or()
			.whereLike(COL_USERNAME, userName);
		}
		
		return sel.exec();
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
	
	/**
	 * Load user from database
	 * @param userName
	 * @param req HttpServletRequest
	 * @param chain Chain
	 * @return user with given userName, or null of user not found
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static User loadUser(String userName, HttpServletRequest req, Chain ch) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
				
		User sessionUser = getSessionUser(req);
		if( userName == null ) {
			if( sessionUser == null ) return null;
			userName = sessionUser.getUserName();
		}
		
		SelectChain chain = ch
			.select(PROJECTION);
		
		if( sessionUser != null ) {
			chain.q( ", (SELECT EXISTS(" );
			chain
				.select(COL_USERID)
				.from(TABLE_FOLLOWS)
				.whereIs(COL_USERID, ""+sessionUser.getUserId())
				.whereIs(COL_TOID, PK_USERID)
				.limit(1)
				.getQuery();
			chain.q( ")) as follows" );
		}
		
		ResultSet rs = chain
			.from(FROM)
			.whereLike(COL_USERNAME, userName)
			.exec();

		User user = null;
		if( rs.first() ) {
			user = new User( rs );
			if( sessionUser != null && user != null && sessionUser.userid == user.userid ) user.setIsMe( true );
		}
		rs.close();
		return user;
	}
	
	/**
	 * Load user from database
	 * @param userName
	 * @param req HttpServletRequest
	 * @return user with given userName, or null of user not found
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static User loadUser(String userName, HttpServletRequest req) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		Chain chain = new SQLChain().open(DATABASE_URL);
		User user = User.loadUser( userName, req, chain );
		chain.close();
		return user;
	}
	
	public static StatusMessage updateCurrentUser(HttpServletRequest req) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		String
		userName = req.getParameter( COL_USERNAME ),
		description = req.getParameter( COL_DESCRIPTION ),
		websiteUrl = req.getParameter( COL_WEBSITEURL ),
		location = req.getParameter( COL_LOCATION ),
		imageUrl = req.getParameter( COL_IMAGEURL );
		
		User user = User.getSessionUser( req );
		if( user != null ) {
			user.setDescription(description)
				.setWebsiteUrl(websiteUrl)
				.setLocation(location)
				.setImageUrl(imageUrl)
				.update();
			return StatusMessage.updateCompleted();
		}
		return StatusMessage.sessionError();
	}
	
	public static StatusMessage followUser(String userName, HttpServletRequest req) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		User sessionUser = User.getSessionUser(req);
		if( sessionUser == null ) return StatusMessage.authError();
		
		Chain chain = new SQLChain().open(DATABASE_URL);
		ResultSet rs = chain
			.insertInto(TABLE_FOLLOWS, COL_USERID, COL_TOID)
			.values( 
				sessionUser.getUserId(), 
				chain.innerChain()
					.select(COL_USERID)
					.from(TABLE_USERS)
					.whereIs(COL_USERNAME, userName)
					.limit(1)
					.endInnerChain()
				)
			.update();
		
		boolean doesAnything = rs.first();
		rs.close();
		chain.close();
		
		if( doesAnything ) return StatusMessage.notFound("user " + userName + " ");
		return new StatusMessage(StatusMessage.STATUS_OK, "You now follow user " + userName);
	}
	
	public static StatusMessage unFollowUser(String userName, HttpServletRequest req) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		User sessionUser = User.getSessionUser(req);
		if( sessionUser == null ) return StatusMessage.authError();
		
		Chain chain = new SQLChain().open(DATABASE_URL);
		ResultSet rs = chain		
			.delete()
			.from(TABLE_FOLLOWS)
			.whereIs(COL_USERID, ""+sessionUser.getUserId())
			.whereIs(COL_TOID, 
				"("+
					new SQLChain().builder()
						.select(COL_USERID)
						.from(TABLE_USERS)
						.whereIs(COL_USERNAME, userName)
						.limit(1)
						.getQuery()
				+ ")")
			.update();
		
		boolean doesAnything = rs.first();
		rs.close();
		chain.close();
		
		if( doesAnything ) return StatusMessage.notFound("user " + userName + " ");
		return new StatusMessage(StatusMessage.STATUS_OK, "You no longer follow user " + userName);
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

}

package fi.raka.everyconvo.api.entities;

import static fi.raka.everyconvo.api.sql.SQLUtils.Values.COL_TOID;
import static fi.raka.everyconvo.api.sql.SQLUtils.Values.COL_USERID;
import static fi.raka.everyconvo.api.sql.SQLUtils.Values.COL_USERNAME;
import static fi.raka.everyconvo.api.sql.SQLUtils.Values.DATABASE_URL;
import static fi.raka.everyconvo.api.sql.SQLUtils.Values.TABLE_FOLLOWS;
import static fi.raka.everyconvo.api.sql.SQLUtils.Values.TABLE_USERS;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import fi.raka.everyconvo.api.sql.SQLChain;
import fi.raka.everyconvo.api.sql.SQLChain.Chain;
import fi.raka.everyconvo.api.sql.SQLChain.SelectChain;

public class Follow {

	private static final String FROM = TABLE_FOLLOWS;
	private static final String a = FROM + ".";
	private static final String PK_USERID = a+COL_USERID;
	private static final String PK_TOID = a+COL_TOID;
	
	/**
	 * Get list of followers. Gets followers of given userName or current session user if userName is null. 
	 * If user not found, return null.
	 * @param req
	 * @param userName
	 * @return List of followers or null, if user not found
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static ArrayList<User> getFollowers(HttpServletRequest req, String userName) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		return getFollowList( req, userName, PK_TOID );
	}
	public static ArrayList<User> getFollows(HttpServletRequest req, String userName) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		return getFollowList( req, userName, PK_USERID );
	}
	
	private static ArrayList<User> getFollowList(HttpServletRequest req, String userName, String column) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		Integer userId = null;
		if( userName == null ) {
			User sessionUser = User.getSessionUser(req);
			if( sessionUser == null ) return null;
			userId = sessionUser.getUserId();
		}
		else {
			User user = User.loadUser(userName, req);
			if( user == null ) return null;
			userId = user.getUserId();
		}
		
		Chain chain = new SQLChain().open(DATABASE_URL);
		User.loadAllChain( req, chain, null )
		.innerJoin(FROM)
		.on(User.PK_USERID, PK_TOID)
		.whereIs(column, userId);
			
		return User.resultSetToArrayList( req, userName, chain.exec() );
	}

	public static StatusMessage followUser(HttpServletRequest req, String userName) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		User sessionUser = User.getSessionUser(req);
		if( sessionUser == null ) return StatusMessage.authError();
		
		User user = User.loadUser( userName, req );
		if( user == null ) return StatusMessage.notFound("user");
		
		Chain chain = new SQLChain().open(DATABASE_URL);
		ResultSet rs = chain
			.insertInto(TABLE_FOLLOWS, COL_USERID, COL_TOID)
			.values (sessionUser.getUserId(), user.getUserId() )
			.update();
		
		boolean doesAnything = rs.first();
		rs.close();
		chain.close();
		
		if( doesAnything ) return StatusMessage.notFound("user " + userName + " ");
		return new StatusMessage(StatusMessage.STATUS_OK, "You now follow user " + userName);
	}
	
	public static StatusMessage unFollowUser(HttpServletRequest req, String userName) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		User sessionUser = User.getSessionUser(req);
		if( sessionUser == null ) return StatusMessage.authError();
		
		Chain chain = new SQLChain().open(DATABASE_URL);
		ResultSet rs = chain		
			.delete()
			.from(TABLE_FOLLOWS)
			.whereIs(COL_USERID, ""+sessionUser.getUserId())
			.whereIs(
				COL_TOID, 
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
		return new StatusMessage(StatusMessage.STATUS_OK, "You no longer follow user " + userName);
	}
	
}

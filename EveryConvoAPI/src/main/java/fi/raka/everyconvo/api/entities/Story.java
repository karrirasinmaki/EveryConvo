package fi.raka.everyconvo.api.entities;

import static fi.raka.everyconvo.api.sql.SQLUtils.Values.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import fi.raka.everyconvo.api.sql.SQLChain;
import fi.raka.everyconvo.api.sql.SQLChain.SelectChain;

public class Story {

	private int storyid;
	private int fromid;
	private int toid;
	private String content;
	private String mediaurl;
	private int visibility;
	private Timestamp timestamp;
	private User user;
	private ArrayList<Like> likes;
	private Boolean melikes;
	
	public Story() {}
	public Story(int fromid, int toid, String content, String mediaURL) {
		this.fromid = fromid;
		this.toid = toid;
		this.content = content;
		this.mediaurl = mediaURL;
	}
	
	public void send() 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		new SQLChain()
			.open(DATABASE_URL)
			.insertInto(TABLE_STORIES, COL_FROMID, COL_TOID, COL_CONTENT, COL_MEDIAURL)
			.values(""+fromid, ""+toid, content, mediaurl)
			.exec();
	}
	
	public static ArrayList<Story> loadStories(String[] users, HttpServletRequest req, int limit, long startTimeMillis, int offset)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		User sessionUser = User.getSessionUser( req );
		ArrayList<Story> stories = new ArrayList<Story>();
		ResultSet rs = loadStoriesResultSet( users, req, limit, startTimeMillis, offset );
			
		rs.beforeFirst();
		while( rs.next() ) {
			Story story = new Story();
			story.storyid = rs.getInt(COL_STORYID);
			story.fromid = rs.getInt(COL_FROMID);
			story.toid = rs.getInt(COL_TOID);
			story.content = rs.getString(COL_CONTENT);
			story.mediaurl = rs.getString(COL_MEDIAURL);
			story.timestamp = rs.getTimestamp(COL_TIMESTAMP);
			
			story.user = new User();
			story.user.setUserName( rs.getString(COL_USERNAME) );
			story.user.setIsMe( rs.getBoolean("me") );
			
			story.likes = Like.loadAllLikes( ""+story.storyid, null );
			if( sessionUser != null ) {
				for( Like like : story.likes ) {
					if( like.getUserId() == sessionUser.getUserId() ) {
						story.melikes = true;
						break;
					}
				}
			}
			
			stories.add( story );
		}
		
		return stories;
	}

//  users, req, limit, startTime, (page-1)*limit
	public static ResultSet loadStoriesResultSet(String[] users, HttpServletRequest req, int limit, long startTimeMillis, int offset) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		User user = User.getSessionUser(req);
		Integer currentUserId = user != null ? user.getUserId() : null;
		ResultSet rs = null;
		
		SelectChain chain = new SQLChain()
			.open(DATABASE_URL)
			.select("a."+COL_STORYID, "a."+COL_FROMID, "a."+COL_TOID, "a."+COL_CONTENT, "a."+COL_MEDIAURL, "a."+COL_TIMESTAMP, 
					"b."+COL_USERNAME, "b."+COL_IMAGEURL)
		
			.q(",")
			.Case()
				.when()
				.whereIs("a."+COL_FROMID, ""+currentUserId)
				.then("true")
			.end().as( "me" )
			
			.from(TABLE_STORIES+" a")
			.innerJoin(TABLE_USERS+" b")
			.on("a."+COL_FROMID, "b."+COL_USERID);
		
			if( users != null ) {
				chain
				.whereIn("a."+COL_FROMID, users)
				.or()
				.whereIn("b."+COL_USERNAME, users);
			}

			chain
			.gte("a."+COL_TIMESTAMP, startTimeMillis)
			.desc("a."+COL_TIMESTAMP)
			.limit(limit)
			.offset(offset);
			
		rs = chain.exec();
		
		return rs;
	}
	
}

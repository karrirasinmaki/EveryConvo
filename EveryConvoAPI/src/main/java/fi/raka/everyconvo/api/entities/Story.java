package fi.raka.everyconvo.api.entities;

import static fi.raka.everyconvo.api.sql.SQLUtils.Values.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
	private long timestamp;
	private User user;
	
	public Story() {}
	public Story(int fromid, int toid, String content, String mediaURL) {
		this.fromid = fromid;
		this.toid = toid;
		this.content = content;
		this.mediaurl = mediaURL;
	}
	
	public void send() {
		try {
			new SQLChain()
				.open(DATABASE_URL)
				.insertInto(TABLE_STORIES, COL_FROMID, COL_TOID, COL_CONTENT, COL_MEDIAURL)
				.values(""+fromid, ""+toid, content, mediaurl)
				.exec();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static ArrayList<Story> loadStories(String[] users, HttpServletRequest req) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		ArrayList<Story> stories = new ArrayList<Story>();
		ResultSet rs = loadStoriesResultSet( users, req );
			
		rs.beforeFirst();
		while( rs.next() ) {
			Story story = new Story();
			story.storyid = rs.getInt(COL_STORYID);
			story.fromid = rs.getInt(COL_FROMID);
			story.toid = rs.getInt(COL_TOID);
			story.content = rs.getString(COL_CONTENT);
			story.mediaurl = rs.getString(COL_MEDIAURL);
			
			story.user = new User();
			story.user.setUserName( rs.getString(COL_USERNAME) );
			story.user.setIsMe( rs.getBoolean("me") );
			
			stories.add( story );
		}
		
		return stories;
	}
	
	public static ResultSet loadStoriesResultSet(String[] users, HttpServletRequest req) 
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
			.desc("a."+COL_TIMESTAMP);
			
		rs = chain.exec();
		
		return rs;
	}
	
}

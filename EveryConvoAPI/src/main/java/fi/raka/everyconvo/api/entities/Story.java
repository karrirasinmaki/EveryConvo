package fi.raka.everyconvo.api.entities;

import static fi.raka.everyconvo.api.sql.SQLUtils.Values.*;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import fi.raka.everyconvo.api.sql.SQLChain;
import fi.raka.everyconvo.api.sql.SQLChain.SelectChain;

public class Story {

	private int id;
	private int fromid;
	private int toid;
	private String content;
	private String mediaURL;
	private int visibility;
	private long timestamp;
	
	public Story(int fromid, int toid, String content, String mediaURL) {
		this.fromid = fromid;
		this.toid = toid;
		this.content = content;
		this.mediaURL = mediaURL;
	}
	
	public void send() {
		try {
			new SQLChain()
				.open(DATABASE_URL)
				.insertInto(TABLE_STORIES, COL_FROMID, COL_TOID, COL_CONTENT, COL_MEDIAURL)
				.values(""+fromid, ""+toid, content, mediaURL)
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
	
	public static ResultSet loadStories(String[] users, HttpServletRequest req) {
		
		User user = User.getSessionUser(req);
		ResultSet rs = null;
		
		try {
			SelectChain chain = new SQLChain()
				.open(DATABASE_URL)
				.select("a."+COL_STORYID, "a."+COL_FROMID, "a."+COL_TOID, "a."+COL_CONTENT, "a."+COL_MEDIAURL, "a."+COL_TIMESTAMP, 
						"b."+COL_USERNAME, "b."+COL_IMAGEURL)
				.q(",")
				.Case()
					.when()
					.whereIs("a."+COL_FROMID, ""+user.getUserId())
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
			
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return rs;
	}
	
}

package fi.raka.everyconvo.api.entities;

import static fi.raka.everyconvo.api.sql.SQLUtils.Values.*;

import java.sql.ResultSet;
import java.sql.SQLException;

import fi.raka.everyconvo.api.sql.SQLChain;

public class Story {

	private int id;
	private int fromid;
	private String toid;
	private String content;
	private String mediaURL;
	private int visibility;
	private long timestamp;
	
	public Story(int fromid, String toid, String content, String mediaURL) {
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
				.values(""+fromid, toid, content, mediaURL)
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
	
	public static ResultSet loadStories(int userId) {
		ResultSet rs = null;
		
		try {
			rs = new SQLChain()
				.open(DATABASE_URL)
				.select("a."+COL_STORYID, "a."+COL_FROMID, "a."+COL_TOID, "a."+COL_CONTENT, "a."+COL_MEDIAURL, "a."+COL_TIMESTAMP, "b."+COL_USERNAME)
				.from(TABLE_STORIES+" a")
				.innerJoin(TABLE_USERS+" b")
				.on("a."+COL_FROMID, "b."+COL_USERID)
				.desc("a."+COL_TIMESTAMP)
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
		
		return rs;
	}
	
}

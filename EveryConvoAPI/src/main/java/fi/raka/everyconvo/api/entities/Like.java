package fi.raka.everyconvo.api.entities;

import static fi.raka.everyconvo.api.sql.SQLUtils.Values.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import fi.raka.everyconvo.api.sql.SQLChain;
import fi.raka.everyconvo.api.sql.SQLChain.SelectChain;

public class Like {
	
	private Integer userid;
	private Integer storyid;
	
	public Like() {}
	public Like(Integer userId, Integer storyId) {
		this.userid = userId;
		this.storyid = storyId;
	}
	
	public Like setUserId(Integer userId) {
		this.userid = userId;
		return this;
	}
	public Like setStoryId(Integer storyId) {
		this.storyid = storyId;
		return this;
	}
	
	public Integer getUserId() {
		return userid;
	}
	public Integer getStoryId() {
		return storyid;
	}
	
	public void save() 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		new SQLChain().open(DATABASE_URL)
			.insertInto(TABLE_LIKES, COL_USERID, COL_STORYID)
			.values(""+userid, ""+storyid)
			.update();
	}
	
	public void remove() 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		new SQLChain().open(DATABASE_URL)
			.delete()
			.from(TABLE_LIKES)
			.whereIs(COL_USERID, ""+getUserId())
			.whereIs(COL_STORYID, ""+getStoryId())
			.update();
	}
	
	public static ArrayList<Like> loadAllLikes(String storyIds, String userIds) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		ArrayList<Like> likes = new ArrayList<Like>();
		ResultSet rs = loadAllLikesResultSet( storyIds, userIds );
		rs.beforeFirst();
		while( rs.next() ) {
			Like like = new Like();
			like.setUserId( rs.getInt(COL_USERID) )
				.setStoryId( rs.getInt(COL_STORYID) );
			
			likes.add( like );
		}
		
		return likes;
	}
	
	private static ResultSet loadAllLikesResultSet(String storyIds, String userIds) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		SelectChain chain = new SQLChain().open(DATABASE_URL)
			.select(COL_USERID, COL_STORYID)
			.from(TABLE_LIKES);
		
			if( storyIds != null ) chain.whereIn(COL_STORYID, storyIds);
			if( userIds != null ) chain.whereIn(COL_USERID, userIds);
		
		return chain.exec();
	}

}

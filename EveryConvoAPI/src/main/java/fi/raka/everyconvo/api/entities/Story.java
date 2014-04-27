package fi.raka.everyconvo.api.entities;

import static fi.raka.everyconvo.api.sql.SQLUtils.Values.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;

import fi.raka.everyconvo.api.sql.SQLChain;
import fi.raka.everyconvo.api.sql.SQLChain.Chain;
import fi.raka.everyconvo.api.sql.SQLChain.SelectChain;

public class Story {
	
	public static String FROM = TABLE_STORIES;
	public static String a = TABLE_STORIES+".";
	public static String FK_FROMID = a+COL_FROMID;
	public static String TIMESTAMP = a+COL_TIMESTAMP;
	public static String[] OWN_PROJECTION = { a+COL_STORYID, a+COL_FROMID, a+COL_TOID, a+COL_CONTENT, a+COL_MEDIAURL, a+COL_TIMESTAMP };
	public static String[] PROJECTION = ArrayUtils.addAll( OWN_PROJECTION, User.FULL_PROJECTION );
	
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
			.insertInto(FROM, COL_FROMID, COL_TOID, COL_CONTENT, COL_MEDIAURL)
			.values(""+fromid, ""+toid, content, mediaurl)
			.update()
			.close();
	}
	
	public static ArrayList<Story> loadStories(String[] users, HttpServletRequest req, int limit, long startTimeMillis, int offset)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		User sessionUser = User.getSessionUser( req );
		ArrayList<Story> stories = new ArrayList<Story>();
		Chain chain = new SQLChain().open(DATABASE_URL);
		ResultSet rs = loadStoriesResultSet( users, req, limit, startTimeMillis, offset, chain );
			
		rs.beforeFirst();
		while( rs.next() ) {
			Story story = new Story();
			story.storyid = rs.getInt(COL_STORYID);
			story.fromid = rs.getInt(COL_FROMID);
			story.toid = rs.getInt(COL_TOID);
			story.content = rs.getString(COL_CONTENT);
			story.mediaurl = rs.getString(COL_MEDIAURL);
			story.timestamp = rs.getTimestamp(COL_TIMESTAMP);
			
			story.user = User.createUser( rs, req );
			
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
		
		rs.close();
		chain.close();
		
		return stories;
	}

//  users, req, limit, startTime, (page-1)*limit
	public static ResultSet loadStoriesResultSet(String[] users, HttpServletRequest req, int limit, long startTimeMillis, int offset, Chain ch) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		User user = User.getSessionUser(req);
		Integer currentUserId = user != null ? user.getUserId() : null;
		
		SelectChain chain = ch
			.select(PROJECTION)
		
			.q(",")
			.Case()
				.when()
				.whereIs(FK_FROMID, ""+currentUserId)
				.then("true")
			.end().as( "me" )
			
			.from(FROM)
			.innerJoin(User.FROM)
			.on(FK_FROMID, User.PK_USERID);
			
			User.leftJoinPersonOrGroup( chain );
		
			if( users != null ) {
				chain
				.whereIn(FK_FROMID, users)
				.or()
				.whereIn(User.PK_USERNAME, users)
				.and();
			}

			chain
			.gte(TIMESTAMP, startTimeMillis)
			.desc(TIMESTAMP)
			.limit(limit)
			.offset(offset);
			
		return chain.exec();
	}
	
}

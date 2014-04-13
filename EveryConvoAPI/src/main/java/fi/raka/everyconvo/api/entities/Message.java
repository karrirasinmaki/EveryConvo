package fi.raka.everyconvo.api.entities;

import static fi.raka.everyconvo.api.sql.SQLUtils.Values.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import fi.raka.everyconvo.api.sql.SQLChain;
import fi.raka.everyconvo.api.sql.SQLChain.Chain;
import fi.raka.everyconvo.api.sql.SQLChain.SelectChain;

public class Message {

	private int messageid;
	private int fromid;
	private int toid;
	private String content;
	private long timestamp;
	
	public Message(int fromid, int toid, String content) {
		this.fromid = fromid;
		this.toid = toid;
		this.content = content;
	}
	
	public Message(ResultSet rs) throws SQLException {
		messageid = rs.getInt(COL_MESSAGEID);
		fromid = rs.getInt(COL_FROMID);
		toid = rs.getInt(COL_TOID);
		content = rs.getString(COL_CONTENT);
	}
	
	public void send() 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		new SQLChain()
			.open(DATABASE_URL)
			.insertInto(TABLE_MESSAGES, COL_FROMID, COL_TOID, COL_CONTENT)
			.values(fromid, toid, content)
			.exec()
			.close();
	}
	
	public static ArrayList<Message> loadMessages(int userId, Integer user2Id) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		ArrayList<Message> messages = new ArrayList<Message>();
		Chain chain = new SQLChain().open(DATABASE_URL);
		ResultSet rs = loadMessagesResultSet( userId, user2Id, chain );
		
		rs.beforeFirst();
		while( rs.next() ) {
			messages.add( new Message(rs) );
		}
		
		rs.close();
		chain.close();
		return messages;
	}
	
	public static ResultSet loadMessagesResultSet(int userId, Integer user2Id, Chain chain) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		SelectChain sel = chain
			.select(COL_MESSAGEID, COL_FROMID, COL_TOID, COL_CONTENT, COL_TIMESTAMP)
			.from(TABLE_MESSAGES)
			.whereIs(COL_FROMID, userId)
			.or()
			.whereIs(COL_TOID, userId);
		
			if( user2Id != null ) {
				sel
				.and()
				.q("(")
					.whereIs(COL_FROMID, user2Id)
					.or()
					.whereIs(COL_TOID, user2Id)
				.q(")");
			}
			
		return sel.exec();
	}
	
}

package fi.raka.everyconvo.api.entities;

import static fi.raka.everyconvo.api.sql.SQLUtils.Values.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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
	
	public Message(int fromid, int toid) {
		this.fromid = fromid;
		this.toid = toid;
	}
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
	
	private String getConversationName() {
		return "u" + Math.min(fromid, toid) + "-u" + Math.max(fromid, toid);
	}
	
	public void send() 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		new SQLChain()
			.open(DATABASE_URL)
			.insertInto(TABLE_MESSAGES, COL_FROMID, COL_TOID, COL_CONTENT, COL_CONVERSATION)
			.values(fromid, toid, content, getConversationName())
			.update()
			.close();
	}
	
	public static ArrayList<Message> loadMessages(int userId, Integer user2Id, Long fromMillis, Long toMillis) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		ArrayList<Message> messages = new ArrayList<Message>();
		Chain chain = new SQLChain().open(DATABASE_URL);
		SelectChain sel = loadMessagesSelectChain( userId, user2Id, chain );
		if( fromMillis != null ) sel.and().gte( COL_TIMESTAMP, new Timestamp(fromMillis) );
		if( toMillis != null ) sel.and().lte( COL_TIMESTAMP, new Timestamp(toMillis) );
		
		ResultSet rs = sel.exec();
		
		rs.beforeFirst();
		while( rs.next() ) {
			messages.add( new Message(rs) );
		}
		
		rs.close();
		chain.close();
		return messages;
	}
	
	public static SelectChain loadMessagesSelectChain(int userId, Integer user2Id, Chain chain) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		Message dumbMessage = new Message(userId, user2Id);
		
		SelectChain sel = chain
			.select(COL_MESSAGEID, COL_FROMID, COL_TOID, COL_CONTENT, COL_TIMESTAMP)
			.from(TABLE_MESSAGES);
			sel
			.whereIs(COL_CONVERSATION, dumbMessage.getConversationName());
			
		return sel;
	}
	
}

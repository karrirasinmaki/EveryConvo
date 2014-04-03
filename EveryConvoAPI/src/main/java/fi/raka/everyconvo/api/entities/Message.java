package fi.raka.everyconvo.api.entities;

import static fi.raka.everyconvo.api.sql.SQLUtils.Values.*;

import java.sql.ResultSet;
import java.sql.SQLException;

import fi.raka.everyconvo.api.sql.SQLChain;
import fi.raka.everyconvo.api.sql.SQLChain.Chain;

public class Message {

	private int id;
	private int fromid;
	private String toid;
	private String content;
	private long timestamp;
	
	public Message(int fromid, String toid, String content) {
		this.fromid = fromid;
		this.toid = toid;
		this.content = content;
	}
	
	public void send() 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		new SQLChain()
			.open(DATABASE_URL)
			.insertInto(TABLE_MESSAGES, COL_FROMID, COL_TOID, COL_CONTENT)
			.values(""+fromid, toid, content)
			.exec()
			.close();
	}
	
	public static ResultSet loadMessages(int userId, Chain chain) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		return chain
			.select(COL_MESSAGEID, COL_FROMID, COL_TOID, COL_CONTENT, COL_TIMESTAMP)
			.from(TABLE_MESSAGES)
			.whereIs(COL_TOID, ""+userId)
			.or()
			.whereIs(COL_FROMID, ""+userId)
			.exec();
	}
	
}

package fi.raka.everyconvo.api.servelets;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.raka.everyconvo.api.entities.StatusMessage;
import fi.raka.everyconvo.api.sql.SQLChain;
import static fi.raka.everyconvo.api.sql.SQLUtils.*;
import static fi.raka.everyconvo.api.sql.SQLUtils.Values.*;

public class InstallServelet extends HttpServlet {
	
	private static final long serialVersionUID = 7200686354507643001L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		StatusMessage statusMessage = null;
		SQLChain chain = new SQLChain();
		
		try {
			
			new SQLChain()
				.open(DATABASE_BASE_URL)
				.create()
				.database(DATABASE_NAME, SQLChain.CreateChain.IF_NOT_EXISTS)
				.exec()
				.close();
			
			chain.open(DATABASE_URL)
				.setAutoCommit( false )
				
				.create()
				.table(TABLE_USERS, null, 
						COL_USERID + INT_NOT_NULL_AUTO_INCREMENT,
					    COL_USERNAME + " VARCHAR(20)",
					    COL_DESCRIPTION + " TEXT",
					    COL_WEBSITEURL + " TEXT",
					    COL_LOCATION + " VARCHAR(255)",
					    COL_VISIBILITY + " VARCHAR(1)",
					    getPrimaryKeyClause(COL_USERID),
					    "UNIQUE ( username )"
						).exe()
						
				.create()
				.table(TABLE_LOGIN, null, 
						COL_USERID + INT_NOT_NULL,
						COL_PASSHASH + " VARCHAR(128)",
						getForeignKeyClause(COL_USERID, TABLE_USERS)
					    ).exe()
					    
				.create()
				.table(TABLE_PERSONS, null, 
						COL_USERID + INT_NOT_NULL,
					    COL_FIRSTNAME + " VARCHAR(60)",
					    COL_LASTNAME + " VARCHAR(60)",
					    getForeignKeyClause(COL_USERID, TABLE_USERS)
					    ).exe()
					    
				.create()
				.table(TABLE_GROUPS, null, 
						COL_USERID + INT_NOT_NULL,
						getForeignKeyClause(COL_USERID, TABLE_USERS)
					    ).exe()
					    
				.create()
				.table(TABLE_GROUPSUSERS, null, 
						COL_GROUPID + INT_NOT_NULL,
					    COL_USERID + INT_NOT_NULL,
					    getForeignKeyClause(COL_GROUPID, TABLE_GROUPS, COL_USERID),
					    getForeignKeyClause(COL_USERID, TABLE_USERS)
					    ).exe()
					    
				.create()
				.table(TABLE_MESSAGES, null, 
						COL_MESSAGEID + INT_NOT_NULL_AUTO_INCREMENT,
					    COL_FROMID + INT_NOT_NULL,
					    COL_TOID + INT_NOT_NULL,
					    COL_CONTENT + " TEXT NOT NULL",
					    COL_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP",
					    getPrimaryKeyClause(COL_MESSAGEID),
					    getForeignKeyClause(COL_FROMID, TABLE_USERS, COL_USERID),
					    getForeignKeyClause(COL_TOID, TABLE_USERS, COL_USERID)
					    ).exe()
					    
				.commit()
				.setAutoCommit( true )
				.close();
			
			statusMessage = new StatusMessage(StatusMessage.STATUS_OK, "Database created.");
			
		} catch (SQLException|InstantiationException|IllegalAccessException|ClassNotFoundException e ) {
			e.printStackTrace();
			statusMessage = new StatusMessage( StatusMessage.STATUS_ERROR, e.getMessage() );
		}
		
		fi.raka.everyconvo.api.json.JSONUtils.writeJSONStatusResponse(resp, statusMessage);
	}

}

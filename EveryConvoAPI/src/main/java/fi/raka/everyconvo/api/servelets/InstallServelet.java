package fi.raka.everyconvo.api.servelets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.raka.everyconvo.api.entities.StatusMessage;
import fi.raka.everyconvo.api.sql.SQLChain;
import fi.raka.everyconvo.api.sql.SQLChain.CreateChain;
import fi.raka.everyconvo.api.utils.Values;
import static fi.raka.everyconvo.api.sql.SQLUtils.*;
import static fi.raka.everyconvo.api.sql.SQLUtils.Values.*;

public class InstallServelet extends HttpServlet {
	
	private static final long serialVersionUID = 7200686354507643001L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String dbUser = req.getParameter("username");
		String dbPass = req.getParameter("password");
		
		if( dbUser == null && dbPass == null ) {
			File settingsFile = new File(Values.CONFIG_FILE_PATH);
			if( settingsFile.exists() ) {
				createDatabase(resp);
			}
		}
		else {
			installAndCreateConfigFile( dbUser, dbPass, resp );
		}
		
	}
	
	/**
	 * Creates database structure and configure file
	 * @param dbUser DB username
	 * @param dbPass DB user password
	 * @param resp HttpServletResponse
	 * @throws IOException
	 */
	private void installAndCreateConfigFile(String dbUser, String dbPass, HttpServletResponse resp) throws IOException {
		try {
			
			new SQLChain().open( DATABASE_BASE_URL, dbUser, dbPass );
			
			Properties props = new Properties();
			OutputStream out = new FileOutputStream(Values.CONFIG_FILE_PATH);
			
			props.setProperty(Values.CONFIG_DB_USER, dbUser);
			props.setProperty(Values.CONFIG_DB_PASS, dbPass);
			
			props.store(out, null);
			
			createDatabase(resp);
			
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			fi.raka.everyconvo.api.json.JSONUtils.writeJSONStatusResponse(resp, StatusMessage.authError() );
		}
	}
	
	/**
	 * Creates database structure
	 * @param resp HttpServletResponse
	 */
	private void createDatabase(HttpServletResponse resp) {

		StatusMessage statusMessage = null;
		SQLChain chain = new SQLChain();
		
		try {
			
			new SQLChain()
				.open(DATABASE_BASE_URL)
				.create()
				.database(DATABASE_NAME, SQLChain.CreateChain.IF_NOT_EXISTS)
				.upd()
				.close();
			
			chain.open(DATABASE_URL)
				.setAutoCommit( false )
				
				.alterOrCreate()
				.table(TABLE_USERS, CreateChain.IF_NOT_EXISTS, 
						COL_USERID + INT_NOT_NULL_AUTO_INCREMENT,
					    COL_USERNAME + varchar(60),
					    COL_DESCRIPTION + TEXT,
					    COL_WEBSITEURL + TEXT,
					    COL_LOCATION + varchar(255),
					    COL_VISIBILITY + varchar(1),
					    COL_IMAGEURL + TEXT,
					    getPrimaryKeyClause(COL_USERID),
					    unique(COL_USERNAME)
						).upd()
						
				.alterOrCreate()
				.table(TABLE_LOGIN, CreateChain.IF_NOT_EXISTS, 
						COL_USERID + INT_NOT_NULL,
						COL_PASSHASH + varchar(128),
						getForeignKeyClause(COL_USERID, TABLE_USERS)
					    ).upd()
					    
				.alterOrCreate()
				.table(TABLE_PERSONS, CreateChain.IF_NOT_EXISTS, 
						COL_USERID + INT_NOT_NULL,
					    COL_FIRSTNAME + varchar(60),
					    COL_LASTNAME + varchar(60),
					    getForeignKeyClause(COL_USERID, TABLE_USERS)
					    ).upd()
					    
				.alterOrCreate()
				.table(TABLE_GROUPS, CreateChain.IF_NOT_EXISTS, 
						COL_FULLNAME + TEXT + NOT_NULL,
						COL_USERID + INT_NOT_NULL,
						getForeignKeyClause(COL_USERID, TABLE_USERS)
					    ).upd()
					    
				.alterOrCreate()
				.table(TABLE_GROUPSUSERS, CreateChain.IF_NOT_EXISTS, 
						COL_GROUPID + INT_NOT_NULL,
					    COL_USERID + INT_NOT_NULL,
					    getForeignKeyClause(COL_GROUPID, TABLE_GROUPS, COL_USERID),
					    getForeignKeyClause(COL_USERID, TABLE_USERS)
					    ).upd()
					    
				.alterOrCreate()
				.table(TABLE_MESSAGES, CreateChain.IF_NOT_EXISTS, 
						COL_MESSAGEID + INT_NOT_NULL_AUTO_INCREMENT,
					    COL_FROMID + INT_NOT_NULL,
					    COL_TOID + INT_NOT_NULL,
					    COL_CONTENT + TEXT + NOT_NULL,
					    COL_TIMESTAMP + TIMESTAMP_DEFAULT_CURRENT_TIMESTAMP,
					    getPrimaryKeyClause(COL_MESSAGEID),
					    getForeignKeyClause(COL_FROMID, TABLE_USERS, COL_USERID),
					    getForeignKeyClause(COL_TOID, TABLE_USERS, COL_USERID)
					    ).upd()
					    
				.alterOrCreate()
				.table(TABLE_STORIES, CreateChain.IF_NOT_EXISTS, 
						COL_STORYID + INT_NOT_NULL_AUTO_INCREMENT,
					    COL_FROMID + INT_NOT_NULL,
					    COL_TOID + INT_NOT_NULL,
					    COL_CONTENT + TEXT + NOT_NULL,
					    COL_MEDIAURL + TEXT,
					    COL_TIMESTAMP + TIMESTAMP_DEFAULT_CURRENT_TIMESTAMP,
					    getPrimaryKeyClause(COL_STORYID),
					    getForeignKeyClause(COL_FROMID, TABLE_USERS, COL_USERID),
					    getForeignKeyClause(COL_TOID, TABLE_USERS, COL_USERID)
					    ).upd()
					    
				.alterOrCreate()
				.table(TABLE_LIKES, CreateChain.IF_NOT_EXISTS, 
						COL_STORYID + INT_NOT_NULL,
						COL_USERID + INT_NOT_NULL,
						getPrimaryKeyClause(COL_STORYID, COL_USERID),
						getForeignKeyClause(COL_STORYID, TABLE_STORIES),
						getForeignKeyClause(COL_USERID, TABLE_USERS)
						).upd()
						
				.alterOrCreate()
				.table(TABLE_FOLLOWS, CreateChain.IF_NOT_EXISTS, 
						COL_USERID + INT_NOT_NULL,
						COL_TOID + INT_NOT_NULL,
						getPrimaryKeyClause(COL_USERID, COL_TOID),
						getForeignKeyClause(COL_USERID, TABLE_USERS),
						getForeignKeyClause(COL_TOID, TABLE_USERS, COL_USERID)
						).upd()
					    
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

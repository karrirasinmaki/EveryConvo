package fi.raka.everyconvo.api.servelets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static fi.raka.everyconvo.api.sql.SQLUtils.*;

public class InstallServelet extends HttpServlet {
	
	private static final long serialVersionUID = 7200686354507643001L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		PrintWriter out = resp.getWriter();
		
		try {
			
			Connection conn = getConnection( DATABASE_BASE_URL );
			createDatabase( conn, DATABASE_NAME );
			conn.close();
			
			conn = getConnection();
						
			// users table
			createTable( conn, TABLE_USERS, 
			    COL_USERID + INT_NOT_NULL_AUTO_INCREMENT_ +
			    COL_USERNAME + " VARCHAR(20), " +
			    COL_DESCRIPTION + " TEXT, " +
			    COL_WEBSITEURL + " TEXT, " +
			    COL_LOCATION + " VARCHAR(255), " +
			    COL_VISIBILITY + " INT(1), " +
			    getPrimaryKeyClause(COL_USERID) + "," +
			    "UNIQUE ( username )"
			);
			// login table - stores user ids and passwords
			createTable( conn, TABLE_LOGIN, 
				COL_USERID + INT_NOT_NULL_ +
				COL_PASSHASH + " VARCHAR(128), " +
				COL_SALT + " VARCHAR(16), " +
				getForeignKeyClause(COL_USERID, TABLE_USERS)
			);
			// persons table
			createTable( conn, TABLE_PERSONS, 
				COL_USERID + INT_NOT_NULL_ +
			    COL_FIRSTNAME + " VARCHAR(60), " +
			    COL_LASTNAME + " VARCHAR(60), " +
			    getForeignKeyClause(COL_USERID, TABLE_USERS)
			);
			// groups table
			createTable( conn, TABLE_GROUPS,
				COL_USERID + INT_NOT_NULL_ +
				getForeignKeyClause(COL_USERID, TABLE_USERS)
			);
			// groupsusers table
			createTable( conn, TABLE_GROUPSUSERS,
				COL_GROUPID + INT_NOT_NULL_ +
			    COL_USERID + INT_NOT_NULL_ +
			    getForeignKeyClause(COL_GROUPID, TABLE_GROUPS, COL_USERID) + "," +
			    getForeignKeyClause(COL_USERID, TABLE_USERS)
			);
			// messages table
			createTable( conn, TABLE_MESSAGES, 
				COL_MESSAGEID + INT_NOT_NULL_AUTO_INCREMENT_ +
			    COL_FROMID + INT_NOT_NULL_ +
			    COL_TOID + INT_NOT_NULL_ +
			    COL_MESSAGE + " TEXT NOT NULL, " +
			    COL_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
			    getPrimaryKeyClause(COL_MESSAGEID) + "," +
			    getForeignKeyClause(COL_FROMID, TABLE_USERS, COL_USERID) + "," +
			    getForeignKeyClause(COL_TOID, TABLE_USERS, COL_USERID)
			);
			
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
			out.print( e.getMessage() );
		} catch (InstantiationException e) {
			e.printStackTrace();
			out.print( e.getMessage() );
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			out.print( e.getMessage() );
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			out.print( e.getMessage() );
		}
	}

}

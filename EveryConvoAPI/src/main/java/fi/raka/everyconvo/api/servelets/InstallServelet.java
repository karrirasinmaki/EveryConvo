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
			createTable( conn, "users", 
			    "userid INT NOT NULL AUTO_INCREMENT, " +
			    "username VARCHAR(20), " +
			    "description TEXT, " +
			    "websiteurl TEXT, " +
			    "location VARCHAR(255), " +
			    "visibility INT(1), " +
			    "PRIMARY KEY ( userid )"
			);
			// persons table
			createTable( conn, "persons", 
				"userid INT NOT NULL, " +
			    "firstname VARCHAR(60), " +
			    "lastname VARCHAR(60), " +
			    "FOREIGN KEY ( userid )  " +
			        "REFERENCES users( userid ) "
			);
			// groups table
			createTable( conn, "groups",
				"userid INT NOT NULL, " +
			    "FOREIGN KEY ( userid )  " +
			        "REFERENCES users( userid ) "
			);
			// groupsusers table
			createTable( conn, "groupsusers",
				"groupid INT NOT NULL, " +
			    "userid INT NOT NULL, " +
			    "FOREIGN KEY ( groupid )  " +
			        "REFERENCES groups( userid ), " +
			    "FOREIGN KEY ( userid )  " +
			        "REFERENCES users( userid ) "
			);
			// messages table
			createTable( conn, "messages", 
			    "messageid INT NOT NULL AUTO_INCREMENT, " +
			    "fromid INT NOT NULL, " +
			    //-- if toid == null, message to everyone, "wall post"
			    "toid INT, " +
			    "message TEXT NOT NULL, " +
			    "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
			    "PRIMARY KEY ( messageid ), " +
			    "FOREIGN KEY ( fromid )  " +
			        "REFERENCES users( userid ), " +
			    "FOREIGN KEY ( toid )  " +
			        "REFERENCES users( userid ) "
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

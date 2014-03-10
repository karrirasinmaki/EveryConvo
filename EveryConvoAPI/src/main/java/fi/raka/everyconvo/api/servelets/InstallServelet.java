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
			createTable( conn, "users", 
			    "userid INT NOT NULL AUTO_INCREMENT, " +
			    "username VARCHAR(20), " +
			    "description TEXT, " +
			    "websiteurl TEXT, " +
			    "location VARCHAR(255), " +
			    "visibility INT(1), " +
			    "PRIMARY KEY ( userid ) "
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

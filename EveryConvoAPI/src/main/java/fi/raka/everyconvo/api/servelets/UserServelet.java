package fi.raka.everyconvo.api.servelets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static fi.raka.everyconvo.api.sql.SQLUtils.*;
import static fi.raka.everyconvo.api.json.JSONUtils.*;

public class UserServelet extends HttpServlet {

	private static final long serialVersionUID = 1898885274959360003L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String[] urlPathParts = req.getPathInfo().substring(1).split("/");
		String requestUserName = urlPathParts[0];
		
		try {
			
			Connection conn = getConnection();
			selectFrom(conn, "users", 
				new String[] {"userid", "username", "description", "websiteurl", "location", "visibility"},
				new String[] {"username='" + requestUserName + "'"}
			);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		writeJSONResponse(resp, "{username: '" + requestUserName + "'}");
		
	}

}
package fi.raka.everyconvo.api.servelets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.raka.everyconvo.api.entities.StatusMessage;
import fi.raka.everyconvo.api.entities.User;
import static fi.raka.everyconvo.api.sql.SQLUtils.*;
import static fi.raka.everyconvo.api.sql.SQLUtils.Values.*;
import static fi.raka.everyconvo.api.json.JSONUtils.*;

public class UserServelet extends HttpServlet {

	private static final long serialVersionUID = 1898885274959360003L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String[] urlPathParts = req.getPathInfo().substring(1).split("/");
		String requestUserName = urlPathParts[0];
		User user = new User();
		
		try {
			
			writeJSONResponse( resp, user.getUserInfo(requestUserName) );
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String
		userName = req.getParameter( COL_USERNAME ),
		description = req.getParameter( COL_DESCRIPTION ),
		websiteUrl = req.getParameter( COL_WEBSITEURL ),
		location = req.getParameter( COL_LOCATION ),
		visibility = req.getParameter( COL_VISIBILITY ),
		password = req.getParameter( "pass" );
		
		StatusMessage statusMessage;
		
		if( userName.length() <= 0 || password.length() <= 0 ) {
			statusMessage = new StatusMessage(StatusMessage.STATUS_ERROR, "Username or password missing.");
		}
		else {
			User user = new User();
			statusMessage = user.createUser( userName, description, websiteUrl, location, visibility, password );
		}
		
		writeJSONStatusResponse(resp, statusMessage);
		
	}

}

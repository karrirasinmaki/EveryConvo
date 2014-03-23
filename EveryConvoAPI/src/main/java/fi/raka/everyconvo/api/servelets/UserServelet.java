package fi.raka.everyconvo.api.servelets;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.raka.everyconvo.api.entities.StatusMessage;
import fi.raka.everyconvo.api.entities.User;
import fi.raka.everyconvo.api.utils.Utils;
import static fi.raka.everyconvo.api.sql.SQLUtils.Values.*;
import static fi.raka.everyconvo.api.json.JSONUtils.*;

public class UserServelet extends HttpServlet {

	private static final long serialVersionUID = 1898885274959360003L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
			
		try {
						
			switch( req.getServletPath().substring(1) ) {
			case "user":
				String[] urlPathParts = Utils.getPathParts( req.getPathInfo() );
				String requestUserName = urlPathParts[0];
				printUser( req, resp, requestUserName );
				break;
			case "users":
				printAllUsers( req, resp );
				break;
			}
			
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

		try {
			
			switch( req.getServletPath().substring(1) ) {
			case "login":
				login(req, resp);
				break;
			case "user":
				updateUser(req, resp);
				break;
			case "create-user":
				createUser(req, resp);
				break;
			}
			
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Print user to client. If userName is not set, print current session user.
	 * @param req HttpServletRequest
	 * @param resp HttpServletResponse
	 * @param userName if null, use current session user instead
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private void printUser(HttpServletRequest req, HttpServletResponse resp, String userName) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		User user;

		if( userName == null ) {
			user = User.getSessionUser( req );
		}
		else {
			user = new User( userName, req );
		}
		
		if( user == null ) writeJSONStatusResponse( resp, StatusMessage.sessionError() );
		else writeJSONResponse( resp, user.getUserInfo() );
	}
	
	/**
	 * Print all users from database to client
	 * @param req HttpServletRequest
	 * @param resp HttpServletResponse
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private void printAllUsers(HttpServletRequest req, HttpServletResponse resp) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		writeJSONResponse( resp, User.getAllUsers() );
	}

	/**
	 * Login user. Sets user as current session user if login success.
	 * @param req HttpServletRequest having parameters [userName, password]
	 * @param resp HttpServletResponse
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private void login(HttpServletRequest req, HttpServletResponse resp) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		String
		userName = req.getParameter( COL_USERNAME ),
		password = req.getParameter( "password" );
		
		User user = new User();
		writeJSONStatusResponse( resp, user.login(userName, password, req) );
	}
	
	/**
	 * Update current user info
	 * @param req HttpServletRequest
	 * @param resp HttpServletResponse
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private void updateUser(HttpServletRequest req, HttpServletResponse resp) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		String
		userName = req.getParameter( COL_USERNAME ),
		description = req.getParameter( COL_DESCRIPTION ),
		websiteUrl = req.getParameter( COL_WEBSITEURL ),
		location = req.getParameter( COL_LOCATION ),
		imageUrl = req.getParameter( COL_IMAGEURL );
		
		User user = User.getSessionUser( req );
		if( user != null ) {
			user.setDescription(description)
				.setWebsiteUrl(websiteUrl)
				.setLocation(location)
				.setImageUrl(imageUrl)
				.update();
		}
	}
	
	/**
	 * Create new user
	 * @param req HttpServletRequest having parameters [userName, description, websiteUrl, location, visibility, password]
	 * @param resp HttpServletResponse
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private void createUser(HttpServletRequest req, HttpServletResponse resp) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		String
		userName = req.getParameter( COL_USERNAME ),
		description = req.getParameter( COL_DESCRIPTION ),
		websiteUrl = req.getParameter( COL_WEBSITEURL ),
		location = req.getParameter( COL_LOCATION ),
		visibility = req.getParameter( COL_VISIBILITY ),
		password = req.getParameter( "password" );
		
		StatusMessage statusMessage;
		
		if( userName.length() <= 0 || password.length() <= 0 ) {
			statusMessage = new StatusMessage(StatusMessage.STATUS_ERROR, "Username or password missing.");
		}
		else {
			statusMessage = User.createUser( userName, description, websiteUrl, location, visibility, password, req );
		}
		
		writeJSONStatusResponse(resp, statusMessage);
	}

}

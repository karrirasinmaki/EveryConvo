package fi.raka.everyconvo.api.servelets;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.raka.everyconvo.api.entities.Follow;
import fi.raka.everyconvo.api.entities.StatusMessage;
import fi.raka.everyconvo.api.entities.User;
import fi.raka.everyconvo.api.utils.ServeletUtils;
import fi.raka.everyconvo.api.utils.Utils;
import static fi.raka.everyconvo.api.sql.SQLUtils.Values.*;
import static fi.raka.everyconvo.api.json.JSONUtils.*;

public class UserServelet extends HttpServlet {

	private static final long serialVersionUID = 1898885274959360003L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String requestUserName = ServeletUtils.getRequestUserName( req );
		
		try {
						
			switch( ServeletUtils.getCallString(req) ) {
			case "user":
			case "login":
				String extraPathParam = ServeletUtils.getExtraPathParam( req );
				if( extraPathParam != null && extraPathParam.equalsIgnoreCase("followers") ) {
					printFollowers( req, resp, requestUserName );
				}
				if( extraPathParam != null && extraPathParam.equalsIgnoreCase("follows") ) {
					printFollows( req, resp, requestUserName );
				}
				else printUser( req, resp, requestUserName );
				break;
			case "users":
				printAllUsers( req, resp, requestUserName );
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
		
		String requestUserName = ServeletUtils.getRequestUserName( req );

		try {
			
			switch( ServeletUtils.getCallString(req) ) {
			case "login":
				login(req, resp);
				break;
			case "user":
				if( requestUserName != null ) {
					if( req.getParameter("follow") != null ) followUser(req, requestUserName);
					if( req.getParameter("unfollow") != null ) unFollowUser(req, requestUserName);
				}
				else writeJSONStatusResponse( resp, User.updateCurrentUser( req ) );
				break;
			case "create-user":
				createUser(req, resp);
				break;
			}
			
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			writeJSONStatusResponse( resp, StatusMessage.generalError(e) );
		}
		
	}
	
	private void followUser(HttpServletRequest req, String userName) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		Follow.followUser( req, userName );
	}
	private void unFollowUser(HttpServletRequest req, String userName) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		Follow.unFollowUser( req, userName );
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
		
		User user = User.loadUser( userName, req );
		if( user == null ) writeJSONStatusResponse( resp, StatusMessage.notFound("user") );
		else writeJSONResponse( resp, user );
	}
	
	private void printFollowers(HttpServletRequest req, HttpServletResponse resp, String userName) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		ArrayList<User> users = Follow.getFollowers( req, userName );
		if( users == null ) writeJSONStatusResponse( resp, StatusMessage.notFound("user") );
		else writeJSONResponse( resp, users );
	}
	
	private void printFollows(HttpServletRequest req, HttpServletResponse resp, String userName) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		ArrayList<User> users = Follow.getFollows( req, userName );
		if( users == null ) writeJSONStatusResponse( resp, StatusMessage.notFound("user") );
		else writeJSONResponse( resp, users );
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
	private void printAllUsers(HttpServletRequest req, HttpServletResponse resp, String userName) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		writeJSONResponse( resp, User.loadAll(req, userName) );
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
		
		writeJSONStatusResponse( resp, User.login(userName, password, req) );
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
		password = req.getParameter( "password" );
		
		Integer 
		visibility = Utils.parseInteger( req.getParameter( COL_VISIBILITY ) );
		
		StatusMessage statusMessage;
		
		if( userName.length() <= 0 || password.length() <= 0 ) {
			statusMessage = new StatusMessage(StatusMessage.STATUS_ERROR, "Username or password missing.");
		}
		else {
			User user = new User( userName, description, websiteUrl, location, visibility );
			user.register( password );
			statusMessage = new StatusMessage( StatusMessage.STATUS_OK, "User created with id " + user.getUserId() );
		}
		
		writeJSONStatusResponse(resp, statusMessage);
	}

}

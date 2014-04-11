package fi.raka.everyconvo.api.servelets;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.raka.everyconvo.api.entities.Group;
import fi.raka.everyconvo.api.entities.StatusMessage;
import fi.raka.everyconvo.api.utils.ServeletUtils;
import fi.raka.everyconvo.api.utils.Utils;
import static fi.raka.everyconvo.api.json.JSONUtils.*;
import static fi.raka.everyconvo.api.sql.SQLUtils.Values.*;

public class GroupServelet extends HttpServlet {

	private static final long serialVersionUID = 986315230553966286L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String requestUserName = ServeletUtils.getRequestUserName( req );
		
		try {
			switch( ServeletUtils.getCallString(req) ) {
			case "groups":
				writeJSONResponse( resp, Group.loadGroups(req) );
				break;
			case "group":
				printGroup( req, resp, requestUserName );
			}
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			writeJSONStatusResponse( resp, StatusMessage.generalError(e) );
		}
		
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		switch( ServeletUtils.getCallString(req) ) {
		case "create-group":
			createGroup(req, resp);
			break;
		}
		
		
	}
	
	private void printGroup(HttpServletRequest req, HttpServletResponse resp, String userName) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		Group group = Group.loadGroup( userName, req );
		if( group == null ) writeJSONStatusResponse( resp, StatusMessage.notFound("group") );
		else writeJSONResponse( resp, group );
	}
	
	private void createGroup(HttpServletRequest req, HttpServletResponse resp) {
		
		String
		userName = req.getParameter( COL_USERNAME ),
		description = req.getParameter( COL_DESCRIPTION ),
		websiteUrl = req.getParameter( COL_WEBSITEURL ),
		location = req.getParameter( COL_LOCATION ),
		password = req.getParameter( "password" ),
		
		fullName = req.getParameter( COL_FULLNAME );
		
		Integer 
		visibility = Utils.parseInteger( req.getParameter( COL_VISIBILITY ) );
		
		Group group = new Group(fullName, userName, description, websiteUrl, location, visibility);

		try {
			if( password == null ) {
				group.add();
			}
			else {
				group.register( password );
			}
			writeJSONStatusResponse( resp, new StatusMessage( StatusMessage.STATUS_OK, "Group created with id " + group.getUserId() ) );
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			writeJSONStatusResponse( resp, StatusMessage.generalError(e) );
		}
	}
	
}

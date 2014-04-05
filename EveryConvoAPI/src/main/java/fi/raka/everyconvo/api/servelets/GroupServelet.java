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
import static fi.raka.everyconvo.api.json.JSONUtils.*;
import static fi.raka.everyconvo.api.sql.SQLUtils.Values.*;

public class GroupServelet extends HttpServlet {

	private static final long serialVersionUID = 986315230553966286L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		try {
			writeJSONResponse( resp, Group.loadGroups(req) );
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
	
	private void createGroup(HttpServletRequest req, HttpServletResponse resp) {
		String fullName = req.getParameter( COL_FULLNAME );
		String password = req.getParameter( "password" );
		
		Group group = new Group(fullName);

		try {
			if( password == null ) {
				group.add();
			}
			else {
				group.register( password );
			}
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			writeJSONStatusResponse( resp, StatusMessage.generalError(e) );
		}
	}
	
}

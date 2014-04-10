package fi.raka.everyconvo.api.servelets;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.raka.everyconvo.api.entities.Group;
import fi.raka.everyconvo.api.entities.Person;
import fi.raka.everyconvo.api.entities.StatusMessage;
import fi.raka.everyconvo.api.entities.User;
import fi.raka.everyconvo.api.utils.ServeletUtils;
import fi.raka.everyconvo.api.utils.Utils;
import static fi.raka.everyconvo.api.json.JSONUtils.*;
import static fi.raka.everyconvo.api.sql.SQLUtils.Values.*;

public class PeopleServelet extends HttpServlet {

	private static final long serialVersionUID = 986315230553966286L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String requestUserName = ServeletUtils.getRequestUserName( req );
		
		try {
			switch( ServeletUtils.getCallString(req) ) {
			case "persons":
			case "people":
				writeJSONResponse( resp, Person.loadPersons(req) );
				break;
			case "person":
				printPerson( req, resp, requestUserName );
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
		case "create-person":
			createPerson(req, resp);
			break;
		}
		
		
	}
	
	private void printPerson(HttpServletRequest req, HttpServletResponse resp, String userName) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		
		Person person = Person.loadPerson( userName, req );
		if( person == null ) writeJSONStatusResponse( resp, StatusMessage.notFound("person") );
		else writeJSONResponse( resp, person );
	}
	
	private void createPerson(HttpServletRequest req, HttpServletResponse resp) {
		
		String
		userName = req.getParameter( COL_USERNAME ),
		description = req.getParameter( COL_DESCRIPTION ),
		websiteUrl = req.getParameter( COL_WEBSITEURL ),
		location = req.getParameter( COL_LOCATION ),
		password = req.getParameter( "password" ),
		
		firstName = req.getParameter( COL_FIRSTNAME ),
		lastName = req.getParameter( COL_LASTNAME );
		
		Integer 
		visibility = Utils.parseInteger( req.getParameter( COL_VISIBILITY ) );
		
		Person person = new Person(firstName, lastName, userName, description, websiteUrl, location, visibility);

		try {
			if( password == null || userName == null ) {
				writeJSONStatusResponse( resp, new StatusMessage(StatusMessage.STATUS_ERROR, "Username or password missing.") );
			}
			else {
				person.register( password );
			}
			writeJSONStatusResponse( resp, new StatusMessage( StatusMessage.STATUS_OK, "Person created with id " + person.getUserId() ) );
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			writeJSONStatusResponse( resp, StatusMessage.generalError(e) );
		}
	}
	
}

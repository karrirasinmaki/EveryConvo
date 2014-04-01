package fi.raka.everyconvo.api.servelets;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.raka.everyconvo.api.entities.StatusMessage;
import fi.raka.everyconvo.api.entities.Story;
import fi.raka.everyconvo.api.entities.User;
import static fi.raka.everyconvo.api.json.JSONUtils.*;

public class StoryServelet extends HttpServlet {
	
	private static final long serialVersionUID = 3447776139233999152L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String[] users = null;
		String usersString = req.getParameter("user");
		if( usersString != null ) {
			users = usersString.split(",");
		}
		
		try {
			
			writeJSONResponse( resp, Story.loadStories(users, req) );
			
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			writeJSONStatusResponse(resp, StatusMessage.generalError(e));
		}
		
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		User user = User.getSessionUser( req );
		if( user == null ) {
			writeJSONStatusResponse( resp, StatusMessage.sessionError() );
		}
		else {
			Story story = new Story( user.getUserId(), user.getUserId(), req.getParameter("content"), req.getParameter("mediaurl") );
			story.send();
		}
		
	}

}

package fi.raka.everyconvo.api.servelets;

import java.io.IOException;
import java.sql.SQLException;
import java.text.NumberFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.raka.everyconvo.api.entities.Like;
import fi.raka.everyconvo.api.entities.StatusMessage;
import fi.raka.everyconvo.api.entities.Story;
import fi.raka.everyconvo.api.entities.User;
import fi.raka.everyconvo.api.utils.Utils;
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
		
		String[] urlPathParts = Utils.getPathParts( req.getPathInfo() );
		Integer requestStoryId = null;
		try {
			requestStoryId = Integer.parseInt( urlPathParts[0] );
		}
		catch(NumberFormatException e) {
			// It wasn't number :(
		}

		try {
			
			User user = User.getSessionUser( req );
			if( user == null ) {
				writeJSONStatusResponse( resp, StatusMessage.sessionError() );
			}
			else {
				if( requestStoryId != null ) {
					if( req.getParameter("like") != null ) {
						Like like = new Like( user.getUserId(), requestStoryId );
						like.save();
					}
				}
				else {
					Story story = new Story( user.getUserId(), user.getUserId(), req.getParameter("content"), req.getParameter("mediaurl") );
					story.send();
				}
			}

		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		
	}

}

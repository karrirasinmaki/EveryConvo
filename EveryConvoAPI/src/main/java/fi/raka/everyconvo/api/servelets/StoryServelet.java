package fi.raka.everyconvo.api.servelets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.raka.everyconvo.api.entities.Story;
import static fi.raka.everyconvo.api.json.JSONUtils.*;

public class StoryServelet extends HttpServlet {
	
	private static final long serialVersionUID = 3447776139233999152L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		writeJSONResponse( resp, Story.loadStories(1) );
		
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		Story story = new Story( 1, "1", req.getParameter("content"), req.getParameter("mediaurl") );
		story.send();
		
	}

}

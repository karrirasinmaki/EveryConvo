package fi.raka.everyconvo.api.servelets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.raka.everyconvo.api.entities.Message;
import fi.raka.everyconvo.api.entities.User;
import static fi.raka.everyconvo.api.json.JSONUtils.*;

public class MessageServelet extends HttpServlet {
	
	private static final long serialVersionUID = 3447776139233999152L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		writeJSONResponse( resp, Message.loadMessages(1) );
		
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		User user = User.getSessionUser( req );
		if( user != null ) {
			Message message = new Message( user.getUserId(), req.getParameter("to"), req.getParameter("content") );
			message.send();
		}
		
	}

}

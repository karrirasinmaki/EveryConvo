package fi.raka.everyconvo.api.servelets;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.raka.everyconvo.api.entities.Message;
import fi.raka.everyconvo.api.entities.StatusMessage;
import fi.raka.everyconvo.api.entities.User;
import fi.raka.everyconvo.api.utils.ServeletUtils;
import fi.raka.everyconvo.api.utils.Utils;
import static fi.raka.everyconvo.api.json.JSONUtils.*;

public class MessageServelet extends HttpServlet {
	
	private static final long serialVersionUID = 3447776139233999152L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		User user = ServeletUtils.getSessionUser( req, resp );
		if( user == null ) {
			writeJSONStatusResponse( resp, StatusMessage.authError() );
		}
		else {
			try {
				
				Integer participantUserId = Utils.parseInteger( ServeletUtils.getRequestUserName( req ) );
				Long from = Utils.parseLong( req.getParameter("from") );
				Long to = Utils.parseLong( req.getParameter("to") );
				
				writeJSONResponse( resp, Message.loadMessages(user.getUserId(), participantUserId, from, to) );
				
			} catch (InstantiationException | IllegalAccessException
					| ClassNotFoundException | SQLException e) {
				e.printStackTrace();
				writeJSONStatusResponse( resp, StatusMessage.generalError(e) );
			}
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		Integer toid = Utils.parseInteger( req.getParameter("to") );
		String content = req.getParameter("content");
		
		User user = ServeletUtils.getSessionUser( req, resp );
		if( user == null ) {
			writeJSONStatusResponse( resp, StatusMessage.authError() );
		}
		else if( toid == null || content == null ) {
			writeJSONStatusResponse( resp, new StatusMessage(StatusMessage.STATUS_ERROR, "Please pass all required parameters (to, content"));
		}
		else {
			Message message = new Message( user.getUserId(), toid, content );
			try {
				message.send();
			} catch (InstantiationException | IllegalAccessException
					| ClassNotFoundException | SQLException e) {
				e.printStackTrace();
				writeJSONStatusResponse( resp, StatusMessage.generalError(e) );
			}
		}
		
	}

}

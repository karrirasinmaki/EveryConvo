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
				writeJSONResponse( resp, Message.loadMessages(user.getUserId(), participantUserId) );
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
		
		User user = ServeletUtils.getSessionUser( req, resp );
		if( user == null ) {
			writeJSONStatusResponse( resp, StatusMessage.authError() );
		}
		else {
			Integer toid = Utils.parseInteger( req.getParameter("to") );
			Message message = new Message( user.getUserId(), toid, req.getParameter("content") );
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

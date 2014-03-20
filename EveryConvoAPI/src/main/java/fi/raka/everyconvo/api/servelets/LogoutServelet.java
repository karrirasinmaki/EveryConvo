package fi.raka.everyconvo.api.servelets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.raka.everyconvo.api.entities.StatusMessage;
import static fi.raka.everyconvo.api.json.JSONUtils.*;

public class LogoutServelet extends HttpServlet {
	
	private static final long serialVersionUID = -4032033726169731396L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		logout( req, resp );
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		logout( req, resp );
	}
	
	private void logout(HttpServletRequest req, HttpServletResponse resp) {
		req.getSession().invalidate();
		writeJSONStatusResponse( resp, StatusMessage.sessionLogout() );
	}

}

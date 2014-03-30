package fi.raka.everyconvo.api.servelets;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.raka.everyconvo.api.entities.StatusMessage;
import fi.raka.everyconvo.api.sql.SQLChain;
import static fi.raka.everyconvo.api.sql.SQLUtils.Values.*;

public class DestroyServelet extends HttpServlet {

	private static final long serialVersionUID = -3081212684045908412L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {


		String dbUser = req.getParameter("username");
		String dbPass = req.getParameter("password");
		
		StatusMessage statusMessage;
		SQLChain chain = new SQLChain();
		try {
			
			chain.open(DATABASE_URL, dbUser, dbPass)
				.dropDatabase( DATABASE_NAME )
				.exec();
			
			statusMessage = new StatusMessage(StatusMessage.STATUS_OK, "Database destroyed");
			
		} catch (SQLException|InstantiationException|IllegalAccessException|ClassNotFoundException e) {
			e.printStackTrace();
			statusMessage = new StatusMessage( StatusMessage.STATUS_ERROR, e.getMessage() );
		}
		finally {
			try {
				if( chain.getConnection() != null ) chain.cont().close();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		fi.raka.everyconvo.api.json.JSONUtils.writeJSONStatusResponse(resp, statusMessage);
		
	}
	
}

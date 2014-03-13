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

		SQLChain chain = new SQLChain();
		try {
			
			chain.open(DATABASE_URL)
				.dropTable(TABLE_GROUPSUSERS).q(";")
				.dropTable(TABLE_GROUPSUSERS).q(";")
				.dropTable(TABLE_LOGIN).q(";")
				.dropTable(TABLE_MESSAGES).q(";")
				.dropTable(TABLE_GROUPS).q(";")
				.dropTable(TABLE_PERSONS).q(";")
				.dropTable(TABLE_USERS)
				.exec();
			
			StatusMessage statusMessage = new StatusMessage(StatusMessage.STATUS_OK, "Database destroyed");
			fi.raka.everyconvo.api.json.JSONUtils.writeJSONStatusResponse(resp, statusMessage);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			try {
				chain.cont().close();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}

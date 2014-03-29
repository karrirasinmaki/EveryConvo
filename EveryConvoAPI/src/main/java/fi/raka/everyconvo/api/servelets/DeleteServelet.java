package fi.raka.everyconvo.api.servelets;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.raka.everyconvo.api.sql.SQLChain;
import fi.raka.everyconvo.api.sql.SQLChain.SelectChain;
import static fi.raka.everyconvo.api.sql.SQLUtils.Values.*;


public class DeleteServelet extends HttpServlet {

	private static final long serialVersionUID = -6811631476666607371L;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String type = req.getParameter( "type" );
		String id = req.getParameter( "id" );
		
		SelectChain chain = null;
		try {
			
			chain = new SQLChain().open( DATABASE_URL ).delete();
			
			switch( type ) {
			case "story":
				chain.from( TABLE_STORIES )
					.whereIs( COL_STORYID, id );
				break;
			}
			
			chain.update().close();
			
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		
	}

}

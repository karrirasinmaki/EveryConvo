package fi.raka.everyconvo.api.servelets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static fi.raka.everyconvo.api.sql.SQLUtils.*;
import static fi.raka.everyconvo.api.sql.SQLUtils.Values.*;

public class DestroyServelet extends HttpServlet {

	private static final long serialVersionUID = -3081212684045908412L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		Connection conn = null;
		try {
			
			conn = getConnection();
			dropTable(conn, TABLE_GROUPSUSERS);
			dropTable(conn, TABLE_LOGIN);
			dropTable(conn, TABLE_MESSAGES);
			dropTable(conn, TABLE_GROUPS);
			dropTable(conn, TABLE_PERSONS);
			dropTable(conn, TABLE_USERS);
			
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
			if(conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	
}

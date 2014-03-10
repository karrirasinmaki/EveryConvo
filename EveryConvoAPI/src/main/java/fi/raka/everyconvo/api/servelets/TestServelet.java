package fi.raka.everyconvo.api.servelets;

import static fi.raka.everyconvo.api.sql.SQLUtils.getConnection;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static fi.raka.everyconvo.api.sql.SQLUtils.*;

public class TestServelet extends HttpServlet {
	
	private static final long serialVersionUID = -4124657385679911710L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		PrintWriter out = resp.getWriter();
		
		out.print("Hellow!");
		
		try {

			Connection conn = getConnection();
			
			insertInto(conn, "users", new String[] {"username"}, new String[] {"Moro"});
			
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		out.close();
	}

}

package fi.raka.everyconvo.api.entities;

import static fi.raka.everyconvo.api.sql.SQLUtils.getConnection;
import static fi.raka.everyconvo.api.sql.SQLUtils.selectFrom;
import static fi.raka.everyconvo.api.sql.SQLUtils.insertInto;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
	
	public ResultSet getUserInfo(String userName) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		Connection conn = getConnection();
		ResultSet rs = selectFrom(conn, "users", 
			new String[] {"userid", "username", "description", "websiteurl", "location", "visibility"},
			new String[] {"username='" + userName + "'"}
		);
		
		return rs;
	}
	
	public boolean createUser(String userName, String description, String websiteUrl, String location, int visibility) {
		Connection conn = getConnection();
		insertInto(conn, tableName, columns, values);
		
		return true;
	}

}

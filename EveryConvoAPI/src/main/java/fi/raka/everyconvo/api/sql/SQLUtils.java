package fi.raka.everyconvo.api.sql;

import static fi.raka.everyconvo.api.sql.SQLUtils.Values.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLUtils {
	
	public static String getPrimaryKeyClause(String columnName) {
		return PRIMARY_KEY + "(" + columnName + ")";
	}
	public static String getForeignKeyClause(String columnName, String refTableName, String refColumnName) {
		return FOREIGN_KEY + "(" + columnName + ") " + 
				REFERENCES + refTableName + "(" + refColumnName + ")";
	}
	public static String getForeignKeyClause(String columnName, String refTableName) {
		return getForeignKeyClause(columnName, refTableName, columnName);
	}
	
	/**
	 * Create connection to MySQL server
	 * @param url where to connect
	 * @return Connection or null if fails
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static Connection getConnection(String url) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		return DriverManager.getConnection(url, DATABASE_USER_NAME, DATABASE_USER_PASSWORD);
	}
	/**
	 * Create connection to MySQL server
	 * @return Connection or null if fails
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static Connection getConnection() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		return getConnection( DATABASE_BASE_URL + DATABASE_NAME );
	}
	
	public class Values {
		public static final String 
		DATABASE_BASE_URL = "jdbc:mysql://localhost/",
		DATABASE_NAME = "everyconvo",
		DATABASE_URL = DATABASE_BASE_URL + DATABASE_NAME,
		DATABASE_USER_NAME = "root",
		DATABASE_USER_PASSWORD = "",
		
		TABLE_LOGIN = "login",
		TABLE_USERS = "users",
		TABLE_PERSONS = "persons",
		TABLE_GROUPS = "groups",
		TABLE_GROUPSUSERS = "groupsusers",
		TABLE_MESSAGES = "messages",
		TABLE_STORIES = "stories",
		
		COL_PASSHASH = "passhash",
		COL_SALT = "salt",
		COL_USERID = "userid",
		COL_USERNAME = "username",
		COL_DESCRIPTION = "description",
		COL_WEBSITEURL = "websiteurl",
		COL_LOCATION = "location",
		COL_VISIBILITY = "visibility",
		COL_FIRSTNAME = "firstname",
		COL_LASTNAME = "lastname",
		COL_GROUPID = "groupid",
		COL_MESSAGEID = "messageid",
		COL_CONTENT = "content",
		COL_FROMID = "fromid",
		COL_TOID = "toid",
		COL_TIMESTAMP = "timestamp",
		COL_MEDIAURL = "mediaurl",
		COL_STORYID = "storyid",
		
		PRIMARY_KEY = "PRIMARY KEY ",
		FOREIGN_KEY = "FOREIGN KEY ",
		REFERENCES = "REFERENCES ",
		INT_NOT_NULL = " INT NOT NULL",
		INT_NOT_NULL_AUTO_INCREMENT = " INT NOT NULL AUTO_INCREMENT";
	}
	
}

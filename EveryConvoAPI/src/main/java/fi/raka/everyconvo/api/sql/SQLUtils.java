package fi.raka.everyconvo.api.sql;

import static fi.raka.everyconvo.api.sql.SQLUtils.Values.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang3.StringUtils;

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
	
	/**
	 * Creates new database
	 * @param conn Connection
	 * @param name of database
	 * @throws SQLException
	 */
	public static void createDatabase(Connection conn, String name) throws SQLException {
		PreparedStatement stmt;
		stmt = conn.prepareStatement("CREATE DATABASE IF NOT EXISTS " + name);
		stmt.executeUpdate();
	}
	
	/**
	 * Creates new table in database
	 * @param conn Connection
	 * @param name of table
	 * @param creationClause clause inside CREATE TABLE ( ... )
	 * @throws SQLException
	 */
	public static void createTable(Connection conn, String name, String creationClause) throws SQLException {
		PreparedStatement stmt;
		stmt = conn.prepareStatement("CREATE TABLE " + name + " (" + creationClause + ");");
		stmt.executeUpdate();
	}
	
	/**
	 * Execute SELECT query
	 * @param conn Connection
	 * @param tableName name of table
	 * @param projection which columns to get
	 * @param whereArgs where conditions. eq. columnName='value'
	 * @return ResultSet containing selected rows
	 * @throws SQLException
	 */
	public static ResultSet selectFrom(Connection conn, String tableName, String[] projection, String[] whereArgs) throws SQLException {
		String query = 
				"SELECT " + StringUtils.join(projection, ", ") + 
				" FROM " + tableName + 
				" WHERE " + StringUtils.join(whereArgs, " AND ");
		System.out.println("SQL QUERY:\n" + query);
		
		PreparedStatement stmt;
		stmt = conn.prepareStatement( query );
		return stmt.executeQuery();
	}
	
	public static ResultSet insertInto(Connection conn, String tableName, String[] columns, String[] values) throws SQLException {
		if(columns.length != values.length) throw new IllegalArgumentException("columns and values should be same length");
		
		String query = 
				"INSERT INTO " + tableName + 
				" (" + StringUtils.join(columns, ",") + ")" +
				" VALUES ('" + StringUtils.join(values, "','") + "')";
		System.out.println("SQL QUERY:\n" + query);
		
		PreparedStatement stmt;
		stmt = conn.prepareStatement( query, Statement.RETURN_GENERATED_KEYS );
		stmt.executeUpdate();
		return stmt.getGeneratedKeys();
	}
	
	public static void dropTable(Connection conn, String tableName) throws SQLException {
		String query = "DROP TABLE " + tableName;
		System.out.println("SQL QUERY:\n" + query);

		PreparedStatement stmt;
		stmt = conn.prepareStatement( query );
		stmt.executeUpdate();
	}
	
	public class Values {
		public static final String 
		DATABASE_BASE_URL = "jdbc:mysql://localhost/",
		DATABASE_NAME = "everyconvo",
		DATABASE_USER_NAME = "root",
		DATABASE_USER_PASSWORD = "",
		
		TABLE_LOGIN = "login",
		TABLE_USERS = "users",
		TABLE_PERSONS = "persons",
		TABLE_GROUPS = "groups",
		TABLE_GROUPSUSERS = "groupsusers",
		TABLE_MESSAGES = "messages",
		
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
		COL_MESSAGE = "message",
		COL_FROMID = "fromid",
		COL_TOID = "toid",
		COL_TIMESTAMP = "timestamp",
		
		PRIMARY_KEY = "PRIMARY KEY ",
		FOREIGN_KEY = "FOREIGN KEY ",
		REFERENCES = "REFERENCES ",
		INT_NOT_NULL_ = " INT NOT NULL,",
		INT_NOT_NULL_AUTO_INCREMENT_ = " INT NOT NULL AUTO_INCREMENT,";
	}
	
}

package fi.raka.everyconvo.api.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLUtils {
	
	public static String 
		DATABASE_BASE_URL = "jdbc:mysql://localhost/",
		DATABASE_NAME = "everyconvo",
		DATABASE_USER_NAME = "root",
		DATABASE_USER_PASSWORD = "";

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
		stmt = conn.prepareStatement("CREATE TABLE " + creationClause);
		stmt.executeUpdate();
	}
	
}

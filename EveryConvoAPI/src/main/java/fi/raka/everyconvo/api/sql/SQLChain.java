package fi.raka.everyconvo.api.sql;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import fi.raka.everyconvo.api.utils.Values;

public class SQLChain {
	
	private Connection conn = null;
	private StringBuilder query;
	
	public SQLChain() {
		query = new StringBuilder();
	}
	
	public Chain open(String url, String username, String password) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		conn = DriverManager.getConnection(url, username, password);
		return new Chain();
	}
	/**
	 * Opens new database connection
	 * @param url database url
	 * @return new SQLChain
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public Chain open(String url) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		Properties props = new Properties();
		InputStream in = null;
		try {
			
			in = new FileInputStream(Values.CONFIG_FILE_PATH);
			props.load(in);
			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		String username = props.getProperty(Values.CONFIG_DB_USER);
		String password = props.getProperty(Values.CONFIG_DB_PASS);
		
		return open( url, username, password );
	}
	/**
	 * Opens new SQLChain using already opened connection. Throws error if not any connection have opened
	 * @return new SQLChain
	 * @throws IllegalAccessException
	 */
	public Chain cont() throws IllegalAccessException {
		if(conn == null) throw new IllegalAccessError( "You must have opened connection before continue." );
		query.setLength(0);
		return new Chain();
	}
	
	public Connection getConnection() {
		return conn;
	}
	
	public class Chain {
		
		public Chain() {}
		
		/**
		 * Get current query as string
		 * @return query as string
		 */
		public String getQuery() {
			return query.toString();
		}
		
		/**
		 * Closes connection
		 * @throws SQLException
		 */
		public void close() throws SQLException {
			conn.close();
		}
		/**
		 * Commit SQL query
		 * @return Chain
		 * @throws SQLException
		 */
		public Chain commit() throws SQLException {
			conn.commit();
			return this;
		}
		/**
		 * Executes SQL chain
		 * @return ResultSet of generated keys
		 * @throws SQLException
		 */
		public ResultSet exec() throws SQLException {
			return executeUpdate();
		}
		/**
		 * Executes SQL chain
		 * @return Chain
		 * @throws SQLException
		 */
		public Chain exe() throws SQLException {
			exec();
			return this;
		}
		/**
		 * Executes SQL chain update.
		 * @return ResultSet of generated keys
		 * @throws SQLException
		 */
		public ResultSet update() throws SQLException {
			return executeUpdate();
		}
		
		/**
		 * Adds free text to SQL query
		 * @param q
		 * @return Chain
		 */
		public Chain q(String q) {
			query.append(q);
			return this;
		}
		public SelectChain select(String ... columns) {
			query.append( "SELECT " + StringUtils.join(columns, ",") );
			return new SelectChain();
		}
		public InsertChain insertInto(String table, String ... columns) {
			query.append( "INSERT INTO " + table + " (" + StringUtils.join(columns, ",") + ")"  );
			return new InsertChain();
		}
		public UpdateChain update(String table) {
			query.append( "UPDATE " + table );
			return new UpdateChain();
		}
		public SelectChain delete() {
			query.append( "DELETE " );
			return new SelectChain();
		}
		public CreateChain create() {
			return new CreateChain();
		}
		public Chain dropTable(String table) {
			query.append( "DROP TABLE " + table );
			return this;
		}
		public Chain dropDatabase(String database) {
			query.append( "DROP DATABASE " + database );
			return this;
		}
		
		public Chain setAutoCommit(boolean autoCommit) throws SQLException {
			conn.setAutoCommit(autoCommit);
			return this;
		}
		
		private ResultSet executeUpdate() throws SQLException {
			String q = getQuery();
			System.out.println("query\n" + q.replaceAll(",", ",\n").replaceAll(";", ";\n\n"));
			query.setLength(0);
			PreparedStatement stmt;
			stmt = conn.prepareStatement( q, Statement.RETURN_GENERATED_KEYS );
			stmt.executeUpdate();
			return stmt.getGeneratedKeys();
		}
	}
	
	public class SelectChain extends Chain {
		
		private boolean whereUsed = false;
		private boolean orFlag = false;
		private boolean caseChain = false;
		
		public SelectChain() {}
		
		@Override
		public ResultSet exec() throws SQLException {
			String q = getQuery();
			System.out.println("query\n" + q);
			query.setLength(0);
			PreparedStatement stmt;
			stmt = conn.prepareStatement( q );
			return stmt.executeQuery();
		}
		@Override
		public SelectChain q(String q) {
			super.q(q);
			return this;
		}
		public SelectChain from(String ... tables) {
			query.append( " FROM " + StringUtils.join(tables, ",") );
			return this;
		}
		public SelectChain whereIn(String column, String ... values) {
			query.append( _where() + column + " IN ('" + StringUtils.join(values, "','") + "')" );
			return this;
		}
		public SelectChain whereIs(String column, String value) {
			query.append( _where() + column + "='" + value + "'" );
			return this;
		}
		public SelectChain whereLike(String column, String value) {
			query.append( _where() + column + "LIKE '%" + value + "%'" );
			return this;
		}
		public SelectChain or() {
			orFlag = true;
			return this;
		}
		public JoinChain innerJoin(String table) {
			query.append( " INNER JOIN " + table );
			return new JoinChain( this );
		}
		public SelectChain asc(String ... columns) {
			orderBy(columns);
			query.append( " ASC" );
			return this;
		}
		public SelectChain desc(String ... columns) {
			orderBy(columns);
			query.append( " DESC" );
			return this;
		}
		public SelectChain as(String value) {
			query.append( " AS " + value );
			return this;
		}
		public SelectChain Case() {
			caseChain = true;
			query.append( " CASE " );
			return this;
		}
		public SelectChain when() {
			query.append( " WHEN " );
			return this;
		}
		public SelectChain then(String value) {
			query.append( " THEN " + value );
			return this;
		}
		public SelectChain end() {
			caseChain = false;
			whereUsed = false;
			query.append( " END " );
			return this;
		}
		
		private SelectChain orderBy(String ... columns) {
			query.append( " ORDER BY " + StringUtils.join(columns, ",") );
			return this;
		}
		private String _where() {
			if( whereUsed ) {
				return (orFlag ? " OR " : " AND ");
			}
			else {
				whereUsed = true;
				return caseChain ? "" : " WHERE ";
			}
		}
	}
	
	public class JoinChain extends Chain {
		
		private SelectChain previousChain;
		
		public JoinChain(SelectChain previousChain) {
			this.previousChain = previousChain;
		}
		
		public SelectChain on(String column, String value) {
			query.append( " ON " + column + "=" + value );
			return previousChain;
		}
		
	}
	
	public class InsertChain extends Chain {
		
		public InsertChain() {}
		
		@Override
		public InsertChain q(String q) {
			super.q(q);
			return this;
		}
		public InsertChain values(String ... values) {
			query.append( " VALUES ('" + StringUtils.join(values, "','") + "')" );
			return this;
		}
		
	}
	
	public class UpdateChain extends Chain {
		
		private boolean firstSet = true;
		
		public UpdateChain() {}
		
		@Override
		public UpdateChain q(String q) {
			super.q(q);
			return this;
		}
		
		public SelectChain doneSet() {
			return new SelectChain();
		}
		
		public UpdateChain set(String column, String value) {
			query.append( firstSet ? " SET " : "," );
			query.append( column + "='" + value + "'" );
			firstSet = false;
			return this;
		}
		
	}
	
	public class CreateChain extends Chain {
		
		public static final String IF_NOT_EXISTS = "IF NOT EXISTS";
		
		public CreateChain() {}
		
		public CreateChain database(String database, String condition) {
			query.append( "CREATE DATABASE " + condition + " " + database );
			return this;
		}
		public CreateChain database(String database) {
			return database( database, "" );
		}
		public CreateChain table(String table, String condition, String ... clauses) {
			if(condition == null) condition = "";
			else condition += " ";
			
			query.append( "CREATE TABLE " + condition + table + " (" + StringUtils.join(clauses, ",") + ")" );
			return this;
		}
		
	}

	
}

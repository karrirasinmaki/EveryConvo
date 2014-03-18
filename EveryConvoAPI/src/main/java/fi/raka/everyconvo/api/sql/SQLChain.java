package fi.raka.everyconvo.api.sql;

import static fi.raka.everyconvo.api.sql.SQLUtils.Values.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang3.StringUtils;

public class SQLChain {
	
	private Connection conn = null;
	private StringBuilder query;
	
	public SQLChain() {
		query = new StringBuilder();
	}
	
	public Chain open(String url) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		conn = DriverManager.getConnection(url, DATABASE_USER_NAME, DATABASE_USER_PASSWORD);
		return new Chain();
	}
	public Chain cont() throws IllegalAccessException {
		if(conn == null) throw new IllegalAccessError( "You must have opened connection before continue." );
		query.setLength(0);
		return new Chain();
	}
	
	public class Chain {
		
		public Chain() {}
		
		public String getQuery() {
			return query.toString();
		}
		
		public void close() throws SQLException {
			conn.close();
		}
		public Chain commit() throws SQLException {
			conn.commit();
			return this;
		}
		public ResultSet exec() throws SQLException {
			String q = getQuery();
			System.out.println("query\n" + q.replaceAll(",", ",\n").replaceAll(";", ";\n\n"));
			query.setLength(0);
			PreparedStatement stmt;
			stmt = conn.prepareStatement( q, Statement.RETURN_GENERATED_KEYS );
			stmt.executeUpdate();
			return stmt.getGeneratedKeys();
		}
		public Chain exe() throws SQLException {
			exec();
			return this;
		}
		
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
	}
	
	public class SelectChain extends Chain {
		
		private boolean whereUsed = false;
		private boolean orFlag = false;
		
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
				return " WHERE ";
			}
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

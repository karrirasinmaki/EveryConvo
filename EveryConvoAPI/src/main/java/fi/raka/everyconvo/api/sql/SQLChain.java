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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import fi.raka.everyconvo.api.utils.Values;

public class SQLChain {
	
	private Connection conn = null;
	private StringBuilder query;
	private LinkedList<Object> params;
	private Chain parentChain;
	
	public SQLChain() {
		query = new StringBuilder();
		params = new LinkedList<Object>();
	}
	public SQLChain(Connection conn) {
		super();
		this.conn = conn;
	}
	public SQLChain(Chain parentChain) {
		super();
		this.parentChain = parentChain;
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
	private Chain startChain() {
		return new Chain();
	}
	/**
	 * Opens new SQLChain using already opened connection. Throws error if not any connection have opened
	 * @return new SQLChain
	 * @throws IllegalAccessException
	 */
	public Chain cont() throws IllegalAccessException {
		if(conn == null) throw new IllegalAccessError( "You must have opened connection before continue." );
		emptyQuery();
		return new SQLChain(conn).startChain();
	}
	
	public Chain builder() {
		return new Chain();
	}
	
	public Connection getConnection() {
		return conn;
	}
	
	public void emptyQuery() {
		query.setLength(0);
		params.clear();
	}
	
	public class Chain {
		
		public Chain() { }
		
		public Chain addParam(Object obj) {
			params.add( obj );
			return this;
		}
		
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
			return executeQuery();
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
		public CreateChain alterOrCreate() {
			return new CreateChain(true);
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
		public Chain innerChain() {
			return new SQLChain(this).builder();
		}
		public String endInnerChain() {
			parentChain.addParam( params.toArray() );
			return "' + (" + getQuery() + ") + '";
		}
		
		private ResultSet executeUpdate() throws SQLException {
			String q = getQuery();
			System.out.println("\nPreparedStatement query:\n" + q.replaceAll(",", ",\n").replaceAll(";", ";\n\n") + "\n");
			PreparedStatement stmt = conn.prepareStatement( q, Statement.RETURN_GENERATED_KEYS );
			handleParams( stmt );
			System.out.println( stmt );
			stmt.executeUpdate();
			emptyQuery();
			return stmt.getGeneratedKeys();
		}
		
		private ResultSet executeQuery() throws SQLException {
			String q = getQuery();
			System.out.println("\nPreparedStatement query:\n" + q.replaceAll(",", ",\n").replaceAll(";", ";\n\n") + "\n");
			PreparedStatement stmt = conn.prepareStatement( q );
			handleParams( stmt );
			System.out.println( stmt );
			emptyQuery();
			return stmt.executeQuery();
		}
		
		private int handleArray(PreparedStatement stmt, Object[] objs, int i) 
				throws ArrayIndexOutOfBoundsException, IllegalArgumentException, SQLException {
			
			for( int index=0, l=objs.length; index<l; ++index ) {
				stmt.setObject( i, objs[index] );
				i++;
			}
			return i;
		}
		
		private int handleParams(PreparedStatement stmt, Object obj, int i) throws SQLException {
			Iterator<Object> iterator = params.iterator();
			while( iterator.hasNext() ) {
				Object o = iterator.next();
				if( o.getClass().isArray() ) {
					i = handleArray( stmt, (Object[]) o, i );
				}
				else {
					stmt.setObject( i, o );
					i++;
				}
			}
			return i;
		}
		public void handleParams(PreparedStatement stmt) {
			try {
				handleParams( stmt, params, 1 );
			} catch (SQLException e) {
				System.out.println("Error handling params.");
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
		
		public String generateQuestionMarks(int number) {
			StringBuffer b = new StringBuffer( number*2 - 1 );
			for(int i=0; i<number; ++i) {
				if( i > 0 ) b.append(',');
				b.append('?');
			}
			return b.toString();
		}
	}
	
	public class SelectChain extends Chain {
		
		private boolean whereUsed = false;
		private boolean orFlag = false;
		private boolean caseChain = false;
		
		public SelectChain() {}
		
		@Override
		public SelectChain q(String q) {
			super.q(q);
			return this;
		}
		public SelectChain from(String ... tables) {
			query.append( " FROM " + StringUtils.join(tables, ",") );
			return this;
		}
		public SelectChain whereIn(String column, Object ... values) {
			params.add( values );
			query.append( _where() + column + " IN (" + generateQuestionMarks(values.length) + ")" );
			return this;
		}
		public SelectChain whereIs(String column, Object value) {
			params.add( value );
			query.append( _where() + column + "=" + generateQuestionMarks(1) );
			return this;
		}
		public SelectChain whereIsCol(String column, String column2) {
			query.append( _where() + column + "=" + column2 );
			return this;
		}
		public SelectChain contains(String column, Object value) {
			params.add( value );
			query.append( _where() + column + " LIKE '%" + generateQuestionMarks(1) + "%'" );
			return this;
		}
		public SelectChain whereLike(String column, Object value) {
			params.add( value );
			query.append( _where() + column + " LIKE " + generateQuestionMarks(1) );
			return this;
		}
		public SelectChain or() {
			orFlag = true;
			return this;
		}
		public SelectChain and() {
			orFlag = false;
			return this;
		}
		public JoinChain innerJoin(String table) {
			query.append( " INNER JOIN " + table );
			return new JoinChain( this );
		}
		public SelectChain asc(Object ... columns) {
			orderBy(columns);
			query.append( " ASC" );
			return this;
		}
		public SelectChain desc(Object ... columns) {
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
		public SelectChain limit(Object limit) {
			params.add( limit );
			query.append( " LIMIT " + generateQuestionMarks(1) );
			return this;
		}
		public SelectChain offset(Object offset) {
			params.add( offset );
			query.append( " OFFSET " + generateQuestionMarks(1) );
			return this;
		}
		public SelectChain gte(String column, Object value) {
			params.add( value );
			query.append( _where() + column + ">" + generateQuestionMarks(1) );
			return this;
		}
		public SelectChain lte(String column, Object value) {
			params.add( value );
			query.append( _where() + column + "<" + generateQuestionMarks(1) );
			return this;
		}
		
		private SelectChain orderBy(Object ... columns) {
			params.add( columns );
			query.append( " ORDER BY " + generateQuestionMarks(columns.length) );
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
		
		public SelectChain on(String column, String column2) {
			query.append( " ON " + column + "=" + column2 );
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
		public InsertChain values(Object ... values) {
			params.add( values );
			query.append( " VALUES (" + generateQuestionMarks(values.length) + ")" );
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
		
		public UpdateChain set(String column, Object value) {
			params.add( value );
			query.append( firstSet ? " SET " : "," );
			query.append( column + "=" + generateQuestionMarks(1) );
			firstSet = false;
			return this;
		}
		
	}
	
	public class AlterChain extends Chain {
		
		public AlterChain() {}
		
		public AlterChain alter(String table) {
			query.append( " ALTER TABLE " + table );
			return this;
		}
		public AlterChain add(String ... clauses) {
			query.append( " ADD " + StringUtils.join(clauses, ",") );
			return this;
		}
		public AlterChain modify(String ... clauses) {
			query.append( " MODIFY COLUMN " + StringUtils.join(clauses, ",") );
			return this;
		}
		
	}
	
	public class CreateChain extends Chain {
		
		public static final String IF_NOT_EXISTS = "IF NOT EXISTS";
		private boolean force = false;
		
		public CreateChain() {}
		public CreateChain(boolean force) {
			this.force = force;
		}
		
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
			
			if( force ) alterOneByOne( table, clauses );
			query.append( "CREATE TABLE " + condition + table + " (" + StringUtils.join(clauses, ",") + ")" );
			return this;
		}
		
		private CreateChain alterOneByOne(String table, String ... clauses) {
			for( String clause : clauses ) {
				try {
					new AlterChain().alter( table ).add( clause ).update();
				} catch (SQLException e) {
					try {
						new AlterChain().alter( table ).modify( clause ).update();
					} catch (SQLException e2) {
						e2.printStackTrace();
					}
				}
			}
			return this;
		}
		
	}

	
}

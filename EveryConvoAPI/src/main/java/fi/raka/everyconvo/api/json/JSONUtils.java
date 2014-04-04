package fi.raka.everyconvo.api.json;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

import fi.raka.everyconvo.api.entities.Paged;
import fi.raka.everyconvo.api.entities.StatusMessage;

public class JSONUtils {
	
	/**
	 * Writes JSON return to client
	 * @param resp HttpServletResponse
	 * @param json JSON data as string
	 */
	public static void writeJSONResponse(HttpServletResponse resp, String json) {
		resp.setContentType("application/json;charset=utf-8");
		try {
			PrintWriter out = resp.getWriter();
			out.flush();
			out.print( json );
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public static void writeJSONResponse(HttpServletResponse resp, ResultSet rs) {
		writeJSONResponse( resp, rsToJSON(rs) );
	}
	public static void writeJSONResponse(HttpServletResponse resp, Object o) {
		writeJSONResponse( resp, toDataJSON(o) );
	}
	public static void writeJSONPagedResponse(HttpServletResponse resp, Paged paged) {
		writeJSONResponse( resp, toJSON(paged) );
	}
	public static void writeJSONStatusResponse(HttpServletResponse resp, StatusMessage statusMessage) {
		writeJSONResponse( resp, toDataJSON(statusMessage) );
	}
	
	public static String rsToJSON(ResultSet rs) {		
		JsonObject json = new JsonObject();
		JsonArray rows = new JsonArray();
		try {
			if( rs != null ) {
				ResultSetMetaData rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount();
				rs.beforeFirst();
				while( rs.next() ) {
					JsonObject row = resultSetRowToJson(rs, rsmd, columnCount);
					rows.add( row );
				}
				json.add( "data", rows );
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return json.toString();
	}

	public static String toJSON(Object o) {
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		return gson.toJson( o );
	}
	public static String toDataJSON(Object o) {
		Data data = new Data( o );
		return toJSON( data );
	}
	
	private static JsonObject resultSetRowToJson(ResultSet rs, ResultSetMetaData rsmd, int columnCount) throws SQLException {
		JsonObject row = new JsonObject();
		// column indexes are 1-based, rather than 0-based
		for(int i=1; i<=columnCount; ++i) {
			String columnName = rsmd.getColumnName(i);
			Gson gson = new Gson();
			
			switch( rsmd.getColumnType(i) ) {
		    case java.sql.Types.BIGINT:
		    case java.sql.Types.INTEGER:
		    case java.sql.Types.TINYINT:
		    case java.sql.Types.SMALLINT:
		    	row.addProperty(columnName, rs.getInt(i));
		        break;
		    case java.sql.Types.BOOLEAN:
		    	row.addProperty(columnName, rs.getBoolean(i));
		        break;
		    case java.sql.Types.DOUBLE:
		    	row.addProperty(columnName, rs.getDouble(i)); 
		        break;
		    case java.sql.Types.FLOAT:
		    	row.addProperty(columnName, rs.getFloat(i));
		        break;
		    case java.sql.Types.LONGNVARCHAR:
		    case java.sql.Types.NVARCHAR:
		    	row.addProperty(columnName, rs.getNString(i));
		        break;
		    case java.sql.Types.LONGVARCHAR:
		    case java.sql.Types.VARCHAR:
		    	row.addProperty(columnName, rs.getString(i));
		        break;
		    case java.sql.Types.DATE:
		    	row.addProperty(columnName, rs.getDate(i).toString());
		        break;
		    case java.sql.Types.TIMESTAMP:
		    	row.addProperty(columnName, rs.getTimestamp(i).toString());  
		        break;
			case java.sql.Types.ARRAY:
				row.addProperty(columnName, gson.toJson( rs.getArray(i) ));
		        break;
		    case java.sql.Types.BLOB:
				row.addProperty(columnName, gson.toJson( rs.getBlob(i) ));
		         break;
		    default:
		    	row.addProperty(columnName, gson.toJson( rs.getObject(i) ));
		        break;
			}
		}
		
		return row;
	}
	
	static class Data {
		private Object data;
		public Data(Object o) {
			data = o;
		}
	}
	
}

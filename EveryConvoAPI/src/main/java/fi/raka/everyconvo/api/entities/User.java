package fi.raka.everyconvo.api.entities;

import static fi.raka.everyconvo.api.sql.SQLUtils.getConnection;
import static fi.raka.everyconvo.api.sql.SQLUtils.selectFrom;
import static fi.raka.everyconvo.api.sql.SQLUtils.insertInto;
import static fi.raka.everyconvo.api.sql.SQLUtils.Values.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import fi.raka.everyconvo.api.utils.PasswordHash;

public class User {
	
	public ResultSet getUserInfo(String userName) 
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		
		Connection conn = getConnection();
		ResultSet rs = selectFrom(conn, "users", 
			new String[] {"userid", "username", "description", "websiteurl", "location", "visibility"},
			new String[] {"username='" + userName + "'"}
		);
		
		return rs;
	}
	
	public boolean createUser(String userName, String description, String websiteUrl, String location, String visibility, String password) {
		
		Connection conn = null;
		
		try {
			
			conn = getConnection();
			conn.setAutoCommit( false );
			
			ResultSet generatedKeys = insertInto(conn, TABLE_USERS, 
					new String[] { COL_USERNAME, COL_DESCRIPTION, COL_WEBSITEURL, COL_LOCATION, COL_VISIBILITY },
					new String[] { userName, description, websiteUrl, location, visibility }
				);
			
			// Get generated userid
			generatedKeys.first();
			long userId = generatedKeys.getLong(1);
			
			String passhash = null;
			try {
				passhash = PasswordHash.createHash( password );
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (InvalidKeySpecException e) {
				e.printStackTrace();
			}
			if(passhash == null) return false;
			
			insertInto(conn, TABLE_LOGIN,
					new String[] { COL_USERID, COL_PASSHASH },
					new String[] { "" + userId, passhash }
				);
			
			conn.commit();
			
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		finally {
			if( conn != null ) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return true;
	}

}

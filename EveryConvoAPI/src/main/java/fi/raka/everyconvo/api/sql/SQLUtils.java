package fi.raka.everyconvo.api.sql;

import static fi.raka.everyconvo.api.sql.SQLUtils.Values.*;

import org.apache.commons.lang3.StringUtils;

public class SQLUtils {
	
	/**
	 * Get PRIMARY_KEY(columnName) SQL clause
	 * @param columnName
	 * @return PRIMARY_KEY(columnName)
	 */
	public static String getPrimaryKeyClause(String ... columns) {
		return PRIMARY_KEY + "(" + StringUtils.join(columns, ",") + ")";
	}
	/**
	 * Get FOREIGN_KEY(columnName) REFERENCES refTableName (refColumnName) SQL clause
	 * @param columnName
	 * @param refTableName
	 * @param refColumnName
	 * @return FOREIGN_KEY(columnName) REFERENCES refTableName (refColumnName)
	 */
	public static String getForeignKeyClause(String columnName, String refTableName, String refColumnName) {
		return FOREIGN_KEY + "(" + columnName + ") " + 
				REFERENCES + refTableName + "(" + refColumnName + ")";
	}
	/**
	 * Get FOREIGN_KEY(columnName) refTableName REFERENCES(columnName) SQL clause, where key and reference key are same
	 * @param columnName
	 * @param refTableName
	 * @return FOREIGN_KEY(columnName) REFERENCES refTableName (columnName)
	 */
	public static String getForeignKeyClause(String columnName, String refTableName) {
		return getForeignKeyClause(columnName, refTableName, columnName);
	}
	/**
	 * Get UNIQUE(columnName) SQL clause
	 * @param columnName
	 * @return UNIQUE(columnName)
	 */
	public static String unique(String columnName) {
		return "UNIQUE(" + columnName + ")";
	}
	/**
	 * Get VARCHAR(len) SQL clause
	 * @param len
	 * @return VARCHAR(len) SQL clause
	 */
	public static String varchar(int len) {
		return " VARCHAR(" + len + ")";
	}
	
	public class Values {
		public static final String 
		DATABASE_BASE_URL = "jdbc:mysql://localhost/",
		DATABASE_NAME = "everyconvo",
		DATABASE_URL = DATABASE_BASE_URL + DATABASE_NAME,
		
		TABLE_LOGIN = "login",
		TABLE_USERS = "users",
		TABLE_PERSONS = "persons",
		TABLE_GROUPS = "groups",
		TABLE_GROUPSUSERS = "groupsusers",
		TABLE_MESSAGES = "messages",
		TABLE_STORIES = "stories",
		TABLE_LIKES = "likes",
		
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
		COL_IMAGEURL = "imageurl",
		
		PRIMARY_KEY = "PRIMARY KEY ",
		FOREIGN_KEY = "FOREIGN KEY ",
		REFERENCES = "REFERENCES ",
		TEXT = " TEXT",
		NOT_NULL = " NOT NULL",
		INT_NOT_NULL = " INT NOT NULL",
		INT_NOT_NULL_AUTO_INCREMENT = " INT NOT NULL AUTO_INCREMENT",
		TIMESTAMP_DEFAULT_CURRENT_TIMESTAMP = " TIMESTAMP DEFAULT CURRENT_TIMESTAMP";
		
	}
	
}

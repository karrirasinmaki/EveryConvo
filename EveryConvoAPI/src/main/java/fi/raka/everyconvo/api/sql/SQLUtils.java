package fi.raka.everyconvo.api.sql;

import static fi.raka.everyconvo.api.sql.SQLUtils.Values.*;

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
		INT_NOT_NULL = " INT NOT NULL",
		INT_NOT_NULL_AUTO_INCREMENT = " INT NOT NULL AUTO_INCREMENT";
		
	}
	
}

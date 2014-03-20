package fi.raka.everyconvo.api.utils;

public class Utils {

	public static String[] getPathParts(String path) {
		if( path == null || path.length() == 0 ) return new String[] { path };
		return path.substring(1).split("/");
	}
	
}

package fi.raka.everyconvo.api.utils;

public class Utils {

	public static String[] getPathParts(String path) {
		return path.substring(1).split("/");
	}
	
}

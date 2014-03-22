package fi.raka.everyconvo.api.utils;

import java.util.Random;

public class Utils {

	public static String[] getPathParts(String path) {
		if( path == null || path.length() == 0 ) return new String[] { path };
		return path.substring(1).split("/");
	}
	
	public static String getRandomHexString(int numchars){
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        while(sb.length() < numchars){
            sb.append(Integer.toHexString(r.nextInt()));
        }

        return sb.toString().substring(0, numchars);
    }
	
}

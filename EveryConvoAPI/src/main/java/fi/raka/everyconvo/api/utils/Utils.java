package fi.raka.everyconvo.api.utils;

import static fi.raka.everyconvo.api.json.JSONUtils.writeJSONStatusResponse;

import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.raka.everyconvo.api.entities.StatusMessage;
import fi.raka.everyconvo.api.entities.User;

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
	
	/**
	 * Parse String to Integer
	 * @param s
	 * @return Integer, null if String can't be parsed
	 */
	public static Integer parseInteger(String s) {
		try {
			return Integer.parseInt( s );
		}
		catch(NumberFormatException e) {
			return null;
		}
	}
	
	/**
	 * Get current session user. If user not found, write authError StatusMessage
	 * @param req
	 * @param resp
	 * @return Current session user or null
	 */
	public static User getSessionUser(HttpServletRequest req, HttpServletResponse resp) {
		User user = User.getSessionUser( req );
		if( user == null ) {
			writeJSONStatusResponse( resp, StatusMessage.authError() );
		}
		return user;
	}
	
}

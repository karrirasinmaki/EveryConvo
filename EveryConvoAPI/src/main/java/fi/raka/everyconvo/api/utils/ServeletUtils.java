package fi.raka.everyconvo.api.utils;

import static fi.raka.everyconvo.api.json.JSONUtils.writeJSONStatusResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.raka.everyconvo.api.entities.StatusMessage;
import fi.raka.everyconvo.api.entities.User;

public class ServeletUtils {
	
	public static String getFullURL(HttpServletRequest req) {
	    StringBuffer requestURL = req.getRequestURL();
	    String queryString = req.getQueryString();

	    if (queryString == null) {
	        return requestURL.toString();
	    } else {
	        return requestURL.append('?').append(queryString).toString();
	    }
	}
	
	public static String getBaseURL(HttpServletRequest req) {
		String url = req.getRequestURL().toString();
		return url.substring(0, url.length() - req.getRequestURI().length()) + req.getContextPath();
	}
	
	public static String getCallString(HttpServletRequest req) {
		return req.getServletPath().substring(1);
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
	
	public static String getRequestUserName (HttpServletRequest req) {
		String[] urlPathParts = Utils.getPathParts( req.getPathInfo() );
		String requestUserName = urlPathParts[0];
		if( requestUserName == null || requestUserName.length() == 0 ) requestUserName = null;
		return requestUserName;
	}
	
}

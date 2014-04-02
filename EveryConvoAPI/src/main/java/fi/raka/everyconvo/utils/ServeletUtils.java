package fi.raka.everyconvo.utils;

import javax.servlet.http.HttpServletRequest;

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
	
}

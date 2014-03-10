package fi.raka.everyconvo.api.json;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

public class JSONUtils {
	
	/**
	 * Writes JSON return to client
	 * @param resp HttpServletResponse
	 * @param json JSON data as string
	 */
	public static void writeJSONResponse(HttpServletResponse resp, String json) {
		resp.setContentType("application/json");
		try {
			PrintWriter out = resp.getWriter();
			out.print(json);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}

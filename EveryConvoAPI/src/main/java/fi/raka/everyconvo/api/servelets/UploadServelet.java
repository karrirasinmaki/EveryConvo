package fi.raka.everyconvo.api.servelets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import fi.raka.everyconvo.api.entities.StatusMessage;
import fi.raka.everyconvo.api.entities.User;
import fi.raka.everyconvo.api.utils.Utils;
import static fi.raka.everyconvo.api.json.JSONUtils.*;

@MultipartConfig
public class UploadServelet extends HttpServlet {

	private static final long serialVersionUID = 4103079722080320972L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		User user = User.getSessionUser( req );
		if( user == null ) {
			writeJSONStatusResponse( resp, StatusMessage.authError() );
		}
		else {
			Part filePart = req.getPart( "file" );
			String fileName = "u" + user.getUserId() + 
					"t" + System.currentTimeMillis() + 
					"f" + Utils.getRandomHexString(7) + 
					getFileExtension(getFilename(filePart));
			String filePath = saveFile( filePart, fileName );
			writeJSONResponse( resp, "{\"fileurl\":\"" + filePath + "\"}" );
		}
	}
	
	private String saveFile(Part part, String fileName) throws IOException {
		String filePath = null;
		String folderName = "u";
		InputStream fileContent = part.getInputStream();
		byte[] buffer = new byte[8 * 1024];
		
		try {
			filePath = getServletContext().getRealPath( folderName ) + File.separator + fileName;
			OutputStream os = new FileOutputStream( filePath );
			try {
				int bytesRead;
				while( (bytesRead = fileContent.read(buffer)) != -1 ) {
					os.write( buffer, 0, bytesRead );
				}
			}
			finally {
				os.close();
			}
		}
		finally {
			fileContent.close();
		}
		
		System.out.println("File saved to: " + filePath);
		return "/" + folderName + "/" + fileName;
	}
	
	private String getFileExtension(String fileName) {
		return fileName.substring( fileName.lastIndexOf('.') );
	}
	
	private String getFilename(Part part) {
	    for (String cd : part.getHeader("content-disposition").split(";")) {
	        if (cd.trim().startsWith("filename")) {
	            String filename = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
	            return filename.substring(filename.lastIndexOf('/') + 1).substring(filename.lastIndexOf('\\') + 1); // MSIE fix.
	        }
	    }
	    return null;
	}
	
}

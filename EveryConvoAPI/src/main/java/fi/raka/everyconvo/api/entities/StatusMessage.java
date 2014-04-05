package fi.raka.everyconvo.api.entities;

public class StatusMessage {
	
	public static final String 
	STATUS_ERROR = "error",
	STATUS_OK = "ok";
	
	public String status;
	public String message;

	public StatusMessage(String status, String message) {
		this.status = status;
		this.message = message;
	}
	
	private static StatusMessage generateError(String status, String message, Exception e) {
		return new StatusMessage(status, message + "\n" + e.getMessage());
	}
	private static StatusMessage generateError(String status, String message) {
		return new StatusMessage(status, message);
	}
	
	public static StatusMessage sessionError() {
		return generateError(STATUS_ERROR, "Invalid session.");
	}
	
	public static StatusMessage sessionLogout() {
		return generateError(STATUS_OK, "Session end.");
	}
	
	public static StatusMessage authError() {
		return generateError(StatusMessage.STATUS_ERROR, "Error with authentication.");
	}
	
	public static StatusMessage authOk() {
		return generateError(StatusMessage.STATUS_OK, "Logged in.");
	}
	
	public static StatusMessage updateCompleted() {
		return generateError(STATUS_OK, "Update operation completed");
	}
	
	public static StatusMessage generalError(Exception e) {
		return generateError(STATUS_ERROR, "Something went wrong.", e);
	}
}

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
	
	public static StatusMessage sessionError() {
		return new StatusMessage(STATUS_ERROR, "Invalid session.");
	}
	
	public static StatusMessage sessionLogout() {
		return new StatusMessage(STATUS_OK, "Session end.");
	}
	
}

package com.api.user.exception;

//Define the POJO class which will be sent as response by the ControllerAdvice class.
public class UserException extends Exception {
	
	private String errorMessage;
	private String requestedURI;
	private int errorCode;
	
	public UserException() {
			
	}
	public UserException(String errorMessage) {
		super(errorMessage);	
	}
	
	
	public UserException(String errorMessage, int errorCode) {
		this(errorMessage);
		this.errorCode = errorCode;
	}


	public UserException(String errorMessage, String requestedURI, int errorCode) {
		this(errorMessage,errorCode);
		this.requestedURI = requestedURI;
	}

	public UserException(String errorMessage, String requestedURI, int errorCode,Throwable throwable) {
		super(errorMessage,throwable);
		this.errorCode=errorCode;
		this.requestedURI = requestedURI;
	}

	
	public String getErrorMessage() {
		return errorMessage;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int i) {
		this.errorCode = i;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getRequestedURI() {
		return requestedURI;
	}
	public void setRequestedURI(String requestedURI) {
		this.requestedURI = requestedURI;
	}


	@Override
	public String toString() {
		return "UserException [errorMessage=" + errorMessage + ", requestedURI=" + requestedURI + ", errorCode="
				+ errorCode + "]";
	}
	
	

}

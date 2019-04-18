package com.api.user.exception;

public class DuplicateEmailException extends RuntimeException {
	private static final long serialVersionUID=1L;
	public DuplicateEmailException()
	{
		
	}
	public DuplicateEmailException(String message)
	{
		super(message);
	}

}

package com.api.user.exception;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.api.user.response.Response;

/*When throws an exception, it will catch and convert it to the meaningful message.*/
@ControllerAdvice
public class ExceptionHandlerController {
	@ExceptionHandler(Exception.class)
	  public ResponseEntity<Response> exceptionResolver(Exception ex) {
		Response response = new Response();
	        response.setStatusCode(100);
	        response.setStatusMessage(ex.getMessage());
	  return new ResponseEntity<Response>(response, HttpStatus.BAD_REQUEST);
	    }
	
	  @ExceptionHandler(UserException.class)
	  public ResponseEntity<Response> exceptionResolver(UserException ex,HttpServletRequest req) {
		  Response response = new Response();
		  response.setStatusCode(100);
	        response.setStatusMessage(ex.getMessage());
	        return new ResponseEntity<Response>(response, HttpStatus.BAD_REQUEST);
	    }
	  
	  @ExceptionHandler(value=DuplicateEmailException.class)
	  public ResponseEntity<String> handleDuplicateEmailException(DuplicateEmailException ex)
	  {
		  return new ResponseEntity<String>(ex.getMessage(), HttpStatus.ALREADY_REPORTED);
	  }
	  
	  @ExceptionHandler(value=UserNotFoundException.class)
	  public ResponseEntity<String> handleUserNotFoundException(DuplicateEmailException ex)
	  {
		  return new ResponseEntity<String>(ex.getMessage(), HttpStatus.NOT_FOUND);
	  }
	
}
	


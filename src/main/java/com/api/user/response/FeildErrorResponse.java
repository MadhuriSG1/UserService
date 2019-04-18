package com.api.user.response;

import java.util.List;

import org.springframework.validation.FieldError;

public class FeildErrorResponse extends Response{
	List<FieldError> errors;

	public FeildErrorResponse(List<FieldError> fieldErrors) {
		this.errors= fieldErrors;
	}

	public List<FieldError> getErrors() {
		return errors;
	}

	public void setErrors(List<FieldError> errors) {
		this.errors = errors;
	}

	@Override
	public String toString() {
		return "FeildErrorResponse [errors=" + errors + "]";
	}
	
}

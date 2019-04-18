package com.api.user.util;

import java.io.Serializable;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class EmailBody implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String to;
	String subject;
	String body;
}

package com.api.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@ToString
 public class CollaboratorUserDetails {
	private String email;
	private String image;
	
	
 public CollaboratorUserDetails () {
		
	}
	public CollaboratorUserDetails(String email) {
		this.email = email;
	}
	
	
}

package com.api.user.dto;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@ToString
public class UserDTO {
	@Column(name="user_name")
	@NotNull(message="Can not be null")
	private String name;
	
	@Email(regexp =  "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.(?:[A-Z]{2,}|com|org))+$",message="Not valid")
	private String email;
	
	@Pattern(regexp = "[0-9]{10}", message = "Number Should Only Be Digit And 10 digit only")
	private String mobileNumber;
	
	private String password;

	
	

}

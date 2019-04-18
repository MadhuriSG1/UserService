package com.api.user.entity;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

	@Entity
	@Getter
	@Setter
	@ToString
	@Table(name="user_details")
	public class User implements Serializable
	{
		private static final long serialVersionUID = 1L;
		@Id
		@GeneratedValue(strategy=GenerationType.AUTO)
		@Column(name="id")
		private Long id;
		
		@Column(name="user_name")
		@NotNull(message="Can not be null")
		private String name;
		
		@Email(regexp =  "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.(?:[A-Z]{2,}|com|org))+$",message="Not valid")
		private String email;
		
		@Pattern(regexp = "[0-9]{10}", message = "Number Should Only Be Digit And 10 digit only")
		private String mobileNumber;
		
		private String password;
		
		private boolean isverified;
		
		private String userProfilePic;
		
	}

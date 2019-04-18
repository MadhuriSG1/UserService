package com.api.user.dto;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Table(name="user_login")
public class LoginDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	@Id
	private String email;
	private String password;	
}

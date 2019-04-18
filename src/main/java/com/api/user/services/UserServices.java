package com.api.user.services;
import java.util.List;
import javax.validation.Valid;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.api.user.dto.UserDTO;
import com.api.user.dto.UserDetails;
import com.api.user.dto.CollaboratorUserDetails;
import com.api.user.dto.LoginDTO;
import com.api.user.entity.User;
import com.api.user.exception.UserException;

public interface UserServices {

	public User register(@Valid UserDTO userdto) throws UserException;

	public String login(@Valid LoginDTO loginuser) throws UserException;

	public User forgotPassword(String email) throws UserException;

	public User passwordReset(String token) throws UserException;

	void verifyUser(Long userid);

	public Long collabUserId(String token, String email) throws UserException;

	public List<CollaboratorUserDetails> userEmails(List<Long> ids);
	
	public UserDetails getUserDetails(String token) throws UserException;

	/*
	 * public void setProfileImage(String token, String uuidString);
	 * 
	 * public String getProfileImage(long userId);
	 */

	

	/*
	 * public void uploadProfileImage(String token, MultipartFile file);
	 * 
	 * public Resource loadFileAsResource(String fileName) throws UserException;
	 */
	
	


}

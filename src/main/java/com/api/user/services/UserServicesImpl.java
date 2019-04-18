package com.api.user.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.api.user.dto.UserDTO;
import com.api.user.dto.UserDetails;
import com.api.user.applicationconfig.ApplicationConfiguration;
import com.api.user.dto.CollaboratorUserDetails;
import com.api.user.dto.LoginDTO;
import com.api.user.entity.User;
import com.api.user.exception.DuplicateEmailException;
import com.api.user.exception.UserException;
import com.api.user.repository.UserRepository;
import com.api.user.util.UserServiceUtil;
import com.api.user.util.UserToken;

@Service
public class UserServicesImpl implements UserServices {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserServicesImpl.class);

	@Autowired
	private UserRepository userrepositoty;
	@Autowired
	private PasswordEncoder passwordencoder;
	@Autowired
	private ModelMapper modelmapper;
	
	@Autowired
	private RedisServices redisServices;
	
	

	/**
	 * Register user if email already not exist
	 */
	@Override
	public User register(UserDTO userdto) throws UserException {
		LOGGER.info("Info message");
		Optional<User> registrationstatus = userrepositoty.findByEmail(userdto.getEmail());
		if (!registrationstatus.isPresent()) {
			User user = modelmapper.map(userdto, User.class);
			user.setPassword(passwordencoder.encode(user.getPassword()));
			return userrepositoty.save(user);
		}
		LOGGER.info("Email Already Exist");
		throw new DuplicateEmailException("Email Already Exist");
	}

	/**
	 * Login user after match email and password with database user details
	 */
	@Override
	public String login(@Valid LoginDTO loginuser) throws UserException {
		return userrepositoty.findByEmail(loginuser.getEmail())
				.map(fromDBUser -> this.validUser(fromDBUser, loginuser.getPassword()))
				.orElseThrow(() -> new UserException("Not valid User"));
	}

	/**
	 * Entered password encoded and compare with database password if both are
	 * matches then return token
	 * 
	 * @param fromDBUser
	 * @param password
	 * @return
	 * @throws Exception
	 */
	private String validUser(User fromDBUser, String password) {
		boolean isValid = passwordencoder.matches(password, fromDBUser.getPassword());
		String token = null;
		if (isValid) 
		{
			try 
			{
				token = UserToken.generateToken(fromDBUser.getId());
			} catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		return token;
	}

	/**
	 * This function takes email and return user
	 */
	@Override
	public User forgotPassword(String email) throws UserException 
	{
		Optional<User> userAvailable = userrepositoty.findByEmail(email);
		if (userAvailable.isPresent()) 
		{
			return userAvailable.get();
		} else 
		{
			LOGGER.info("Email Address not found");
			throw new UserException("Email Address not found");
		}
	}

	/*
	 * This function takes token and return user
	 */
	@Override
	public User passwordReset(String token) throws UserException 
	{
		long userid = UserServiceUtil.verifyToken(token);
		return userrepositoty.findById(userid).get();
	}

	@Override
	public void verifyUser(Long userId) 
	{
		
		 ApplicationContext ctx = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);
	        RedisServices redisService = ctx.getBean(RedisServices.class);
	        User userFromRedis=redisService.getValue(userId);
	        System.out.println("redis user "+userFromRedis);
	       
		User user = userrepositoty.findById(userId).get();
		 if(userFromRedis!=null) {
			 user.setIsverified(true);
		 }
		 else {
			 
		
		user.setIsverified(true);
		userrepositoty.save(user);
		  redisService.setValue(userId, user);
		System.out.println(user);
		LOGGER.info("User Verified Sucessfully");
		 }
	}
	
	@Override
	public UserDetails getUserDetails(String token) throws UserException {
		System.out.println("User details from token ");
		
		long userId = UserServiceUtil.verifyToken(token);

	        User userFromRedis=redisServices.getValue(userId);
	        System.out.println("redis user "+userFromRedis);
	        if(userFromRedis!=null) {
				 System.out.println("User from redis :"+userFromRedis);
				 UserDetails userDetails=modelmapper.map(userFromRedis, UserDetails.class);
					return userDetails;
			 }
	        else {
		User user= userrepositoty.findById(userId).get();
		System.out.println("User from database :"+user);
		UserDetails userDetails=modelmapper.map(user, UserDetails.class);
		redisServices.setValue(userId, user);
		return userDetails;
	        }
		
	}

	@Override
	public Long collabUserId(String token, String email) throws UserException 
	{
		System.out.println(email);
		UserServiceUtil.verifyToken(token);
		return userrepositoty.findByEmail(email).map(x -> {
			return (Long) x.getId();
		}).orElse(-1L);

	}

	@Override
	public List<CollaboratorUserDetails> userEmails(List<Long> ids) 
	{
		List<CollaboratorUserDetails> list = new ArrayList<>();
		userrepositoty.findEmailofUsers(ids).get().forEach(x -> list.add(new CollaboratorUserDetails(x)));
		return list;
	}

	/*
	 * Set Profile Image by passing token and Universal unique Identifier
	 */
	/*
	 * @Override public void setProfileImage(String token, String uuidString) { long
	 * userId; try { userId = UserServiceUtil.verifyToken(token); User user =
	 * userrepositoty.findById(userId).get(); user.setUserProfilePic(uuidString);
	 * userrepositoty.save(user); System.out.println(user);
	 * 
	 * } catch (UserException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); }
	 * 
	 * }
	 * 
	 * @Override public String getProfileImage(long userId) { User
	 * user=userrepositoty.findById(userId).get(); return user.getUserProfilePic();
	 * }
	 * 
	 * 
	 * private final Path imageLocation =
	 * Paths.get("src/main/resources/profileImages");
	 * 
	 * @Override public void uploadProfileImage(String token, MultipartFile file) {
	 * try { Files.copy(file.getInputStream(),this.imageLocation.resolve(file.
	 * getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING); } catch
	 * (IOException e) { // TODO Auto-generated catch block e.printStackTrace(); }
	 * 
	 * 
	 * }
	 * 
	 * @Override public Resource loadFileAsResource(String fileName) throws
	 * UserException { try { Path filePath =
	 * this.imageLocation.resolve(fileName).normalize(); Resource resource = new
	 * UrlResource(filePath.toUri()); if(resource.exists()) { return resource; }
	 * else { throw new UserException("File not found " + fileName); } } catch
	 * (MalformedURLException ex) { throw new UserException("File not found " +
	 * fileName); }
	 * 
	 * }
	 */

	
}

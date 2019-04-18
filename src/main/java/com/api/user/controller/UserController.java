package com.api.user.controller;


import java.io.IOException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.api.user.dto.CollaboratorUserDetails;
import com.api.user.dto.LoginDTO;
import com.api.user.dto.UserDTO;
import com.api.user.dto.UserDetails;
import com.api.user.entity.User;
import com.api.user.exception.UserException;
import com.api.user.repository.UserRepository;
import com.api.user.response.Response;
import com.api.user.services.AmazonS3ClientService;
import com.api.user.services.UserServices;
import com.api.user.util.EmailBody;
import com.api.user.util.EmailUtil;
import com.api.user.util.Publisher;
import com.api.user.util.UserServiceUtil;
import com.api.user.util.UserToken;
import lombok.extern.slf4j.Slf4j;

/**
 * @author bridgelabz
 * @RestController-Map incoming request to appropriate Class
 * @RequestMapping-Map incoming request to appropriate method
 * @RequestBody-
 *
 */
@RestController
@CrossOrigin(value = "http://localhost:4200", exposedHeaders = { "Authorization" })
@RequestMapping("/api/user")
@Slf4j
public class UserController {
	@Autowired
	private UserServices userservices;
	@Autowired
	private UserRepository userrepository;
	@Autowired
	private PasswordEncoder passwordencoder;
	@Autowired
	private AmazonS3ClientService amazonS3ClientService;

	@Autowired
	private Publisher publisher;
	
	@Autowired
	private EmailBody emailBody;

	/**
	 * @param user
	 * @param bindingResult
	 * @param request
	 * @return
	 * @throws Exception
	 * 
	 * 
	 */
	@PostMapping("/register")
	public ResponseEntity<Response> register(@Valid @RequestBody UserDTO userdto, BindingResult bindingResult,
			HttpServletRequest request) throws Exception {

		if (bindingResult.hasErrors()) {
			throw new UserException("Invalid data ", 100);
		}

		User user = userservices.register(userdto);
		String toEmail = "madhurig231@gmail.com";
		String link = UserServiceUtil.getURL(request, "verify", user);
		log.info("User Register Sucessfully  " + link);
		emailBody.setTo(toEmail);
		emailBody.setSubject("Testing");
		emailBody.setBody( "verify mail by clicking below link:" + link);
		publisher.produceMsg(emailBody);
//		EmailUtil.mailSend(toEmail, "Testing", "verify mail by clicking below link:" + link);
		Response response = new Response();
		response.setStatusCode(200);
		response.setStatusMessage("Sucessfully register");
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	/**
	 * @param loginuser
	 * @param bindingResult
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/login")
	public ResponseEntity<Response> login(@Valid @RequestBody LoginDTO logindto, HttpServletResponse header)
			throws UserException {
		log.info("An INFO Message");
		String token;
		try {
			token = userservices.login(logindto);
		} catch (Exception e) {
			throw new UserException(e.getMessage(), 100);
		}
		header.addHeader("Authorization", token);
		log.info("User Loged In successfully: " + token);
		Response response = new Response();
		response.setStatusCode(200);
		response.setStatusMessage("User Login Sucessfully ");
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	/**
	 * @param token
	 * @param res
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	@RequestMapping(value = "/verify/{token}", method = RequestMethod.GET)
	public ResponseEntity<Response> verify(@PathVariable String token, HttpServletResponse res)
			throws UserException, IOException {
		System.out.println("Verification starts ");
		System.out.println(token);
		Response response = new Response();
		long userid = UserServiceUtil.verifyToken(token);
		res.sendRedirect("http://www.google.com");
		if (userid > 0) {
			userservices.verifyUser(userid);
			response.setStatusCode(200);
			log.info("User account verified successfully");
			return new ResponseEntity<Response>(response, HttpStatus.OK);
		} else {
			throw new UserException("User account Not verified", -200);
		}
	}

	/**
	 * Process form submission from forgotPassword page
	 * 
	 * @param emailuserservices
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/forgotpassword")
	public ResponseEntity<?> forgotPassword(@RequestParam String email, HttpServletRequest request) throws Exception {
		Response response = new Response();
		response.setStatusCode(200);
		response.setStatusMessage("Reset-Mail Send To Your Eamil Address");
		// Lookup user in database by e-mail
		User user = userservices.forgotPassword(email);
		EmailUtil.mailSend(email, "Password Reset", this.getBody(request, user, "resetpassword"));
		log.info("Forgot password: Reset password mail sent to your email address");
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	/**
	 * Display form to reset password
	 * 
	 * 
	 * 
	 * @param token
	 * @param request
	 * @return
	 * @throws Exception
	 * @throws Exceptioncause
	 */
	@RequestMapping(value = "/resetpassword/{token}")
	public ResponseEntity<?> resetPassword(@PathVariable("token") String token, HttpServletRequest request)
			throws Exception {
		// Token found in DB
		User user = userservices.passwordReset(token);
		EmailUtil.mailSend(user.getEmail(), "ChangePassword", this.getBody(request, user, "resetpage"));
		log.info("Redirect to new password reset page");
		Response response = new Response();
		response.setStatusCode(200);
		response.setStatusMessage("Redirect To New Password Set Page");
		return new ResponseEntity<Response>(response, HttpStatus.OK);

	}

	/**
	 * Process reset password form
	 * 
	 * @param token
	 * @param loginuser
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/resetpage/{token}", method = RequestMethod.POST)
	public ResponseEntity<?> resetPage(@PathVariable("token") String token, @RequestBody LoginDTO loginuser,
			HttpServletRequest request) throws UserException

	{
		long userid = UserServiceUtil.verifyToken(token);
		User user = userrepository.findById(userid).get();
		log.info("userid is" + userid);
		user.setPassword(passwordencoder.encode(loginuser.getPassword()));
		userrepository.save(user);
		log.info("Password Set Successfully");
		Response response = new Response();
		response.setStatusCode(200);
		response.setStatusMessage("Password Set Successfully");
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	
	}

	@GetMapping("/collabpersonid")
	public ResponseEntity<Long> getUser(@RequestParam String email, @RequestHeader("token") String token)
			throws UserException {
		// System.out.println(token);
		Long id = userservices.collabUserId(token, email);
		return new ResponseEntity<Long>(id, HttpStatus.OK);
	}

	@PostMapping("/collabuserdetails")
	public ResponseEntity<List<CollaboratorUserDetails>> getDetails(@RequestBody List<Long> ids) throws UserException {
		System.out.println("jjjjjjjjjjj");
		return new ResponseEntity<List<CollaboratorUserDetails>>(userservices.userEmails(ids), HttpStatus.OK);
	}
	
	  @PostMapping("/uploads3file")
	    public ResponseEntity<User> uploadFile(@RequestParam("file") MultipartFile file, @RequestHeader("token") String token) throws UserException
	    {
		  System.out.println("file uploading starts");
		  User user= amazonS3ClientService.uploadFileToS3Bucket(file, true,token);

		/*
		 * Map<String, String> response = new HashMap<>(); response.put("message",
		 * "file [" + file.getOriginalFilename() +
		 * "] uploading request submitted successfully.");
		 */
	         return new ResponseEntity<User>(user, HttpStatus.OK);
	       
	    }
	  
	  @GetMapping("/gets3file")
	    public ResponseEntity<String> getS3File(@RequestHeader("token") String token) throws UserException
	    {
		  System.out.println("Get s3 file started");
	    	String S3_FILE_LINK=amazonS3ClientService.getS3FileLink(token);
	    	System.out.println("S3_FILE_LINK" +S3_FILE_LINK);
	    	  return new ResponseEntity<String>(S3_FILE_LINK, HttpStatus.OK);
	    	
	    }

	    @DeleteMapping("/deletefile")
	    public Map<String, String> deleteFile(@RequestParam("file_name") String fileName)
	    {
	        amazonS3ClientService.deleteFileFromS3Bucket(fileName);

	        Map<String, String> response = new HashMap<>();
	        response.put("message", "file [" + fileName + "] removing request submitted successfully.");

	        return response;
	}
	    
	    @GetMapping("/getfile")
	    public ResponseEntity<Response> getFile(@RequestParam("file_name") String fileName)
	    {
	    	System.out.println("get file request sent");
	    	amazonS3ClientService.getFileFromS3Bucket(fileName);
	    	Response response=new Response();
			response.setStatusCode(200);
			response.setStatusMessage("get file sucessfully");
			return new ResponseEntity<Response>(response, HttpStatus.OK);
	    	
	    	
	    }
	  
	    

	/**
	 * Here  passing profile image and and token to upload image
	 * generate UUID which is unique number and set to user so we can get user profile image easily
	 * @param token
	 * @param uploadProfileImage
	 * @return
	 * @throws UserException
	 * 
	 */
	/*@PostMapping("/uploadimage")
	public ResponseEntity<String> UploadProfileImage(@RequestHeader("token") String token,
			@RequestParam("file") MultipartFile file)throws IOException {
		System.out.println("Uplaod images =xbjsxhcdgbhsjgcf");
		System.out.println("upload image  "+file);
		
	    userservices.uploadProfileImage(token,file);
		return new ResponseEntity<String>("Image Uploaded Successfully ", HttpStatus.OK);
	}
	@GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) throws UserException {
        // Load file as Resource
        Resource resource = userservices.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }*/

	/*
	@PostMapping("/uploadimage")
	public ResponseEntity<Response> UploadProfileImage(@RequestHeader("token") String token,
			@RequestParam("file") MultipartFile file)throws IOException, SerialException, SQLException {
		System.out.println("Uplaod images =xbjsxhcdgbhsjgcf");
		System.out.println("upload image  "+file);
		UUID uuid = UUID.randomUUID();
		System.out.println("uuid "+uuid);
		String uuidString = uuid.toString();
		System.out.println(uuidString);
		
		try {
			Files.copy(file.getInputStream(), this.imageLocation.resolve(uuidString),
					StandardCopyOption.REPLACE_EXISTING);
			userservices.setProfileImage(token, uuid.toString());
		} catch (Exception e) {
			e.printStackTrace();
}
		Response response = new Response();
		response.setStatusCode(200);
		response.setStatusMessage("Image Uploaded Successfully");
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}*/
	
	/*@GetMapping("/getprofileimage/{token}")
	public Resource getProfileImage(@PathVariable String token) throws UserException
	{
		System.out.println("asfdasgfdsgafsdgash");
		long userId=UserServiceUtil.verifyToken(token);
		String fileName=userservices.getProfileImage(userId);
		System.out.println("filename  " +fileName);
		Path file = imageLocation.resolve(fileName);
		System.out.println("file "+file);
		try {
		Resource resource=new UrlResource(file.toUri());
	    if(resource.exists()||resource.isReadable())
	    {
		return resource;
	    }
		}
		catch(IOException e) 
		{
			e.printStackTrace();
		}
		return null;
		
		
		
		
	}*/
	
	
	@GetMapping("/getuserdetails")
	public ResponseEntity<UserDetails> getUserDetails(@RequestHeader("token") String token)throws UserException
	{
		return new ResponseEntity<UserDetails>(userservices.getUserDetails(token), HttpStatus.OK);
		
	}

	private String getBody(HttpServletRequest req, User user, String link) throws Exception {
		return "http://localhost:4200/" + link + "/" + UserToken.generateToken(user.getId());
	}

}


package com.api.user.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.api.user.entity.User;
import com.api.user.exception.UserException;
import com.api.user.repository.UserRepository;
import com.api.user.util.UserServiceUtil;


@Component
public class AmazonS3ClientServiceImpl implements AmazonS3ClientService {
	
	@Autowired
	private UserRepository userrepositoty;
	
	@Autowired
	private UserServices userservices;
	
	  private String awsS3AudioBucket;
	   
	    private AmazonS3 amazonS3;
	    
	    @Value("${aws.s3.imageUrl}")
		private String s3ImageUrl;
	    
	    private static final Logger logger = LoggerFactory.getLogger(AmazonS3ClientServiceImpl.class);

	    @Autowired
	    public AmazonS3ClientServiceImpl(Region awsRegion, AWSCredentialsProvider awsCredentialsProvider, String awsS3AudioBucket) 
	    {
	        this.amazonS3 = AmazonS3ClientBuilder.standard()
	                .withCredentials(awsCredentialsProvider)
	                .withRegion(awsRegion.getName()).build();
	        this.awsS3AudioBucket = awsS3AudioBucket;
	}

	@Async
	@Override
	public User uploadFileToS3Bucket(MultipartFile multipartFile, boolean enablePublicReadAccess,String token) throws UserException {
       long userId = 0;
		try {
		 userId = UserServiceUtil.verifyToken(token);
		}
		catch (UserException e) {
			e.printStackTrace();
		}
		 User user = userrepositoty.findById(userId).get();
		 
			
	
		String key = userId+generateKey(multipartFile);
		
        String fileName = multipartFile.getOriginalFilename();
        System.out.println("filename "+fileName);
       

		ObjectMetadata ob = new ObjectMetadata();
		ob.setContentDisposition(multipartFile.getName().replaceAll(" ", "_"));
		ob.setContentLength(multipartFile.getSize());
		ob.setContentType(multipartFile.getContentType());
		ob.setContentDisposition("inline");
		System.out.println("ob ="+ob);

        try {
            //creating the file in the server (temporarily)
            File file = new File(fileName);
            System.out.println("file "+file);
            FileOutputStream fos = new FileOutputStream(file);
            System.out.println("fos "+fos);
            fos.write(multipartFile.getBytes());
            System.out.println("fos "+fos);
            fos.close();


            PutObjectRequest putObjectRequest = new PutObjectRequest(this.awsS3AudioBucket, fileName, file);
            System.out.println("this.awsS3AudioBucket "+this.awsS3AudioBucket);
            System.out.println("putObjectRequest  "+putObjectRequest);

            if (enablePublicReadAccess) {
                putObjectRequest.withCannedAcl(CannedAccessControlList.Private);
            }
            this.amazonS3.putObject(putObjectRequest);
            System.out.println("amazonS3 ="+amazonS3);
            //removing the file created in the server
            System.out.println("file "+file);
            
            
          
            //file.delete();
        } catch (IOException | AmazonServiceException ex) {
            logger.error("error [" + ex.getMessage() + "] occurred while uploading [" + fileName + "] ");
}
        String imageUrl = s3ImageUrl + this.awsS3AudioBucket + "/" + key;
       user.setUserProfilePic(imageUrl);
        
       
        userrepositoty.save(user);
        System.out.println("user = "+user);
		return user;
				
	}
	
	@Override
	public String getS3FileLink(String token) throws UserException {
		long userId = 0;
		try {
		 userId = UserServiceUtil.verifyToken(token);
		}
		catch (UserException e) {
			e.printStackTrace();
		}
		 User user = userrepositoty.findById(userId).get();
		 
		String S3_FILE_LINK=user.getUserProfilePic();
		System.out.println("s3link = "+S3_FILE_LINK);
		return S3_FILE_LINK;
		
	}

	private String generateKey(MultipartFile multipartFile) {
		
		return "_" + Instant.now().getEpochSecond() + "_" + multipartFile.getOriginalFilename().replaceAll(" ", "_");
	}

	@Async
	@Override
	public void deleteFileFromS3Bucket(String fileName) {
		 try {
	            amazonS3.deleteObject(new DeleteObjectRequest(awsS3AudioBucket, fileName));
	        } catch (AmazonServiceException ex) {
	            logger.error("error [" + ex.getMessage() + "] occurred while removing [" + fileName + "] ");
	}		
	}


	
	@Override
	@Async
	public void getFileFromS3Bucket(String fileName) {
		
		S3Object file=amazonS3.getObject(awsS3AudioBucket, fileName);
		System.out.println(file);
		S3ObjectInputStream objectContent =file.getObjectContent();
		System.out.println("objectContent ="+objectContent);
		try
		{
			IOUtils.copy(objectContent, new FileOutputStream("/home/bridgeit/Documents/images"));
			
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		
		
	}

	

}

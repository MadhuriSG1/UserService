package com.api.user.services;

import org.springframework.web.multipart.MultipartFile;

import com.api.user.entity.User;
import com.api.user.exception.UserException;

public interface AmazonS3ClientService {
	User uploadFileToS3Bucket(MultipartFile multipartFile, boolean enablePublicReadAccess, String token)
			throws UserException;

	void deleteFileFromS3Bucket(String fileName);

	void getFileFromS3Bucket(String fileName);

	String getS3FileLink(String token) throws UserException;

}

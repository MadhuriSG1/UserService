package com.api.user.util;

import java.io.UnsupportedEncodingException;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author admin1
 *
 */
@Slf4j
public class UserToken {
	
	public static String TOKEN_SECRET="s4T2zOIWHMM1sxq" ;
	public static String generateToken(long userId) throws Exception
	{
		 try {
			  Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
	           String token = JWT.create()
	                    .withClaim("ID",userId)
	                    .sign(algorithm);
	                   
	            return token;
	        } catch (UnsupportedEncodingException | JWTCreationException e) {
	            log.error(e.getMessage(), e);
	            throw new Exception(e);
	        } 
	}
	

}

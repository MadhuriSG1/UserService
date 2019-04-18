package com.api.user.util;

import javax.servlet.http.HttpServletRequest;

import com.api.user.entity.User;
import com.api.user.exception.UserException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;

/**
 * @author admin1
 *
 */
public class UserServiceUtil {
	
	/**
	 * @param request
	 * @param value
	 * @return
	 * @throws Exception 
	 */
	public static String getURL(HttpServletRequest request, String value,User user) throws Exception {
		String sentToken = UserToken.generateToken(user.getId());
		String url = request.getRequestURI().toString();
		String link = url.substring(0, url.lastIndexOf("/")).concat("/").concat(value).concat("/");
		System.out.println(link);
		String link1 = "http://localhost:4200" + link+sentToken;
		System.out.println(link1);
		//String link1 = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + link;
		return link1;
	}
	
	

	/**
	 * This function takes token and return userid
	 */
	public  static long verifyToken(String token) throws UserException {
		long userid = 0;
		try {
			Verification verification = JWT.require(Algorithm.HMAC256(UserToken.TOKEN_SECRET));
			JWTVerifier jwtverifier = verification.build();
			DecodedJWT decodedjwt = jwtverifier.verify(token);
			Claim claim = decodedjwt.getClaim("ID");
			userid = claim.asLong();
			System.out.println(userid);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return userid;
	}

}

package com.api.user.services;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.stereotype.Service;

import com.api.user.entity.User;

@Service
public class RedisServices {
	
	 @Autowired
	 private RedisTemplate< Long, User > template;
	 
	 
	 public void setValue( final Long userid, final User user ) {
		    template.opsForValue().set(userid, user );
		    template.expire( userid, 5*60, TimeUnit.SECONDS );
		}
	 
	 public User getValue( final Long userid ) {
		    return template.opsForValue().get( userid );
		}

}

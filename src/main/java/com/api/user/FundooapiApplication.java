package com.api.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
@EnableCaching
//@EnableAutoConfiguration
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class FundooapiApplication {
	public static void main(String[] args) {
		SpringApplication.run(FundooapiApplication.class, args);
		
	}

}


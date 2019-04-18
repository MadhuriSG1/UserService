package com.api.user.applicationconfig;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.api.user.entity.User;


@Configuration
public class ApplicationConfiguration {
	
	
	@Value("${aws.s3.access.key}")
	private String awsKeyId;

	@Value("${aws.access.key.secret}")
	private String awsKeySecret;

	@Value("${aws.region}")
	private String awsRegion;

	
	
	@Value("${aws.s3.audio.bucket}")
	private String awsS3AudioBucket;
	
	/*
	 * @Bean(name = "s3ImageUrl") public String getImageUrl() { return s3ImageUrl; }
	 */
	
    @Bean(name = "awsKeyId")
    public String getAWSKeyId() {
        return awsKeyId;
    }

    @Bean(name = "awsKeySecret")
    public String getAWSKeySecret() {
        return awsKeySecret;
    }

    @Bean(name = "awsRegion")
    public Region getAWSPollyRegion() {
        return Region.getRegion(Regions.fromName(awsRegion));
    }

    @Bean(name = "awsCredentialsProvider")
    public AWSCredentialsProvider getAWSCredentials() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(this.awsKeyId, this.awsKeySecret);
        return new AWSStaticCredentialsProvider(awsCredentials);
    }

    @Bean(name = "awsS3AudioBucket")
    public String getAWSS3AudioBucket() {
        return awsS3AudioBucket;
}

	/*
	 * @Bean public AmazonS3 s3client() {
	 * 
	 * BasicAWSCredentials awsCreds = new BasicAWSCredentials(awsKeyId,
	 * awsKeySecret); return AmazonS3ClientBuilder.standard().withRegion(awsRegion)
	 * .withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build(); }
	 */
	
	
	@Bean
	public PasswordEncoder getPasswordEncoder()
	{
		return new BCryptPasswordEncoder();
	}
	@Bean
	public ModelMapper getModelMapper()
	{
		return new ModelMapper();
	}	
	
	@Bean
	JedisConnectionFactory jedisConnectionFactory() {
	    return new JedisConnectionFactory();
	}
	
	@Bean
	public RedisTemplate<Long, User> redisTemplate() {
	    RedisTemplate<Long, User> template = new RedisTemplate<>();
	    template.setConnectionFactory(jedisConnectionFactory());
	    return template;
	}
	
	@Bean
	   public RedisCacheManager cacheManager() {
		RedisCacheManager rcm = RedisCacheManager.builder(jedisConnectionFactory())
		  .transactionAware()
		  .build();
		return rcm;
	   }

	

}

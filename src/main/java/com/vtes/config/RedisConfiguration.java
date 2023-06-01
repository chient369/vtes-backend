package com.vtes.config;

import java.nio.charset.Charset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfiguration {
	
	 @Value("${spring.redis.host}")
	    private String redisHost;

	    @Value("${spring.redis.port}")
	    private int redisPort;

	    @Value("${spring.redis.password}")
	    private String redisPassword;

	    @Bean
	    public LettuceConnectionFactory lettuceConnectionFactory() {
	        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(redisHost, redisPort);
	        redisConfig.setPassword(RedisPassword.of(redisPassword));
	        return new LettuceConnectionFactory(redisConfig);
	    }
	    @Bean
	    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
	        RedisTemplate<String, Object> template = new RedisTemplate<>();
	        template.setConnectionFactory(lettuceConnectionFactory);

	        template.setKeySerializer(new StringRedisSerializer(Charset.forName("UTF-8")));
	        template.setHashKeySerializer(new StringRedisSerializer(Charset.forName("UTF-8")));

	        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
	        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

	        template.afterPropertiesSet();
	        return template;
	    }
}

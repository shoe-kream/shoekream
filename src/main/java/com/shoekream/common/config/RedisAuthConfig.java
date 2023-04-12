package com.shoekream.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisAuthConfig {

    @Value("${spring.data.redis.auth.host}")
    private String host;
    @Value("${spring.data.redis.auth.port}")
    private int port;

    @Bean
    public RedisConnectionFactory redisAuthConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    @Bean(name = "redisTemplate")
    public StringRedisTemplate stringRedisTemplate() {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(redisAuthConnectionFactory());
        return stringRedisTemplate;
    }
}
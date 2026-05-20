package com.sky.config;

//import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//
//@Configuration
//@Slf4j
//public class RedisConfiguration {
//
//    @Bean
//    public RedisTemplate  redisTemplate(RedisConnectionFactory redisConnectionFactory)
//    {
//        log.info("开始创建RedisTemplate对象...");
//        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(redisConnectionFactory);
//
//        // 设置 key 的序列化器
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//
//        // 设置 value 的序列化器（使用 JSON 序列化）
//        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
//
//        // 设置 hash key 的序列化器
//        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
//
//        // 设置 hash value 的序列化器（改用 String 序列化，支持数值操作）
//        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
//
//        // 初始化 RedisTemplate
//        redisTemplate.afterPropertiesSet();
//
//        return redisTemplate;
//    }
//}

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfiguration {

    @Bean
    public RedisTemplate  redisTemplate(RedisConnectionFactory redisConnectionFactory)
    {
        log.info("开始创建RedisTemplate对象...");
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 设置 key 的序列化器
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // 设置 value 的序列化器（使用 FastJson，支持 Java 8 时间类型）
        redisTemplate.setValueSerializer(new FastJsonRedisSerializer<>(Object.class));

        // 设置 hash key 的序列化器
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        // 设置 hash value 的序列化器（改用 String 序列化，支持数值操作）
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());

        // 初始化 RedisTemplate
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }
}

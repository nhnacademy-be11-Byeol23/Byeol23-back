package com.nhnacademy.byeol23backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);

		// 1. Key Serializer: String으로 설정 (가장 일반적인 Key 타입)
		template.setKeySerializer(new StringRedisSerializer());

		// 2. Value Serializer: JSON으로 설정하여 DTO 객체(CartOrderRequest 등)의 직렬화를 담당하게 합니다.
		// DTO 객체 저장 시 발생할 수 있는 타입 문제(Map<Long, Integer>)를 해결하기 위해 JSON을 사용합니다.
		Jackson2JsonRedisSerializer<Object> jsonSerializer = new Jackson2JsonRedisSerializer<>(Object.class);

		template.setValueSerializer(jsonSerializer);

		// Hash 필드도 String과 JSON으로 설정 (비회원 장바구니 Hash 저장 시 필요)
		template.setHashKeySerializer(new StringRedisSerializer());
		template.setHashValueSerializer(jsonSerializer);

		template.afterPropertiesSet();
		return template;
	}
}

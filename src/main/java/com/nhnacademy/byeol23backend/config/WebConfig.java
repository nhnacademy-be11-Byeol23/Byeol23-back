package com.nhnacademy.byeol23backend.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.nhnacademy.byeol23backend.bookset.book.interceptor.ViewerIdInterceptor;
import com.nhnacademy.byeol23backend.bookset.book.resolver.ViewerIdResolver;
import com.nhnacademy.byeol23backend.cartset.cart.interceptor.CustomerIdentificationInterceptor;
import com.nhnacademy.byeol23backend.cartset.cart.resolver.CustomerIdentifierResolver;
import com.nhnacademy.byeol23backend.utils.JwtParser;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
	private final JwtParser jwtParser;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new ViewerIdInterceptor(jwtParser))
			.addPathPatterns("/api/books/*");

		registry.addInterceptor(new CustomerIdentificationInterceptor(jwtParser))
			.addPathPatterns("/api/carts/**")
			.addPathPatterns("/api/payments/**");
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(new ViewerIdResolver());
		resolvers.add(new CustomerIdentifierResolver());
	}
}

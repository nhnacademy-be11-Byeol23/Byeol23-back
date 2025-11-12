package com.nhnacademy.byeol23backend.config;

import com.nhnacademy.byeol23backend.bookset.book.interceptor.GuestIdCookieInterceptor;
import com.nhnacademy.byeol23backend.bookset.book.interceptor.ViewerIdInterceptor;
import com.nhnacademy.byeol23backend.bookset.book.resolver.ViewerIdResolver;
import com.nhnacademy.byeol23backend.bookset.book.utils.JwtParser;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final JwtParser jwtParser;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new GuestIdCookieInterceptor())
                .addPathPatterns("/api/**");

        registry.addInterceptor(new ViewerIdInterceptor(jwtParser))
                .addPathPatterns("/api/books/*");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new ViewerIdResolver());
    }
}

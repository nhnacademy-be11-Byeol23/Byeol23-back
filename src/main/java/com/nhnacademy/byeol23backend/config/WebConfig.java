package com.nhnacademy.byeol23backend.config;

import com.nhnacademy.byeol23backend.bookset.book.resolver.ViewerIdResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new ViewerIdResolver());
    }
}

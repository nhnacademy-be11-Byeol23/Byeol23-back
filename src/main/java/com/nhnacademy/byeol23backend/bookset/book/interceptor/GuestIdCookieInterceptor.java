package com.nhnacademy.byeol23backend.bookset.book.interceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.UUID;

@Slf4j
public class GuestIdCookieInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorization = request.getHeader("Authorization");
        if(StringUtils.isNoneBlank(authorization) && authorization.startsWith("Bearer ")) return true;

        if(request.getCookies() != null &&
        Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals("guestId")).anyMatch(cookie -> StringUtils.isNoneBlank(cookie.getValue()))) {
            return true;
        }

        Cookie cookie = createCookie();

        response.addCookie(cookie);

        log.info("비회원 쿠키 생성: {}", cookie.getValue());
        return true;
    }

    private String generateGuestId() {
        return UUID.randomUUID().toString();
    }

    private Cookie createCookie() {
        Cookie cookie = new Cookie("guestId", generateGuestId());
        cookie.setPath("/");
        cookie.setMaxAge(3600);
        cookie.setHttpOnly(true);
        return cookie;
    }
}

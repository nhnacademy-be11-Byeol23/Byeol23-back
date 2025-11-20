package com.nhnacademy.byeol23backend.bookset.book.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseCookie;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.UUID;

@Slf4j
public class GuestIdCookieInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(!"GET".equalsIgnoreCase(request.getMethod())) return true;
        String authorization = request.getHeader("Authorization");
        if(StringUtils.isNoneBlank(authorization) && authorization.startsWith("Bearer ")) return true;

        if(request.getCookies() != null &&
        Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals("guestId")).anyMatch(cookie -> StringUtils.isNoneBlank(cookie.getValue()))) {
            return true;
        }

        String guestId = generateGuestId();
        String cookie = createCookie(guestId);
        response.addHeader("Set-Cookie", cookie);

        request.setAttribute("guestId", guestId);

        log.info("비회원 쿠키 생성: {}", guestId);
        return true;
    }

    private String generateGuestId() {
        return UUID.randomUUID().toString();
    }

    private String createCookie(String value) {
        return ResponseCookie.from("guestId").value(value).httpOnly(true).sameSite("Lax").maxAge(3600).build().toString();
    }
}

package com.nhnacademy.byeol23backend.bookset.book.interceptor;

import com.nhnacademy.byeol23backend.utils.JwtParser;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

@Component
@SuppressWarnings("squid:S3516")
@RequiredArgsConstructor
public class ViewerIdInterceptor implements HandlerInterceptor {
    private final JwtParser jwtParser;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if(!"GET".equalsIgnoreCase(request.getMethod())) return true;
        String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            if(request.getCookies() != null) {
                Arrays.stream(request.getCookies())
                        .filter(cookie -> cookie.getName().equals("guestId"))
                        .findFirst()
                        .ifPresent(cookie -> request.setAttribute("viewerId", "guest:%s".formatted(cookie.getValue())));
            }

            return true;
        }
        String token = authHeader.substring("Bearer ".length());
        Claims claims = jwtParser.parseToken(token);
        Long memberId = claims.get("memberId", Long.class);
        request.setAttribute("viewerId", "member:%d".formatted(memberId));
        return true;
    }
}
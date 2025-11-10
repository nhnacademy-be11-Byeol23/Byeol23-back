package com.nhnacademy.byeol23backend.bookset.book.interceptor;

import com.nhnacademy.byeol23backend.bookset.book.utils.JwtParser;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

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
                        .findFirst().ifPresentOrElse(
                                cookie -> Optional.ofNullable(cookie.getValue())
                                        .ifPresentOrElse(value -> request.setAttribute("viewerId", "guest:%s".formatted(value)),
                                                () -> {
                                            Cookie newCookie = createCookie();
                                            request.setAttribute("viewerId", "guest:%s".formatted(newCookie.getValue()));
                                            response.addCookie(newCookie);
                                        })
                        , () -> {
                            Cookie cookie = createCookie();
                            request.setAttribute("viewerId", "guest:%s".formatted(cookie.getValue()));
                            response.addCookie(cookie);
                        });
            }
            else {
                Cookie cookie = createCookie();
                request.setAttribute("viewerId", "guest:%s".formatted(cookie.getValue()));
                response.addCookie(cookie);
            }
            return true;
        }
        String token = authHeader.substring("Bearer ".length());
        Claims claims = jwtParser.parseToken(token);
        Long memberId = claims.get("memberId", Long.class);
        request.setAttribute("viewerId", "member:%d".formatted(memberId));
        return true;
    }

    private String generateGuestId() {
        return UUID.randomUUID().toString();
    }
    private Cookie createCookie() {
        Cookie cookie = new Cookie("guestId", generateGuestId());
        cookie.setMaxAge(3600);
        cookie.setHttpOnly(true);
        return cookie;
    }
}
package com.nhnacademy.byeol23backend.bookset.book.interceptor;

import com.nhnacademy.byeol23backend.bookset.book.utils.JwtParser;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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

        String accessToken = null;
        if(request.getCookies() != null) {
            accessToken = Arrays.stream(request.getCookies())
                    .filter(cookie -> cookie.getName().equals("Access-Token"))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }

        if(StringUtils.isNotBlank(accessToken)) {
            Claims claims = jwtParser.parseToken(accessToken);
            Long memberId = claims.get("memberId", Long.class);
            request.setAttribute("viewerId", "member:%d".formatted(memberId));
            return true;
        }

        if(request.getCookies() != null) {
            Arrays.stream(request.getCookies())
                    .filter(cookie -> cookie.getName().equals("guestId"))
                    .findFirst()
                    .ifPresent(cookie -> request.setAttribute("viewerId", "guest:%s".formatted(cookie.getValue())));
        }
        return true;
    }
}
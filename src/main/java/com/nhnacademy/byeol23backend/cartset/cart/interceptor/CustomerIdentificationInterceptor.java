package com.nhnacademy.byeol23backend.cartset.cart.interceptor;

import com.nhnacademy.byeol23backend.cartset.cart.dto.CustomerIdentifier;
import com.nhnacademy.byeol23backend.utils.JwtParser;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class CustomerIdentificationInterceptor implements HandlerInterceptor {
    private final JwtParser jwtParser;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(request.getCookies() != null) {
            String accessToken = getCookieValue(request, "Access-Token");
            if(StringUtils.isNotBlank(accessToken)) {
                Long memberId = jwtParser.parseToken(accessToken).get("memberId", Long.class);
                request.setAttribute("customerIdentifier", CustomerIdentifier.member(memberId));
                return true;
            }

            String guestId = getCookieValue(request, "guestId");
            if(StringUtils.isNotBlank(guestId)) {
                request.setAttribute("customerIdentifier", CustomerIdentifier.guest(guestId));
                return true;
            }
        }
        return true;
    }

    private String getCookieValue(HttpServletRequest request, String name) {
        if(request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(name))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}

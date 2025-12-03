package com.nhnacademy.byeol23backend.bookset.book.interceptor;

import com.nhnacademy.byeol23backend.utils.JwtParser;
import com.nhnacademy.byeol23backend.utils.MemberUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
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
public class ViewerIdInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if(!"GET".equalsIgnoreCase(request.getMethod())) return true;

        // 회원인 경우
        Long memberId = MemberUtil.getMemberId();
        if(memberId != -1L) {
            request.setAttribute("viewerId", "member:%d".formatted(memberId));
            return true;
        }



        // 비회원인 경우
        if(request.getCookies() != null) {
            String guestId = Arrays.stream(request.getCookies())
                    .filter(cookie -> cookie.getName().equals("guestId"))
                    .map(Cookie::getValue)
                    .filter(StringUtils::isNotBlank)
                    .findFirst()
                    .orElse(null);

            if(StringUtils.isNotBlank(guestId)) {
                request.setAttribute("viewerId", "guest:%s".formatted(guestId));
                return true;
            }
        }
        /*
        // 비회원이지만 첫 요청인 경우
        String guestId = (String) request.getAttribute("guestId");
        if(StringUtils.isNotBlank(guestId)) {
            request.setAttribute("viewerId", "guest:%s".formatted(guestId));
            return true;
        }
        */
        return true;
    }
}
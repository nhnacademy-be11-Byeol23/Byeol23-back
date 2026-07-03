package com.nhnacademy.byeol23backend.commons.filters;

import com.nhnacademy.byeol23backend.utils.JwtParser;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtParser jwtParser;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Bearer 토큰이 있을 때만 인증을 시도한다. (공개 자원 요청은 토큰 없이 통과)
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Claims claims = jwtParser.parseToken(token);

                Long memberId = claims.get("memberId", Long.class);
                String role = claims.get("role", String.class); // "USER", "ADMIN" 등

                if (memberId != null && role != null) {
                    var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

                    Authentication authentication =
                            new UsernamePasswordAuthenticationToken(memberId, null, authorities);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                // 토큰이 유효하지 않으면 인증을 채우지 않고 넘긴다.
                // 보호 자원이면 SecurityConfig가 EntryPoint를 통해 401을 반환한다.
                log.warn("JWT 인증 필터 - 토큰 파싱 실패: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }


}
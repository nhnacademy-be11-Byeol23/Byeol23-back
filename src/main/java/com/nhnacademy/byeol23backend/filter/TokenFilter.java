package com.nhnacademy.byeol23backend.filter;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nhnacademy.byeol23backend.memberset.member.repository.MemberRepository;
import com.nhnacademy.byeol23backend.utils.JwtParser;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenFilter extends OncePerRequestFilter {

	private final MemberRepository memberRepository;
	private final JwtParser jwtParser;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		String accessToken;
		Long memberId = -1L;
		Cookie[] cookies = request.getCookies();
		if(cookies != null) {
			for (Cookie cookie : cookies) {
				accessToken = cookie.getValue();
				try {
					memberId = jwtParser.parseToken(accessToken)
						.get("memberId", Long.class);

					log.info("토큰 기반 회원 설정: id={}", memberId);
				} catch (Exception e) {
					log.warn("액세스 토큰 파싱/회원 조회 실패");
				}
				break;
			}
		}

        if (memberId == -1L) {
		memberId = memberRepository.getReferenceById(1L).getMemberId();
		log.info("토큰 없음으로 기본 회원(1L) 사용");
	}

        request.setAttribute("memberId", memberId);

		filterChain.doFilter(request, response);
	}
}

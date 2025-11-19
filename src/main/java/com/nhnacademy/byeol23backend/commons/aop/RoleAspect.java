package com.nhnacademy.byeol23backend.commons.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.nhnacademy.byeol23backend.memberset.member.domain.Role;
import com.nhnacademy.byeol23backend.memberset.member.exception.MemberNotFoundException;
import com.nhnacademy.byeol23backend.utils.JwtParser;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RoleAspect {

	private final JwtParser jwtParser;

	@Around("@annotation(requireRole)")
	public Object roleCheck(ProceedingJoinPoint pjp, RequireRole requireRole) throws Throwable {

		ServletRequestAttributes attrs =
			(ServletRequestAttributes)RequestContextHolder.getRequestAttributes();

		if(attrs == null) {
			throw new MemberNotFoundException("요청 정보가 없습니다.");
		}

		HttpServletRequest request = attrs.getRequest();

		Cookie[] cookies = request.getCookies();

		String token = null;
		for(Cookie cookie : cookies) {
			if("Access-Token".equals(cookie.getName())) {
				token = cookie.getValue();
			}
		}

		String role = jwtParser.parseToken(token).get("role", String.class);

		Role userRole = Role.valueOf(role);
		Role requiredRole = requireRole.value();

		if(!userRole.equals(requiredRole)) {
			log.warn("권한 부족: required={}, user={}", requiredRole, userRole);
			throw new MemberNotFoundException("권한이 부족합니다.");
		}
		return pjp.proceed();
	}
}

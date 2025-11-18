package com.nhnacademy.byeol23backend.commons.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RoleAspect {

	@Before("@annotation(RequireRole)")
	public Object roleCheck(ProceedingJoinPoint pjp) throws Throwable {
		return null;
	}
}

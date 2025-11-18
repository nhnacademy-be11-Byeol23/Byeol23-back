package com.nhnacademy.byeol23backend.commons.aop;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)			 //
@Retention(RetentionPolicy.RUNTIME)  // 런타임까지 유지하여 AOP가 읽을 수 있어야 함
@Documented
public @interface RequireRole {
	String role();
}

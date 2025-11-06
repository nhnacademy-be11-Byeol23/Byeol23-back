package com.nhnacademy.byeol23backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Byeol23BackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(Byeol23BackendApplication.class, args);
	}
}
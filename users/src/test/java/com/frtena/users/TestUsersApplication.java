package com.frtena.users;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestUsersApplication {

	public static void main(String[] args) {
		SpringApplication.from(UsersApplication::main).with(TestUsersApplication.class).run(args);
	}

}

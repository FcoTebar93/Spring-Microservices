package com.frtena.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestCartApplication {

	public static void main(String[] args) {
		SpringApplication.from(CartApplication::main).with(TestCartApplication.class).run(args);
	}

}

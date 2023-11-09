package com.frtena.catalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestCatalogApplication {

	public static void main(String[] args) {
		SpringApplication.from(CatalogApplication::main).with(TestCatalogApplication.class).run(args);
	}

}

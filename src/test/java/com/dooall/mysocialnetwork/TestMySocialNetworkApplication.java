package com.dooall.mysocialnetwork;

import org.springframework.boot.SpringApplication;

public class TestMySocialNetworkApplication {

	public static void main(String[] args) {
		SpringApplication.from(MySocialNetworkApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}

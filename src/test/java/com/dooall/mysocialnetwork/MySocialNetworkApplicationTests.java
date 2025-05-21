package com.dooall.mysocialnetwork;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class MySocialNetworkApplicationTests {

	@Test
	void contextLoads() {
		System.out.println("test");
	}

}

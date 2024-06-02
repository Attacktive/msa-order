package com.github.attacktive.msaorder;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MsaOrderApplicationTests {
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void contextLoads() {
		Assertions.assertNotNull(objectMapper);
	}
}

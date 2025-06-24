package com.samir.dragons;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DragonsApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void main_runsWithoutException() {
		String[] args = {};
		DragonsApplication.main(args);
	}
}
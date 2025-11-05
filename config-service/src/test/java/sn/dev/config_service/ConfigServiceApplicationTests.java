package sn.dev.config_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ConfigServiceApplicationTests {

	@Test
	void contextLoads() {
        // This test verifies that the Spring application context loads successfully
        assertTrue(true, "Application context should load without errors");
	}

}

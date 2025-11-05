package sn.dev.api_gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ApiGatewayApplicationTests {

	@Test
	void contextLoads() {
        // This test verifies that the Spring application context loads successfully
        assertTrue(true, "Application context should load without errors");
	}

}

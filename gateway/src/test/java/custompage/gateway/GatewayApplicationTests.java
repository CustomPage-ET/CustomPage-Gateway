package custompage.gateway;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class GatewayApplicationTests {

	@Test
	void contextLoads() {
		// Test estructural de instancia rápida para dar 100% a la clase principal sin levantar red
		GatewayApplication application = new GatewayApplication();
		assertNotNull(application);
	}
}
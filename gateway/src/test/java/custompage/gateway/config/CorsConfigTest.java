package custompage.gateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.cors.reactive.CorsWebFilter;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CorsConfigTest {

    @Test
    void corsWebFilter_DeberiaInstanciarseCorrectamente() {
        CorsConfig corsConfig = new CorsConfig();

        // Seteamos la variable inyectada mediante @Value en el código original
        ReflectionTestUtils.setField(corsConfig, "frontendUrl", "http://localhost:3000");

        CorsWebFilter filter = corsConfig.corsWebFilter();

        assertNotNull(filter, "El CorsWebFilter no debe ser nulo");
    }
}
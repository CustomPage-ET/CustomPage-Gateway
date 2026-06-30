package custompage.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${urls.frontend}")
    private String frontendUrl;

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        // Permite explícitamente el origen de tu Frontend dinámico
        corsConfig.setAllowedOrigins(List.of(frontendUrl));

        // Métodos HTTP permitidos para la gestión de cosméticos y stock
        corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Cabeceras permitidas (imprescindible 'Authorization' para el Token JWT)
        corsConfig.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "X-Requested-With"));

        // Permite el envío de credenciales/cookies si fuera necesario en el futuro
        corsConfig.setAllowCredentials(true);

        // Tiempo en que el navegador cachea esta configuración pre-vuelo (1 hora)
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}
package custompage.gateway.config;

import custompage.gateway.security.AuthenticationManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Test
    void securityWebFilterChain_DeberiaConstruirseCorrectamente() {
        SecurityConfig securityConfig = new SecurityConfig(authenticationManager);
        ServerHttpSecurity http = ServerHttpSecurity.http(); // Instancia base limpia para testear

        SecurityWebFilterChain filterChain = securityConfig.securityWebFilterChain(http);

        assertNotNull(filterChain, "La cadena de filtros de seguridad reactiva debe crearse con éxito");
    }
}
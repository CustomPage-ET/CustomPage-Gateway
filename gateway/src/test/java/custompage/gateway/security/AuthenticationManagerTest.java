package custompage.gateway.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationManagerTest {

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthenticationManager authenticationManager;

    @Test
    void authenticate_TokenValido_RetornaAuthentication() {
        String token = "mockTokenValido";
        Authentication authRequest = new UsernamePasswordAuthenticationToken(token, token);

        when(jwtUtils.validarExpiracionToken(token)).thenReturn(false);
        when(jwtUtils.extraerUsername(token)).thenReturn("adminUser");
        when(jwtUtils.extraerRoles(token)).thenReturn(List.of("ROLE_ADMIN"));

        Mono<Authentication> resultado = authenticationManager.authenticate(authRequest);

        StepVerifier.create(resultado)
                .expectNextMatches(auth -> {
                    return auth.getName().equals("adminUser") &&
                            auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                })
                .verifyComplete();
    }

    @Test
    void authenticate_TokenExpirado_RetornaMonoEmpty() {
        String token = "mockTokenExpirado";
        Authentication authRequest = new UsernamePasswordAuthenticationToken(token, token);

        when(jwtUtils.validarExpiracionToken(token)).thenReturn(true);

        Mono<Authentication> resultado = authenticationManager.authenticate(authRequest);

        StepVerifier.create(resultado)
                .verifyComplete(); // El flujo reactivo termina vacío (Mono.empty())
    }

    @Test
    void authenticate_ExcepcionInterna_RetornaMonoEmpty() {
        String token = "tokenInvalido";
        Authentication authRequest = new UsernamePasswordAuthenticationToken(token, token);

        // Simulamos una excepción (por ejemplo, firma corrupta) para entrar al bloque catch
        when(jwtUtils.validarExpiracionToken(token)).thenThrow(new RuntimeException("Firma Inválida"));

        Mono<Authentication> resultado = authenticationManager.authenticate(authRequest);

        StepVerifier.create(resultado)
                .verifyComplete(); // El catch devuelve Mono.empty(), por lo que se completa limpio
    }
}
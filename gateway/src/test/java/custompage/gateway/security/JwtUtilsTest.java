package custompage.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.ExpiredJwtException; // 👈 Importamos la excepción de JJWT
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;
    private String secret = "MiClaveSuperSecretaDe32CaracteresMinimoParaElTokenJWT123!";
    private String tokenValido;
    private String tokenExpirado;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils(secret);
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        // Generamos un Token Válido (expira en 1 hora)
        tokenValido = Jwts.builder()
                .subject("usuarioTest")
                .claim("roles", List.of("ROLE_USER", "ROLE_ADMIN"))
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(key)
                .compact();

        // Generamos un Token Expirado (caducó hace 1 hora)
        tokenExpirado = Jwts.builder()
                .subject("usuarioTest")
                .claim("roles", List.of("ROLE_USER"))
                .expiration(new Date(System.currentTimeMillis() - 3600000))
                .signWith(key)
                .compact();
    }

    @Test
    void testExtraerUsername() {
        String username = jwtUtils.extraerUsername(tokenValido);
        assertEquals("usuarioTest", username);
    }

    @Test
    void testExtraerRoles() {
        List<String> roles = jwtUtils.extraerRoles(tokenValido);
        assertNotNull(roles);
        assertEquals(2, roles.size());
        assertTrue(roles.contains("ROLE_ADMIN"));
    }

    @Test
    void testValidarExpiracionToken_NoExpirado() {
        Boolean esExpirado = jwtUtils.validarExpiracionToken(tokenValido);
        assertFalse(esExpirado);
    }

    @Test
    void testValidarExpiracionToken_Expirado() {
        // 👈 Corregido: Validamos que lanzar un token caducado dispare la excepción esperada
        assertThrows(ExpiredJwtException.class, () -> {
            jwtUtils.validarExpiracionToken(tokenExpirado);
        });
    }
}
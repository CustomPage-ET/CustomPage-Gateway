package custompage.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {

    private final SecretKey key;

    public JwtUtils(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Claims extraerTodosLosClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extraerUsername(String token) {
        return extraerTodosLosClaims(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> extraerRoles(String token) {
        // Extraemos la lista de roles que inyectó el microservicio auth
        return extraerTodosLosClaims(token).get("roles", List.class);
    }

    public Boolean validarExpiracionToken(String token) {
        return extraerTodosLosClaims(token).getExpiration().before(new Date());
    }
}
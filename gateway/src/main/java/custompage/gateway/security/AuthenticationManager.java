package custompage.gateway.security;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtUtils jwtUtils;

    public AuthenticationManager(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String tokenAuth = authentication.getCredentials().toString();

        try {
            if (jwtUtils.validarExpiracionToken(tokenAuth)) {
                return Mono.empty();
            }

            String username = jwtUtils.extraerUsername(tokenAuth);
            List<String> roles = jwtUtils.extraerRoles(tokenAuth);

            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            return Mono.just(new UsernamePasswordAuthenticationToken(username, null, authorities));

        } catch (Exception e) {
            return Mono.empty();
        }
    }
}
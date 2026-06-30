package custompage.gateway.config;

import custompage.gateway.security.AuthenticationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final AuthenticationManager authenticationManager;

    public SecurityConfig(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .authenticationManager(authenticationManager)
                .securityContextRepository(new ServerSecurityContextRepository() {
                    @Override
                    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
                        return Mono.empty();
                    }

                    @Override
                    public Mono<SecurityContext> load(ServerWebExchange exchange) {
                        ServerHttpRequest request = exchange.getRequest();
                        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

                        if (authHeader != null && authHeader.startsWith("Bearer ")) {
                            String authToken = authHeader.substring(7);
                            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(authToken, authToken);
                            return authenticationManager.authenticate(auth)
                                    .map(SecurityContextImpl::new);
                        }
                        return Mono.empty();
                    }
                })
                .authorizeExchange(exchanges -> exchanges
                        // RUTA PÚBLICA: El login y registro pasan directo sin Token
                        .pathMatchers("/api/bff/auth/**").permitAll()
                        // RUTA RESTRINGIDA: Acciones de inventario (BffProductoController) requieren rol ADMIN
                        .pathMatchers("/api/bff/productos/**").hasRole("ADMIN")
                        // CUALQUIER OTRA RUTA: Exige que el usuario esté autenticado legítimamente
                        .anyExchange().authenticated()
                )
                .exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec
                        .authenticationEntryPoint((exchange, e) -> Mono.fromRunnable(() ->
                                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)))
                        .accessDeniedHandler((exchange, e) -> Mono.fromRunnable(() ->
                                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN)))
                )
                .build();
    }
}
package com.frtena.apigateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class JwTokenValidatorFilter extends AbstractGatewayFilterFactory<JwTokenValidatorFilter.Config> {

    public JwTokenValidatorFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // Lógica de validación del token JWT aquí
            String token = exchange.getRequest().getHeaders().getFirst("Authorization");

            // Ejemplo: Validar el token (deberías implementar esta lógica según tus necesidades)
            if (token == null || !token.startsWith("Bearer ")) {
                exchange.getResponse().setComplete();
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return Mono.empty();
            }

            String jwt = token.substring(7); // Elimina "Bearer " del token

            try {
                // Ejemplo: Validar el token utilizando una clave secreta (deberías tener tu propia lógica aquí)
                String secretKey = config.getSecretKey(); // Obtén la clave desde la configuración
                Jws<Claims> claims = Jwts.parserBuilder()
                        .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                        .build()
                        .parseClaimsJws(jwt);

                // Continuar con la cadena de filtros si el token es válido
                return chain.filter(exchange);
            } catch (Exception e) {
                // Manejar la excepción (por ejemplo, token inválido)
                exchange.getResponse().setComplete();
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return Mono.empty();
            }
        };
    }

    public static class Config extends AbstractGatewayFilterFactory.NameConfig {
        private String secretKey;

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }
    }
}

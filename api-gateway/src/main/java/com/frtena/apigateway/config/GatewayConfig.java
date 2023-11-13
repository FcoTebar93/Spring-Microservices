package com.frtena.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public JwTokenValidatorFilter.Config jwTokenValidatorConfig() {
        JwTokenValidatorFilter.Config config = new JwTokenValidatorFilter.Config();
        config.setSecretKey("j3sJ891=================================================================");
        return config;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, JwTokenValidatorFilter jwTokenValidatorFilter) {
        return builder.routes()
                .route("ecommerce-users", r -> r
                        .path("/ecommerce-users/**")
                        .filters(f -> f.filter(jwTokenValidatorFilter.apply(new JwTokenValidatorFilter.Config())))
                        .uri("http://localhost:8093")
                )
                .route("catalog", r -> r
                        .path("/catalog/**")
                        .filters(f -> f.filter(jwTokenValidatorFilter.apply(new JwTokenValidatorFilter.Config())))
                        .uri("http://localhost:8098")
                )
                .route("cart", r -> r
                        .path("/cart/**")
                        .filters(f -> f.filter(jwTokenValidatorFilter.apply(new JwTokenValidatorFilter.Config())))
                        .uri("http://localhost:8095")
                )
                .build();
    }
}

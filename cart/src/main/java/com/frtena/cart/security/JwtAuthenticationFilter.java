package com.frtena.cart.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-in-ms}")
    private int jwtExpirationInMs;

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String HEADER_STRING = "Authorization";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // Obtén el token del encabezado de la solicitud
            String token = extractToken(request);

            // Validar el token
            if (token != null && validateToken(token)) {
                // Si el token es válido, establecer el contexto de seguridad
                Claims claims = parseToken(token);
                String username = claims.getSubject();

                if (username != null) {
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null, null);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        } catch (ExpiredJwtException e) {
            System.out.println("Token expired: "+e.getMessage());
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(HEADER_STRING);

        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            return header.replace(TOKEN_PREFIX, "");
        }

        return null;
    }

    private boolean validateToken(String token) {
        try {
            // Parsea el token y verifica la firma
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("Token expired: "+e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("An error has ocurred: "+e.getMessage());
            return false;
        }
    }

    private Claims parseToken(String token) {
        // Utiliza la biblioteca io.jsonwebtoken para analizar el token y obtener las reclamaciones (claims)
        // Aquí, solo estamos obteniendo el subject, pero puedes extraer otras reclamaciones según sea necesario
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
    }
}



package com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.models.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    @Value("${jwtSecret}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(@SuppressWarnings("null") HttpServletRequest request,
            @SuppressWarnings("null") HttpServletResponse response, @SuppressWarnings("null") FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractTokenFromRequest(request);

        if (token != null) {
            try {

                @SuppressWarnings("deprecation")
                Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
                User userId = claims.get("id", User.class);

                request.setAttribute("id", userId);
            } catch (Exception e) {

                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {

        String headerValue = request.getHeader("Authorization");

        if (headerValue != null && headerValue.startsWith("Bearer ")) {

            return headerValue.substring(7);
        }

        return null;
    }
}
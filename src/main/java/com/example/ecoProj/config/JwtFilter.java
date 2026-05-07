package com.example.ecoProj.config;

import com.example.ecoProj.Service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LogManager.getLogger(JwtFilter.class);

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Skip public endpoints
        if (path.equals("/login") || path.equals("/register")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        try {
            // Step 1: Extract JWT
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                username = jwtService.extractUserName(token);

                logger.info("JWT found for user: {}", username);
            }

            // Step 2: Validate & authenticate
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                if (jwtService.validateToken(token, username)) {

                    //  Extract roles + permissions from JWT
                    List<String> roles = jwtService.extractRoles(token);
                    List<String> permissions = jwtService.extractPermissions(token);

                    //  Combine into authorities
                    List<SimpleGrantedAuthority> authorities = new ArrayList<>();

                    if (roles != null) {
                        roles.forEach(role ->
                                authorities.add(new SimpleGrantedAuthority(role)));
                    }

                    if (permissions != null) {
                        permissions.forEach(perm ->
                                authorities.add(new SimpleGrantedAuthority(perm)));
                    }

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    username,
                                    null,
                                    authorities
                            );

                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    logger.info("User authenticated via JWT: {}", username);
                } else {
                    logger.warn("Invalid JWT token for user: {}", username);
                }
            }

            // Step 3: Authorization
            if (SecurityContextHolder.getContext().getAuthentication() != null) {

                String method = request.getMethod();
                String reqPath = request.getRequestURI();

                logger.info("Authorities: {}",
                        SecurityContextHolder.getContext().getAuthentication().getAuthorities());

                logger.info("Request: {} {}", method, reqPath);

                boolean hasPermission = SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getAuthorities()
                        .stream()
                        .anyMatch(auth -> {

                            String authority = auth.getAuthority();

                            //  Ignore roles
                            if (!authority.contains(":")) return false;

                            String[] parts = authority.split(":", 2);
                            String permMethod = parts[0];
                            String permPath = parts[1];

                            // Method match
                            if (!permMethod.equals(method)) return false;

                            // Exact match
                            if (permPath.equals(reqPath)) return true;

                            // Wildcard match
                            if (permPath.endsWith("/**")) {
                                String basePath = permPath.substring(0, permPath.length() - 3);
                                return reqPath.startsWith(basePath);
                            }

                            return false;
                        });

                if (!hasPermission) {
                    logger.warn("Access denied for user: {} on {}:{}", username, method, reqPath);

                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("Forbidden: You don't have permission");
                    return;
                }
            }

        } catch (Exception e) {
            logger.error("JWT processing failed", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid JWT");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
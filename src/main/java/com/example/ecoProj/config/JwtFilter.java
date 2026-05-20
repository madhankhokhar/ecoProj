package com.example.ecoProj.config;

import com.example.ecoProj.Service.JwtService;
import com.example.ecoProj.exception.ForbiddenException;
import com.example.ecoProj.exception.UnauthorizedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger =
            LogManager.getLogger(JwtFilter.class);

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // PUBLIC ENDPOINTS
        if (path.startsWith("/auth")) {

            filterChain.doFilter(request, response);

            return;
        }

        final String authHeader =
                request.getHeader("Authorization");

        String token = null;
        String username = null;

        Set<String> exactPermissions =
                new HashSet<>();

        List<String> wildcardPermissions =
                new ArrayList<>();

        try {

            // EXTRACT JWT

            if (authHeader != null &&
                    authHeader.startsWith("Bearer ")) {

                token = authHeader.substring(7);

                username =
                        jwtService.extractUserName(token);

                logger.info(
                        "JWT found for user: {}",
                        username
                );
            }

            // AUTHENTICATION

            if (username != null &&
                    SecurityContextHolder.getContext()
                            .getAuthentication() == null) {

                if (jwtService.validateToken(
                        token,
                        username
                )) {

                    List<String> roles =
                            jwtService.extractRoles(token);

                    List<String> permissions =
                            jwtService.extractPermissions(token);

                    List<SimpleGrantedAuthority> authorities =
                            new ArrayList<>();

                    // ROLES

                    if (roles != null) {

                        for (String role : roles) {

                            authorities.add(
                                    new SimpleGrantedAuthority(role)
                            );
                        }
                    }

                    // PERMISSIONS

                    if (permissions != null) {

                        for (String permission : permissions) {

                            authorities.add(
                                    new SimpleGrantedAuthority(permission)
                            );

                            // Separate exact & wildcard permissions

                            if (permission.endsWith("/**")) {

                                wildcardPermissions.add(permission);

                            } else {

                                exactPermissions.add(permission);
                            }
                        }
                    }

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    username,
                                    null,
                                    authorities
                            );

                    SecurityContextHolder.getContext()
                            .setAuthentication(authToken);

                    logger.info(
                            "User authenticated via JWT: {}",
                            username
                    );

                } else {

                    logger.warn(
                            "Invalid JWT token for user: {}",
                            username
                    );

                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");

                    response.getWriter().write("""
                    {
                        "status": 401,
                        "error": "UNAUTHORIZED",
                        "message": "Invalid JWT token"
                    }
                    """);

                    return;
                }
            }

            // UNAUTHORIZED

            if (SecurityContextHolder.getContext()
                    .getAuthentication() == null) {

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");

                response.getWriter().write("""
                {
                    "status": 401,
                    "error": "UNAUTHORIZED",
                    "message": "Unauthorized access"
                }
                """);

                return;
            }

            // AUTHORIZATION

            String method =
                    request.getMethod();

            String reqPath =
                    request.getRequestURI();

            String requestKey =
                    method + ":" + reqPath;

            logger.info(
                    "Request: {} {}",
                    method,
                    reqPath
            );

            logger.info(
                    "Authorities: {}",
                    SecurityContextHolder.getContext()
                            .getAuthentication()
                            .getAuthorities()
            );

            // EXACT MATCH

            boolean hasPermission =
                    exactPermissions.contains(requestKey);

            // WILDCARD MATCH

            if (!hasPermission) {

                for (String permission : wildcardPermissions) {

                    int idx =
                            permission.indexOf(':');

                    if (idx == -1) {
                        continue;
                    }

                    String permMethod =
                            permission.substring(0, idx);

                    // Method mismatch

                    if (!permMethod.equals(method)) {
                        continue;
                    }

                    String permPath =
                            permission.substring(idx + 1);

                    String basePath =
                            permPath.substring(
                                    0,
                                    permPath.length() - 3
                            );

                    if (reqPath.startsWith(basePath)) {

                        hasPermission = true;

                        break;
                    }
                }
            }

            // ACCESS DENIED

            if (!hasPermission) {

                String currentUser =
                        SecurityContextHolder.getContext()
                                .getAuthentication()
                                .getName();

                logger.warn(
                        "Access denied for user: {} on {}:{}",
                        currentUser,
                        method,
                        reqPath
                );

                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");

                response.getWriter().write("""
                {
                    "status": 403,
                    "error": "FORBIDDEN",
                    "message": "You don't have permission to access this resource"
                }
                """);

                return;
            }

        } catch (Exception e) {

            logger.error(
                    "JWT processing failed",
                    e
            );

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");

            response.getWriter().write("""
            {
                "status": 401,
                "error": "UNAUTHORIZED",
                "message": "JWT processing failed"
            }
            """);
        }

        filterChain.doFilter(request, response);
    }
}
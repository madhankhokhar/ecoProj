package com.example.ecoProj.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class JwtService {

    private static final Logger logger = LogManager.getLogger(JwtService.class);

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    // UPDATED: now accepts permissions
    public String generateToken(String username, List<String> roles, List<String> permissions) {
        logger.info("Generating JWT token for user: {}", username);

        try {
            String token = Jwts.builder()
                    .subject(username)
                    .claim("roles", roles)
                    .claim("permissions", permissions) //  FIXED
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                    .signWith(getKey())
                    .compact();

            return token;

        } catch (Exception e) {
            logger.error("Error generating JWT token for user: {}", username, e);
            throw e;
        }
    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // ✅ Extract username
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // ✅ NEW: Extract permissions from JWT
    public List<String> extractPermissions(String token) {
        return extractClaim(token, claims -> claims.get("permissions", List.class));
    }

    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> claims.get("roles", List.class));
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        final Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    // ✅ Extract all claims
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            logger.error("Error extracting claims from token", e);
            throw e;
        }
    }

    // ✅ Validate token
    public boolean validateToken(String token, String username) {
        try {
            final String userName = extractUserName(token);
            return userName.equals(username) && !isTokenExpired(token);
        } catch (Exception e) {
            logger.error("Token validation failed for user: {}", username, e);
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
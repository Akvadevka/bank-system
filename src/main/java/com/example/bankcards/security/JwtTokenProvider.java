package com.example.bankcards.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration.ms}")
    private int jwtExpirationMs;

    @Value("${jwt.refresh.expiration.ms}")
    private int jwtRefreshExpirationMs;

    private Key key() {
        byte[] decodedKey = Decoders.BASE64.decode(jwtSecret);
        System.err.println("JWT DEBUG: Key length (bits): " + (decodedKey.length * 8));
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    private String doGenerateToken(Authentication authentication, int expirationMs) {
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(Authentication authentication) {
        return doGenerateToken(authentication, jwtExpirationMs);
    }

    public String generateRefreshToken(Authentication authentication) {
        return doGenerateToken(authentication, jwtRefreshExpirationMs);
    }

    public String getUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parse(token);
            return true;
        } catch (MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            System.err.println("JWT validation error: " + e.getMessage());
        }
        return false;
    }
}
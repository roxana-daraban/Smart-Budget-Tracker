package com.budget.backend.service;

import com.budget.backend.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JwtService - Serviciu pentru generare și validare JWT token-uri
 *
 * Versiunea JWT: 0.12.3
 * API corect: Jwts.parser() (nu parserBuilder())
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration:86400000}")
    private Long expiration;

    /**
     * Generează un JWT token pentru un utilizator
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole().name());

        return Jwts.builder()
                .claims(claims)  // În 0.12.3 folosim .claims() nu .setClaims()
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Validează un JWT token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()  // În 0.12.3 folosim parser() nu parserBuilder()
                    .verifyWith(getSigningKey())  // verifyWith() nu setSigningKey()
                    .build()
                    .parseSignedClaims(token)  // parseSignedClaims() nu parseClaimsJws()
                    .getPayload();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extrage username-ul din token
     */
    public String getUsernameFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrage un claim specific din token
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrage toate claim-urile din token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()  // parser() nu parserBuilder()
                .verifyWith(getSigningKey())  // verifyWith() nu setSigningKey()
                .build()
                .parseSignedClaims(token)  // parseSignedClaims() nu parseClaimsJws()
                .getPayload();  // getPayload() nu getBody()
    }

    /**
     * Creează cheia de semnare din secret
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
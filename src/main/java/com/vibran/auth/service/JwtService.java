package com.vibran.auth.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Getter
    @Value("${jwt.access-token-expiry-ms}")
    private long accessTokenExpiryMs;

    // ── Token Generation ────────────────────────────────────

    public String generateAccessToken(UserDetails userDetails,
                                      Long userId,
                                      String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role",   role);
        return buildToken(claims, userDetails.getUsername(), accessTokenExpiryMs);
    }

    private String buildToken(Map<String, Object> extraClaims,
                              String subject,
                              long expiryMs) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiryMs))
                .signWith(getSigningKey())
                .compact();
    }

    // ── Token Extraction ──────────────────────────────────────

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Long extractUserId(String token) {
        return extractClaim(token, claims ->
                claims.get("userId", Long.class));
    }

    public String extractRole(String token) {
        return extractClaim(token, claims ->
                claims.get("role", String.class));
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token,
                              Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    //Token Validation

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public boolean isTokenValid(String token, UserDetails userDetails, Claims claims) {
        final String email = claims.getSubject();
        return email.equals(userDetails.getUsername()) && !claims.getExpiration().before(new Date());
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Internals

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        String encodedSecret = secretKey == null ? "" : secretKey.trim();
        if (encodedSecret.isEmpty()) {
            throw new IllegalStateException("JWT secret is not configured");
        }

        byte[] keyBytes = decodeSigningSecret(encodedSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private byte[] decodeSigningSecret(String encodedSecret) {
        try {
            if (encodedSecret.contains("-") || encodedSecret.contains("_")) {
                return Decoders.BASE64URL.decode(encodedSecret);
            }
            return Decoders.BASE64.decode(encodedSecret);
        } catch (DecodingException ex) {
            throw new IllegalStateException(
                    "JWT secret must be Base64 or Base64URL encoded and at least 256 bits for HS256",
                    ex
            );
        }
    }
    @PostConstruct
    public void debugExpiry() {
        System.out.println("🔥 JWT ACCESS EXPIRY MS = " + accessTokenExpiryMs);
    }
}

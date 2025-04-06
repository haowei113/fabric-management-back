package com.example.fabricmanagement.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Collections;
import java.util.List;

@Service
public class JwtService {

    private final Key key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);

    public String generateToken(String username, String userType) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userType", userType)
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Authentication getAuthentication(String token, HttpServletRequest request) {
        try {
            String username = extractUsername(token);
            Claims claims = getClaims(token);
            String userType = claims.get("userType", String.class);

            if (username != null && userType != null) {
                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(userType));
                return new UsernamePasswordAuthenticationToken(username, null, authorities);
            }
        } catch (Exception e) {
            System.out.println("Token validation failed: " + e.getMessage());
        }
        return null;
    }
}
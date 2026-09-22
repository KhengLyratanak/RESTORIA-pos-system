package com.nak.demo.service.security;

import com.nak.demo.common.config.ApplicationConfiguration;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Component
public class JwtUtil {

    @Autowired
    private ApplicationConfiguration appConfig;

    private long expiration;
    private String secret;
    @PostConstruct
    private void init(){
        this.secret = appConfig.getSecurity().getSecret();
        this.expiration = appConfig.getSecurity().getExpiration();
    }


    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(this.secret.getBytes());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String,Object> claims = new HashMap<>();

        return this.createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String,Object> claims, String subject) {
        System.out.println("Key: " + this.getSigningKey());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + this.expiration))
                .signWith(this.getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    public Boolean validateToken(String token, UserDetails userDetails) {
        Boolean isExpired = this.extractExpiration(token).before(new Date());
        String username = this.extractUsername(token);

        return !isExpired && username.equals(userDetails.getUsername());
    }

    public Date extractExpiration(String token) {
        return this.extractClaim(token,Claims::getExpiration);
    }

    public String extractUsername(String token) {
        return this.extractClaim(token,Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims,T> claimResolver) {
        Claims claims = this.extractAllClaims(token);

        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(this.getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
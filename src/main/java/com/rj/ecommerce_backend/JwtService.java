package com.rj.ecommerce_backend;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

//@PropertySource(name = "myProperties", value = "values.properties")
public class JwtService {
    //    @Value("${jwt.secret.key}")
    private static final String SECRET_KEY = "GBVn42k75m08G1G0m19ELQGFPSaGzE0J3Gzck4t1X/NDf4BU6e8qtSUdc8Hl7b7bpDHR2MELvpFFmW8iGPsPCA==";
    private static final int EXPIRATION_TIME = 900000;

    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims extractClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractUserName(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public boolean isTokenValidAndNotExpired(String token, UserDetails userDetails) {
        String name = extractUserName(token);

        if (!name.equals(userDetails.getUsername())) {
            return false;
        }
        return !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return getClaim(token, Claims::getExpiration);
    }

}

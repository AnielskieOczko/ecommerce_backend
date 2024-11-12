package com.rj.ecommerce_backend.securityconfig;

import com.rj.ecommerce_backend.domain.user.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtUtils {

    private final JwtConfig jwtConfig;

    private String getJwtSecret() {
        return jwtConfig.getSecret();
    }

    private int getJwtExpirationMs() {
        return jwtConfig.getExpirationMs();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(getJwtSecret()));
    }

    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        log.info(Integer.toString(getJwtExpirationMs()));
        log.info(getJwtSecret());

        return Jwts.builder()
                .setSubject(String.valueOf(userPrincipal.getId())) // User ID as subject
                .claim("username", userPrincipal.getUsername()) // Username as a separate claim
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + 86400000)) // TODO: change hardcoded value to env variable (getJwtExpiationMs does return 0 - issue to fix)
                .signWith(key(), SignatureAlgorithm.HS512)
                .compact();
    }

    public Claims getTokenClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUsernameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("username", String.class);
    }

    public String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (SecurityException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}

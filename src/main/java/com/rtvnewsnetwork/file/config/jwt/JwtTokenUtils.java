package com.rtvnewsnetwork.file.config.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenUtils {

    @Value("${rtv.jwt.lifetime}")
    private long jwtLifeDuration;


    @Value("${rtv.jwt.secret}")
    private String secretKey;

    @Value("${rtv.refreshToken.secret}")
    private String refreshTokenSecretKey;

    public String generateToken() {
        return Jwts.builder()
                .subject("Kskfjskfaksfjksdf")
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plusSeconds(jwtLifeDuration)))
                .signWith(getKey())
                .compact();
    }


    public SecretKey getKey() {
        return new SecretKeySpec(
                Base64.getDecoder().decode(secretKey),
                "HmacSHA256"
        );
    }

    private SecretKey getRefreshTokenKey() {
        return new SecretKeySpec(
                Base64.getDecoder().decode(refreshTokenSecretKey),
                "HmacSHA256"
        );
    }

    public Jws<Claims> parse(String jwtString) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(jwtString);
    }

    public Jws<Claims> parseRefreshToken(String jwtString) {
        return Jwts.parser()
                .verifyWith(getRefreshTokenKey())
                .build()
                .parseSignedClaims(jwtString);
    }
}

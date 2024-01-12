package com.hajj.hajj.config;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

@Component
public class JWTUtil {
    @Value("${jwt_secret}")
    private String secret;

    public String generateToken(String username) throws IllegalArgumentException, JWTCreationException {
        Instant expirationTime = Instant.now().plus(Duration.ofMinutes(30));
        return JWT.create()
                .withSubject("Login Credential")
                .withClaim("username", username)
                .withIssuedAt(new Date())
                .withExpiresAt(Date.from(expirationTime))
                .withIssuer("Hajj User Management")
                .sign(Algorithm.HMAC512(secret));
    }

    public String validateTokenAndRetrieveSubject(String token)throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                .withSubject("Login Credential")
                .withIssuer("Hajj User Management")
                .build();
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getClaim("username").asString();
    }
}

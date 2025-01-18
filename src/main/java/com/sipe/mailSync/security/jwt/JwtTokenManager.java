package com.sipe.mailSync.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenManager {
    private final JwtProperties jwtProperties;

    public String generateToken(String email, String userRole) {
        return JWT.create()
                .withSubject(email)
                .withIssuer(jwtProperties.getAccessToken().getIssuer())
                .withClaim("role", userRole)
                .withIssuedAt(new Date())
                .withExpiresAt(
                        new Date(System.currentTimeMillis() + jwtProperties.getAccessToken().getExpirationMinute() * 60 * 1000))
                .sign(Algorithm.HMAC256(jwtProperties.getAccessToken().getSecretKey().getBytes()));
    }

    public String getEmailFromToken(String token) {
        final DecodedJWT decodedJWT = getDecodedJWT(token);
        return decodedJWT.getSubject();
    }

    public boolean validateToken(String token, String authenticatedEmail) {
        final String emailFromToken = getEmailFromToken(token);

        final boolean emailVerified = emailFromToken.equals(authenticatedEmail);
        final boolean tokenExpired = isTokenExpired(token);

        return emailVerified && !tokenExpired;
    }

    private boolean isTokenExpired(String token) {
        final Date expirationDateFromToken = getExpirationDateFromToken(token);
        return expirationDateFromToken.before(new Date());
    }

    private Date getExpirationDateFromToken(String token) {
        final DecodedJWT decodedJWT = getDecodedJWT(token);
        return decodedJWT.getExpiresAt();
    }

    private DecodedJWT getDecodedJWT(String token) {

        final JWTVerifier jwtVerifier =
                JWT.require(Algorithm.HMAC256(jwtProperties.getAccessToken().getSecretKey().getBytes())).build();

        return jwtVerifier.verify(token);
    }
}

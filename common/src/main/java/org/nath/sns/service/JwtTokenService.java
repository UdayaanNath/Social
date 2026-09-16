package org.nath.sns.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class JwtTokenService {

    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final long expirationMs;
    private final String issuer = "identity-service";

    public JwtTokenService(RSAPrivateKey privateKey, RSAPublicKey publicKey, long expirationMs) {
        // Algorithm.RSA256 binds the private key for signing and public key for verification
        this.algorithm = Algorithm.RSA256(publicKey, privateKey);
        this.verifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .build();
        this.expirationMs = expirationMs;
    }

    public String generateToken(Long userId, String username, List<String> roles) {
        Instant now = Instant.now();
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(String.valueOf(userId))
                .withClaim("username", username)
                .withClaim("roles", roles)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plusMillis(expirationMs)))
                .sign(algorithm);
    }

    public Optional<DecodedJWT> verifyToken(String token) {
        try {
            return Optional.of(verifier.verify(token));
        } catch (JWTVerificationException e) {
            return Optional.empty(); // Signature failure, expired, or invalid claims
        }
    }
}
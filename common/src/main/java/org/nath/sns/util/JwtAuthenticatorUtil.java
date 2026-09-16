package org.nath.sns.util;

import org.nath.sns.dto.AuthenticatedUser;
import org.nath.sns.service.JwtTokenService;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class JwtAuthenticatorUtil implements Authenticator<String, AuthenticatedUser> {

    private final JwtTokenService tokenService;

    public JwtAuthenticatorUtil(JwtTokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public Optional<AuthenticatedUser> authenticate(String token) throws AuthenticationException {
        Optional<DecodedJWT> jwtOpt = tokenService.verifyToken(token);

        if (jwtOpt.isEmpty()) {
            return Optional.empty(); // 401 Unauthorized
        }

        DecodedJWT jwt = jwtOpt.get();
        Long userId = Long.parseLong(jwt.getSubject());
        String username = jwt.getClaim("username").asString();
        List<String> roles = jwt.getClaim("roles").asList(String.class);

        return Optional.of(new AuthenticatedUser(
                userId,
                username,
                roles != null ? roles : Collections.emptyList()
        ));
    }
}

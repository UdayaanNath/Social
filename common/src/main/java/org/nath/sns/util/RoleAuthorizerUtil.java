package org.nath.sns.util;

import io.dropwizard.auth.Authorizer;
import jakarta.ws.rs.container.ContainerRequestContext;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.nath.sns.dto.AuthenticatedUser;

public class RoleAuthorizerUtil implements Authorizer<AuthenticatedUser> {
    @Override
    public boolean authorize(AuthenticatedUser user, String role, @Nullable ContainerRequestContext context) {
        return user != null && user.hasRole(role);
    }
}
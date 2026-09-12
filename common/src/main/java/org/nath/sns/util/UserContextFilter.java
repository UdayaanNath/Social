package org.nath.sns.util;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import org.nath.sns.dto.AuthenticatedUser;

import java.io.IOException;
import java.security.Principal;

@Priority(Priorities.AUTHENTICATION + 1) // Runs immediately after Dropwizard AuthFilter
public class UserContextFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final ThreadLocal<AuthenticatedUser> CURRENT_USER = new ThreadLocal<>();

    public static AuthenticatedUser getCurrentUser() {
        return CURRENT_USER.get();
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (requestContext.getSecurityContext() != null) {
            Principal principal = requestContext.getSecurityContext().getUserPrincipal();
            if (principal instanceof AuthenticatedUser) {
                CURRENT_USER.set((AuthenticatedUser) principal);
            }
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        // Critical: Clean up thread local to avoid memory leaks across worker threads
        CURRENT_USER.remove();
    }
}

package org.nath.sns.resource;

import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.nath.sns.entity.UserEntity;
import org.nath.sns.identity.AuthApi;
import org.nath.sns.identity.model.LoginRequest;
import org.nath.sns.identity.model.LoginResponse;
import org.nath.sns.service.AuthenticationService;

import java.util.Optional;

public class AuthResource implements AuthApi {

    private AuthenticationService authenticationService;

    @Inject
    public AuthResource(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    @UnitOfWork
    public LoginResponse login(LoginRequest loginRequest) {
        try {
            Optional<UserEntity> user = authenticationService.validatCredentials(loginRequest);
            return authenticationService.generateToken(user.get());
        } catch (IllegalArgumentException e) {
            throw notAuthorized(e);
        } catch (Exception e) {
            throw internalServerError(e);
        }
    }

    private NotAuthorizedException notAuthorized(IllegalArgumentException e) {
        return new NotAuthorizedException(Response.status(Response.Status.UNAUTHORIZED)
                .entity(new org.nath.sns.identity.model.Error()
                        .code(Response.Status.UNAUTHORIZED.getStatusCode())
                        .message(e.getMessage()))
                .build());
    }

    private WebApplicationException internalServerError(Exception e) {
        return new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new org.nath.sns.identity.model.Error()
                        .code(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                        .message("Internal server error: " + e.getMessage()))
                .build());

    }
}
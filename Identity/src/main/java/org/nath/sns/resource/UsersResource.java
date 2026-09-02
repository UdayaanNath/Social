package org.nath.sns.resource;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import org.nath.sns.dto.AuthenticatedUser;
import org.nath.sns.identity.UsersApi;
import org.nath.sns.identity.model.CreateUserRequest;
import org.nath.sns.identity.model.SuccessResponse;
import org.nath.sns.identity.model.UpdateUserRequest;
import org.nath.sns.identity.model.User;
import org.nath.sns.service.UserService;

import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.nath.sns.util.UserValidationUtil;

import java.util.List;

@Singleton
public class UsersResource implements UsersApi {

    @Context
    private SecurityContext securityContext;
    private final UserService userService;

    @Inject
    public UsersResource(UserService userService) {
        this.userService = userService;
    }

    @Override
    @UnitOfWork
    public User createUser(CreateUserRequest createUserRequest) {
        try {
            return userService.createUser(createUserRequest);
        } catch (Exception e) {
            throw new WebApplicationException(
                    Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(new org.nath.sns.identity.model.Error()
                                    .code(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                                    .message("Internal server error: " + e.getMessage()))
                            .build());
        }
    }

    @Override
    @UnitOfWork
    @RolesAllowed("DELETE_USER")
    public SuccessResponse deleteUser(Long id) {
        try {
            AuthenticatedUser user = (AuthenticatedUser) securityContext.getUserPrincipal();
            UserValidationUtil.validateDifferentUser(user, id);

            return userService.deleteUser(id);
        } catch (IllegalArgumentException e) {
            throw new NotFoundException(
                    Response.status(Response.Status.NOT_FOUND)
                            .entity(new org.nath.sns.identity.model.Error()
                                    .code(Response.Status.NOT_FOUND.getStatusCode())
                                    .message(e.getMessage()))
                            .build());
        } catch (Exception e) {
            throw new WebApplicationException(
                    Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(new org.nath.sns.identity.model.Error()
                                    .code(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                                    .message("Internal server error: " + e.getMessage()))
                            .build());
        }
    }

    @Override
    @UnitOfWork
    @RolesAllowed("ADMIN")
    public List<User> getAllUsers() {
        try {
            return userService.getAllUsers();
        } catch (Exception e) {
            throw new WebApplicationException(
                    Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(new org.nath.sns.identity.model.Error()
                                    .code(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                                    .message("Internal server error: " + e.getMessage()))
                            .build());
        }
    }

    @Override
    @UnitOfWork
    @RolesAllowed("GET_USER")
    public User getUserById(Long id) {
        try {
            AuthenticatedUser user = (AuthenticatedUser) securityContext.getUserPrincipal();
            UserValidationUtil.validateDifferentUser(user, id);

            return userService.getUserById(id);
        } catch (IllegalArgumentException e) {
            throw new NotFoundException(
                    Response.status(Response.Status.NOT_FOUND)
                            .entity(new org.nath.sns.identity.model.Error()
                                    .code(Response.Status.NOT_FOUND.getStatusCode())
                                    .message(e.getMessage()))
                            .build());
        } catch (Exception e) {
            throw new WebApplicationException(
                    Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(new org.nath.sns.identity.model.Error()
                                    .code(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                                    .message("Internal server error: " + e.getMessage()))
                            .build());
        }
    }

    @Override
    @UnitOfWork
    @RolesAllowed("UPDATE_USER")
    public User updateUser(Long id, UpdateUserRequest updateUserRequest) {
        try {
            AuthenticatedUser user = (AuthenticatedUser) securityContext.getUserPrincipal();
            UserValidationUtil.validateDifferentUser(user, id);

            return userService.updateUser(id, updateUserRequest);
        } catch (IllegalArgumentException e) {
            throw new NotFoundException(
                    Response.status(Response.Status.NOT_FOUND)
                            .entity(new org.nath.sns.identity.model.Error()
                                    .code(Response.Status.NOT_FOUND.getStatusCode())
                                    .message(e.getMessage()))
                            .build());
        } catch (Exception e) {
            throw new WebApplicationException(
                    Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(new org.nath.sns.identity.model.Error()
                                    .code(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                                    .message("Internal server error: " + e.getMessage()))
                            .build());
        }
    }
}
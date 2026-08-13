package org.nath.sns.resource;

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
import java.util.List;

@Singleton
public class UsersResource implements UsersApi {

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
    public SuccessResponse deleteUser(Long id) {
        try {
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
    public User getUserById(Long id) {
        try {
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
    public User updateUser(Long id, UpdateUserRequest updateUserRequest) {
        try {
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
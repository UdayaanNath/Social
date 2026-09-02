package org.nath.sns.resource;

import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.nath.sns.identity.UserRolesApi;
import org.nath.sns.identity.model.CreateUserRoleRequest;
import org.nath.sns.identity.model.SuccessResponse;
import org.nath.sns.identity.model.UpdateUserRoleRequest;
import org.nath.sns.identity.model.UserRole;
import org.nath.sns.service.UserRoleService;

@Singleton
public class UserRolesResource implements UserRolesApi {

    private final UserRoleService userRoleService;

    @Inject
    public UserRolesResource(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    @Override
    @UnitOfWork
    public UserRole createUserRole(CreateUserRoleRequest request) {
        try {
            return userRoleService.createUserRole(request);
        } catch (Exception e) {
            throw internalServerError(e);
        }
    }

    @Override
    @UnitOfWork
    public SuccessResponse deleteUserRole(Long id) {
        try {
            return userRoleService.deleteUserRole(id);
        } catch (IllegalArgumentException e) {
            throw notFound(e);
        } catch (Exception e) {
            throw internalServerError(e);
        }
    }

    @Override
    @UnitOfWork
    public List<UserRole> getAllUserRoles(Long userId, String username, Long roleId, String roleName) {
        try {
            return userRoleService.getAllUserRoles(userId, username, roleId, roleName);
        } catch (Exception e) {
            throw internalServerError(e);
        }
    }

    @Override
    @UnitOfWork
    public UserRole getUserRoleById(Long id) {
        try {
            return userRoleService.getUserRoleById(id);
        } catch (IllegalArgumentException e) {
            throw notFound(e);
        } catch (Exception e) {
            throw internalServerError(e);
        }
    }

    @Override
    @UnitOfWork
    public UserRole updateUserRole(Long id, UpdateUserRoleRequest request) {
        try {
            return userRoleService.updateUserRole(id, request);
        } catch (IllegalArgumentException e) {
            throw notFound(e);
        } catch (Exception e) {
            throw internalServerError(e);
        }
    }

    private NotFoundException notFound(IllegalArgumentException e) {
        return new NotFoundException(Response.status(Response.Status.NOT_FOUND)
                .entity(new org.nath.sns.identity.model.Error()
                        .code(Response.Status.NOT_FOUND.getStatusCode())
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

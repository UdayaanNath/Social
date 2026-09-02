package org.nath.sns.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;
import org.nath.sns.dao.UserRoleDAO;
import org.nath.sns.entity.UserRoleEntity;
import org.nath.sns.identity.model.CreateUserRoleRequest;
import org.nath.sns.identity.model.SuccessResponse;
import org.nath.sns.identity.model.UpdateUserRoleRequest;
import org.nath.sns.identity.model.UserRole;
import org.nath.sns.identity.model.UserRoleStatus;

@Singleton
public class UserRoleService {

    private final UserRoleDAO userRoleDAO;

    @Inject
    public UserRoleService(UserRoleDAO userRoleDAO) {
        this.userRoleDAO = userRoleDAO;
    }

    public UserRole createUserRole(CreateUserRoleRequest request) {
        UserRoleEntity entity = new UserRoleEntity();
        entity.setUserId(request.getUserId());
        entity.setUsername(request.getUsername());
        entity.setRoleId(request.getRoleId());
        entity.setRoleName(request.getRoleName());
        entity.setStatus(request.getStatus() == null ? UserRoleStatus.ACTIVE : request.getStatus());
        return toApiModel(userRoleDAO.create(entity));
    }

    public List<UserRole> getAllUserRoles(Long userId, String username, Long roleId, String roleName) {
        return userRoleDAO.findAll(userId, username, roleId, roleName).stream()
                .map(this::toApiModel)
                .collect(Collectors.toList());
    }

    public UserRole getUserRoleById(Long id) {
        return toApiModel(requireUserRole(id));
    }

    public UserRole updateUserRole(Long id, UpdateUserRoleRequest request) {
        UserRoleEntity entity = requireUserRole(id);
        entity.setStatus(request.getStatus());
        return toApiModel(userRoleDAO.update(entity));
    }

    public SuccessResponse deleteUserRole(Long id) {
        requireUserRole(id);
        userRoleDAO.delete(id);
        return new SuccessResponse().message("User role deleted successfully");
    }

    private UserRoleEntity requireUserRole(Long id) {
        UserRoleEntity entity = userRoleDAO.findById(id);
        if (entity == null) {
            throw new IllegalArgumentException("User role not found with id: " + id);
        }
        return entity;
    }

    private UserRole toApiModel(UserRoleEntity entity) {
        return new UserRole()
                .id(entity.getId())
                .userId(entity.getUserId())
                .username(entity.getUsername())
                .roleId(entity.getRoleId())
                .roleName(entity.getRoleName())
                .status(entity.getStatus());
    }
}

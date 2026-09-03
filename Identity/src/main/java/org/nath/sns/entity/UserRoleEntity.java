package org.nath.sns.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.nath.sns.identity.model.UserRoleStatus;

@Entity
@Table(name = "user_roles")
public class UserRoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Column(name = "role_name", nullable = false, length = 50)
    private String roleName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private UserRoleStatus status;

    public UserRoleEntity() {
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Long getRoleId() { return roleId; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public UserRoleStatus getStatus() { return status; }
    public void setStatus(UserRoleStatus status) { this.status = status; }
}

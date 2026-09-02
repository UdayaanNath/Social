package org.nath.sns.dao;

import io.dropwizard.hibernate.AbstractDAO;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.nath.sns.entity.UserRoleEntity;

public class UserRoleDAO extends AbstractDAO<UserRoleEntity> {

    public UserRoleDAO(SessionFactory factory) {
        super(factory);
    }

    @UnitOfWork
    public UserRoleEntity create(UserRoleEntity userRoleEntity) {
        return persist(userRoleEntity);
    }

    @UnitOfWork
    public UserRoleEntity findById(Long id) {
        return get(id);
    }

    @UnitOfWork
    public List<UserRoleEntity> findAll(Long userId, String username, Long roleId, String roleName) {
        StringBuilder hql = new StringBuilder("FROM UserRoleEntity WHERE 1 = 1");
        if (userId != null) hql.append(" AND userId = :userId");
        if (username != null) hql.append(" AND username = :username");
        if (roleId != null) hql.append(" AND roleId = :roleId");
        if (roleName != null) hql.append(" AND roleName = :roleName");

        Query<UserRoleEntity> query = currentSession().createQuery(hql.toString(), UserRoleEntity.class);
        if (userId != null) query.setParameter("userId", userId);
        if (username != null) query.setParameter("username", username);
        if (roleId != null) query.setParameter("roleId", roleId);
        if (roleName != null) query.setParameter("roleName", roleName);
        return query.list();
    }

    @UnitOfWork
    public UserRoleEntity update(UserRoleEntity userRoleEntity) {
        return currentSession().merge(userRoleEntity);
    }

    @UnitOfWork
    public void delete(Long id) {
        UserRoleEntity userRoleEntity = findById(id);
        if (userRoleEntity != null) {
            currentSession().remove(userRoleEntity);
        }
    }
}

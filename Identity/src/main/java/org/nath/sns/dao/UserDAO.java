package org.nath.sns.dao;

import io.dropwizard.hibernate.AbstractDAO;
import io.dropwizard.hibernate.UnitOfWork;
import org.hibernate.SessionFactory;
import org.nath.sns.entity.UserEntity;

import java.util.List;

public class UserDAO extends AbstractDAO<UserEntity> {

    public UserDAO(SessionFactory factory) {
        super(factory);
    }

    @UnitOfWork
    public UserEntity create(UserEntity userEntity) {
        return (UserEntity) persist(userEntity);
    }

    @UnitOfWork
    public UserEntity findById(Long id) {
        return (UserEntity) get(id);
    }

    @UnitOfWork
    @SuppressWarnings("unchecked")
    public List<UserEntity> findAll() {
        return (List<UserEntity>) currentSession()
                .createQuery("FROM org.nath.sns.entity.UserEntity", UserEntity.class)
                .list();
    }

    @UnitOfWork
    public UserEntity update(UserEntity userEntity) {
        return (UserEntity) currentSession().merge(userEntity);
    }

    @UnitOfWork
    public void delete(Long id) {
        UserEntity userEntity = findById(id);
        if (userEntity != null) {
            currentSession().delete(userEntity);
        }
    }
}

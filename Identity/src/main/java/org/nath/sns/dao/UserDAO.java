package org.nath.sns.dao;

import io.dropwizard.hibernate.AbstractDAO;
import io.dropwizard.hibernate.UnitOfWork;
import org.hibernate.SessionFactory;
import org.nath.sns.entity.User;

public class UserDAO extends AbstractDAO<User> {

    public UserDAO(SessionFactory factory) {
        super(factory);
    }

    @UnitOfWork
    public User create(User user) {
        return (User) persist(user);
    }

    @UnitOfWork
    public User findById(Long id) {
        return (User) get(id);
    }
}

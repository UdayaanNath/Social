package org.nath.sns.dao;

import io.dropwizard.hibernate.AbstractDAO;
import io.dropwizard.hibernate.UnitOfWork;
import org.hibernate.SessionFactory;
import org.nath.sns.entity.User;

import java.util.List;

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

    @UnitOfWork
    @SuppressWarnings("unchecked")
    public List<User> findAll() {
        return (List<User>) currentSession()
                .createQuery("FROM org.nath.sns.entity.User", User.class)
                .list();
    }

    @UnitOfWork
    public User update(User user) {
        return (User) currentSession().merge(user);
    }

    @UnitOfWork
    public void delete(Long id) {
        User user = findById(id);
        if (user != null) {
            currentSession().delete(user);
        }
    }
}

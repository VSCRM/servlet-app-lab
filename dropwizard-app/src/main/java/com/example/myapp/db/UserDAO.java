package com.example.myapp.db;

import com.example.myapp.core.User;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import java.util.List;
import java.util.Optional;

public class UserDAO extends AbstractDAO<User> {
    public UserDAO(SessionFactory factory) {
        super(factory);
    }

    public List<User> findAll() {
        return list(namedTypedQuery("com.example.myapp.core.User.findAll"));
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(get(id));
    }

    public User create(User user) {
        return persist(user);
    }

    public void delete(User user) {
        currentSession().delete(user);
    }
}
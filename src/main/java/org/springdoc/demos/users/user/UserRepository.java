package org.springdoc.demos.users.user;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    private final List<User> users = new ArrayList<>();

    public List<User> findAll() {
        return users;
    }

    @PostConstruct
    private void init() {
        users.add(new User(1,"Badr","badr@gmail.com"));
    }
}

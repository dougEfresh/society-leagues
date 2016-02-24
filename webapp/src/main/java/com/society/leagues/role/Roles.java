package com.society.leagues.role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class Roles {
    public final String USER;
    public final String ADMIN;

    @Autowired
    public Roles(Environment env) {
        USER = env.getProperty("stormpath.authorized.group.user");
        ADMIN = env.getProperty("stormpath.authorized.group.admin");
    }
}

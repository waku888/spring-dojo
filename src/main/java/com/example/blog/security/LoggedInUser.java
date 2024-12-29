package com.example.blog.security;

import org.springframework.security.core.userdetails.User;

import java.util.List;

public class LoggedInUser extends User {

    private final long userid;

    public LoggedInUser(long userid,
                        String username,
                        String password,
                        boolean enabled
    ) {
        super(username, password, enabled, true, true, true, List.of() );
        this.userid = userid;
    }
}

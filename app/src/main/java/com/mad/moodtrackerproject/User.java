package com.mad.moodtrackerproject;

import java.io.Serializable;

public class User implements Serializable {
    public User() {
        userId = "";
        name = "";
        email = "";
        password = "";
    }
    public User(String userId, String name, String email, String password) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String userId;
    public String name;
    public String email;
    public String password;
}

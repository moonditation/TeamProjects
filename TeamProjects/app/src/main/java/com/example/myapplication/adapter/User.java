package com.example.myapplication.adapter;

import java.util.List;

public class User {
    String name;
    String id;
    List<User> friends;

    float reliability;

    public User(String name, String id, float reliability){
        this.name = name;
        this.id = id;
        this.reliability = reliability;
    }
    public User(String name, String id){
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

}

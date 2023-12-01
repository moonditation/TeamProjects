package com.example.myapplication.adapter;

import java.util.List;

public class User {
    String name;
    String id;
    List<User> friends;
    List<Promise> promises;

    float reliability;

    public User(String name, String id, float reliability){
        this.name = name;
        this.id = id;
        this.reliability = reliability;
    }
    public User(String name, String id, float reliability, List<User> friends, List<Promise> promises){
        this.name = name;
        this.id = id;
        this.reliability = reliability;
        this.friends = friends;
        this.promises = promises;
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

    public float getReliability(){ return reliability; }

}

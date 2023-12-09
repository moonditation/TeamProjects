package com.example.myapplication.adapter;

import java.util.List;

public class User {
    String name;
    String id;
    String uid;
    List<User> friends;
    List<Promise> promises;

    float reliability;

    public User(String name, String id, float reliability, String uid){
        this.name = name;
        this.id = id;
        this.reliability = reliability;
        this.uid = uid;
    }
    public User(String name, String id, float reliability, List<User> friends, List<Promise> promises){
        this.name = name;
        this.id = id;
        this.reliability = reliability;
        this.friends = friends;
        this.promises = promises;
    }
    public User(String name, String id, String uid){
        this.name = name;
        this.id = id;
        this.uid = uid;
    }


    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public float getReliability(){ return reliability; }

}

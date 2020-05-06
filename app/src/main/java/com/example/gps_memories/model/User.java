package com.example.gps_memories.model;

public class User {

    private String username;
    private String id;
    private String imageURL;

    private String email;

    public User(String id, String username, String email , String imageURL) {

        this.id = id;
        this.username = username;
        this.email=email;
        this.imageURL = imageURL;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}

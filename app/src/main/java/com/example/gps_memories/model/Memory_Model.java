package com.example.gps_memories.model;

import java.util.List;

public class Memory_Model {
    
    String id;

    String userid;

    String title;

    String description;

    String time;

    String address;

    double latitude;

    double longitude;

    List<String> photos;

    public Memory_Model() {
    }

    public Memory_Model(String id, String userid, String title, String description, String time, String address, double latitude,
                        double longitude, List<String> photos) {

        this.id = id;
        this.userid=userid;
        this.title = title;
        this.description = description;
        this.time = time;
        this.address = address;
        this.latitude=latitude;
        this.longitude=longitude;
        this.photos = photos;

    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}

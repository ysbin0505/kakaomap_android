package com.example.spotapp.client;

import com.google.gson.annotations.SerializedName;

public class LocationData {

    @SerializedName("id")
    private Long id;
    @SerializedName("latitude")
    private Double latitude;

    @SerializedName("longitude")
    private Double longitude;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @SerializedName("title")
    private String title;

    @SerializedName("address")
    private String address;

    @SerializedName("description")
    private String description;

    @SerializedName("image")
    private String image;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocationData(Double latitude, Double longitude, String title, String address, String description, String image) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.address = address;
        this.description = description;
        this.image = image;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
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
}

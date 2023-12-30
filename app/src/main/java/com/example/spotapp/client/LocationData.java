package com.example.spotapp.client;

public class LocationData {
    private Double latitude;
    private Double longitude;
    private String title;
    private String address;
    private String description;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocationData(Double latitude, Double longitude, String title, String address, String description) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.address = address;
        this.description = description;
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

package com.example.wander_app.models;

public class LocationPDF {
    private String locationName;
    private String locationDescription;
    private String locationPicUrl;

    public LocationPDF(String locationName, String locationDescription, String locationPicUrl) {
        this.locationName = locationName;
        this.locationDescription = locationDescription;
        this.locationPicUrl = locationPicUrl;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }

    public String getLocationPicUrl() {
        return locationPicUrl;
    }

    public void setLocationPicUrl(String locationPicUrl) {
        this.locationPicUrl = locationPicUrl;
    }
}

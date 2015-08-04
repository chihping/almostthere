package com.chihping.almostthere.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by klai on 8/3/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Guest {

    private String displayName;
    private String address;
    private String profileImageURL;
    private int eta;
    private double lat;
    private double lon;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getEta() {
        return eta;
    }

    public void setEta(int eta) {
        this.eta = eta;
    }

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public void setProfileImageURL(String profileImageURL) {
        this.profileImageURL = profileImageURL;
    }
}

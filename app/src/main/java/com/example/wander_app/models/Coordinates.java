package com.example.wander_app.models;

public class Coordinates {
    private float lon;
    private float lan;

    public Coordinates(float lon, float lan) {
        this.lan = lan;
        this.lon = lon;
    }

    public float getLon() {
        return lon;
    }

    public float getLan() {
        return lan;
    }
}

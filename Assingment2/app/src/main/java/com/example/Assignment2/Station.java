package com.example.hack2;

class Station {
    private String name;
    private double lat;
    private double lng;

    public void setName(String newName) {
        this.name = newName;
    }
    public void setLat(double latitude) {
        this.lat = latitude;
    }
    public void setLong(double longitude) {
        this.lng = longitude;
    }
    public String getName() {
        return name;
    }
    public double getLat() { return lat; }
    public double getLong() { return lng; }
    public String toString() { return name + " (Lat: " + lat + ", Long: " + lng + ")\n"; }
}
